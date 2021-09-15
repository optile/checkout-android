/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import static com.payoneer.checkout.model.InteractionCode.PROCEED;
import static com.payoneer.checkout.model.InteractionCode.RELOAD;
import static com.payoneer.checkout.model.InteractionCode.RETRY;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_ACCOUNT;
import static com.payoneer.checkout.model.InteractionCode.TRY_OTHER_NETWORK;
import static com.payoneer.checkout.model.InteractionReason.OK;
import static com.payoneer.checkout.model.InteractionReason.PENDING;
import static com.payoneer.checkout.model.NetworkOperationType.CHARGE;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_CODE;
import static com.payoneer.checkout.redirect.RedirectService.INTERACTION_REASON;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_PROCEED;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.form.DeleteAccount;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.OperationResult;
import com.payoneer.checkout.model.Parameter;
import com.payoneer.checkout.model.Redirect;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;
import com.payoneer.checkout.ui.PaymentActivityResult;
import com.payoneer.checkout.ui.PaymentResult;
import com.payoneer.checkout.ui.PaymentUI;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.list.PaymentListListener;
import com.payoneer.checkout.ui.model.AccountCard;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.model.PresetCard;
import com.payoneer.checkout.ui.service.NetworkService;
import com.payoneer.checkout.ui.service.NetworkServiceListener;
import com.payoneer.checkout.ui.service.PaymentSessionListener;
import com.payoneer.checkout.ui.service.PaymentSessionService;
import com.payoneer.checkout.ui.widget.FormWidget;
import com.payoneer.checkout.util.PaymentResultHelper;
import com.payoneer.checkout.util.PaymentUtils;

import android.content.Context;
import android.text.TextUtils;

/**
 * The PaymentListPresenter implementing the presenter part of the MVP
 */
final class PaymentListPresenter extends BasePaymentPresenter
    implements PaymentSessionListener, NetworkServiceListener, PaymentListListener {

    private final static int CHARGEPAYMENT_REQUEST_CODE = 1;
    private final PaymentSessionService sessionService;
    private final PaymentListView listView;

    private Operation operation;
    private DeleteAccount deleteAccount;

    private PaymentSession session;
    private PaymentActivityResult activityResult;
    private NetworkService networkService;
    private RedirectRequest redirectRequest;

    /**
     * Create a new PaymentListPresenter
     *
     * @param view The PaymentListView displaying the payment list
     */
    PaymentListPresenter(PaymentListView view) {
        super(PaymentUI.getInstance().getListUrl(), view);
        this.listView = view;

        sessionService = new PaymentSessionService(view.getActivity());
        sessionService.setListener(this);
    }

    void onStart() {
        setState(STARTED);

        if (redirectRequest != null) {
            handleRedirectRequest(redirectRequest);
            redirectRequest = null;
        } else if (activityResult != null) {
            handlePaymentActivityResult(activityResult);
            activityResult = null;
        } else if (session == null) {
            loadPaymentSession();
        } else {
            showPaymentSession();
        }
    }

    void onStop() {
        setState(STOPPED);
        sessionService.stop();

        if (networkService != null) {
            networkService.stop();
        }
    }

    public void onRefresh(boolean hasUserInputData) {
        if (!checkState(STARTED)) {
            return;
        }
        if (!hasUserInputData) {
            loadPaymentSession();
            return;
        }
        PaymentDialogListener listener = new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
            }

            @Override
            public void onDismissed() {
            }
        };
        view.showRefreshAccountDialog(listener);
    }

    void setPaymentActivityResult(PaymentActivityResult activityResult) {
        this.activityResult = activityResult;
    }

    @Override
    public void onActionClicked(PaymentCard paymentCard, Map<String, FormWidget> widgets) {
        if (!checkState(STARTED)) {
            return;
        }
        if (paymentCard instanceof PresetCard) {
            onPresetCardSelected((PresetCard) paymentCard);
            return;
        }
        processPaymentCard(paymentCard, widgets);
    }

    @Override
    public void onDeleteClicked(PaymentCard paymentCard) {
        if (!checkState(STARTED)) {
            return;
        }
        if (!(paymentCard instanceof AccountCard)) {
            return;
        }
        view.showDeleteAccountDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                deleteAccountCard((AccountCard) paymentCard);
            }

            @Override
            public void onNegativeButtonClicked() {
            }

            @Override
            public void onDismissed() {
            }
        }, paymentCard.getTitle());
    }

    @Override
    public void onHintClicked(String networkCode, String type) {
        view.showHintDialog(networkCode, type, null);
    }

    @Override
    public void onPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(interaction.getCode(), PROCEED)) {
            handleLoadPaymentSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            closeWithErrorCode(new PaymentResult(errorInfo));
        }
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        PaymentResult result = PaymentResultHelper.fromThrowable(cause);
        if (result.isNetworkFailure()) {
            handleLoadingNetworkFailure(result);
        } else {
            closeWithErrorCode(result);
        }
    }

    @Override
    public void showProgress(boolean visible) {
        view.showProgress(visible);
    }

    @Override
    public void onDeleteAccountResult(int resultCode, PaymentResult result) {
        if (result.isNetworkFailure()) {
            handleDeleteNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        setState(STARTED);

        switch (interaction.getCode()) {
            case PROCEED:
            case RELOAD:
                loadPaymentSession();
                break;
            case RETRY:
                showMessageAndPaymentSession(InteractionMessage.fromDeleteFlow(interaction));
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                showMessageAndReloadPaymentSession(InteractionMessage.fromDeleteFlow(interaction));
                break;
            default:
                closeWithErrorCode(result);
        }
    }

    @Override
    public void redirect(RedirectRequest redirectRequest) throws PaymentException {
        Context context = view.getActivity();
        if (!RedirectService.supports(context, redirectRequest)) {
            throw new PaymentException("The Redirect payment method is not supported by the Android-SDK");
        }
        this.redirectRequest = redirectRequest;
        view.showProgress(false);
        RedirectService.redirect(context, redirectRequest);
    }

    @Override
    public void onProcessPaymentResult(int resultCode, PaymentResult result) {
        setState(STARTED);
        if (UPDATE.equals(session.getListOperationType())) {
            handleUpdatePaymentResult(resultCode, result);
        } else {
            handleProcessPaymentResult(resultCode, result);
        }
    }

    private void handleUpdatePaymentResult(int resultCode, PaymentResult result) {
        switch (resultCode) {
            case RESULT_CODE_PROCEED:
                handleUpdatePaymentProceed(result);
                break;
            case RESULT_CODE_ERROR:
                handleUpdatePaymentError(result);
                break;
            default:
                showPaymentSession();
        }
    }

    private void handleUpdatePaymentProceed(PaymentResult result) {
        Interaction interaction = result.getInteraction();
        switch (interaction.getReason()) {
            case PENDING:
                showMessageAndResetPaymentSession(createInteractionMessage(interaction, session));
                break;
            case OK:
                loadPaymentSession();
                break;
            default:
                closeWithProceedCode(result);
        }
    }

    private void handleUpdatePaymentError(PaymentResult result) {
        if (result.isNetworkFailure()) {
            handleProcessNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case RELOAD:
                loadPaymentSession();
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
            case RETRY:
                showMessageAndReloadPaymentSession(createInteractionMessage(interaction, session));
                break;
            default:
                closeWithErrorCode(result);
        }
    }

    private void handleProcessPaymentResult(int resultCode, PaymentResult result) {
        switch (resultCode) {
            case RESULT_CODE_PROCEED:
                closeWithProceedCode(result);
                break;
            case RESULT_CODE_ERROR:
                handleProcessPaymentError(result);
                break;
            default:
                showPaymentSession();
        }
    }

    private void handleProcessPaymentError(PaymentResult result) {
        if (result.isNetworkFailure()) {
            handleProcessNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case RELOAD:
                loadPaymentSession();
                break;
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                showMessageAndReloadPaymentSession(createInteractionMessage(interaction, session));
                break;
            case RETRY:
                showMessageAndPaymentSession(createInteractionMessage(interaction, session));
                break;
            default:
                closeWithErrorCode(result);
        }
    }

    private void handleProcessNetworkFailure(final PaymentResult result) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                processPayment(operation);
            }

            @Override
            public void onNegativeButtonClicked() {
                closeWithErrorCode(result);
            }

            @Override
            public void onDismissed() {
                closeWithErrorCode(result);
            }
        });
    }

    private void handleRedirectRequest(RedirectRequest redirectRequest) {
        setState(PROCESS);
        OperationResult result = RedirectService.getRedirectResult();
        networkService.onRedirectResult(redirectRequest, result);
    }

    private void handleLoadPaymentSessionProceed(PaymentSession session) {
        if (session.isEmpty()) {
            closeWithErrorCode("There are no payment methods available");
            return;
        }
        this.session = session;
        showPaymentSession();
    }

    private void handleLoadingNetworkFailure(final PaymentResult result) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                closeWithErrorCode(result);
            }

            @Override
            public void onDismissed() {
                closeWithErrorCode(result);
            }
        });
    }

    private void handlePaymentActivityResult(PaymentActivityResult activityResult) {
        if (this.session == null) {
            closeWithErrorCode("Missing cached PaymentSession in PaymentListPresenter");
            return;
        }
        int requestCode = activityResult.getRequestCode();
        if (requestCode == CHARGEPAYMENT_REQUEST_CODE) {
            handleChargeActivityResult(activityResult);
        }
    }

    private void handleDeleteNetworkFailure(final PaymentResult result) {
        view.showConnectionErrorDialog(new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                deleteAccount(deleteAccount);
            }

            @Override
            public void onNegativeButtonClicked() {
                showPaymentSession();
            }

            @Override
            public void onDismissed() {
                showPaymentSession();
            }
        });
    }

    private void processPaymentCard(PaymentCard paymentCard, Map<String, FormWidget> widgets) {
        try {
            operation = createOperation(paymentCard, widgets);
            if (CHARGE.equals(operation.getOperationType())) {
                listView.showChargePaymentScreen(CHARGEPAYMENT_REQUEST_CODE, operation);
            } else {
                networkService = loadNetworkService(paymentCard.getNetworkCode(), paymentCard.getPaymentMethod());
                networkService.setListener(this);

                processPayment(operation);
            }
        } catch (PaymentException e) {
            closeWithErrorCode(PaymentResultHelper.fromThrowable(e));
        }
    }

    private void deleteAccountCard(AccountCard card) {
        try {
            networkService = loadNetworkService(card.getNetworkCode(), card.getPaymentMethod());
            networkService.setListener(this);

            URL url = card.getLink("self");
            deleteAccount = new DeleteAccount(url, session.getListOperationType());
            deleteAccount(deleteAccount);
        } catch (PaymentException e) {
            closeWithErrorCode(PaymentResultHelper.fromThrowable(e));
        }
    }

    private void processPayment(Operation operation) {
        setState(PROCESS);
        networkService.processPayment(operation);
    }

    private void deleteAccount(DeleteAccount account) {
        setState(PROCESS);
        networkService.deleteAccount(account);
    }

    private Operation createOperation(PaymentCard card, Map<String, FormWidget> widgets) throws PaymentException {
        URL url = card.getOperationLink();
        Operation operation = new Operation(card.getNetworkCode(), card.getPaymentMethod(), card.getOperationType(), url);

        for (FormWidget widget : widgets.values()) {
            widget.putValue(operation);
        }
        return operation;
    }

    /**
     * The Charge result is received from the ChargePaymentActivity. Error messages are not displayed by this presenter since
     * the ChargePaymentActivity has taken care of displaying error and warning messages.
     *
     * @param paymentActivityResult result received after a charge has been performed
     */
    private void handleChargeActivityResult(PaymentActivityResult paymentActivityResult) {
        switch (paymentActivityResult.getResultCode()) {
            case RESULT_CODE_ERROR:
                handleChargeError(paymentActivityResult);
                break;
            case RESULT_CODE_PROCEED:
                view.passOnActivityResult(paymentActivityResult);
                break;
            default:
                showPaymentSession();
        }
    }

    private void handleChargeError(PaymentActivityResult paymentActivityResult) {
        Interaction interaction = paymentActivityResult.getPaymentResult().getInteraction();
        switch (interaction.getCode()) {
            case RELOAD:
            case TRY_OTHER_ACCOUNT:
            case TRY_OTHER_NETWORK:
                loadPaymentSession();
                break;
            case RETRY:
                showPaymentSession();
                break;
            default:
                view.passOnActivityResult(paymentActivityResult);
        }
    }

    private void onPresetCardSelected(PresetCard card) {
        Redirect redirect = card.getPresetAccount().getRedirect();
        List<Parameter> parameters = redirect.getParameters();

        String code = PaymentUtils.getParameterValue(INTERACTION_CODE, parameters);
        String reason = PaymentUtils.getParameterValue(INTERACTION_REASON, parameters);
        if (TextUtils.isEmpty(code) || TextUtils.isEmpty(reason)) {
            closeWithErrorCode("Missing Interaction code and reason inside PresetAccount.redirect");
            return;
        }
        OperationResult result = new OperationResult();
        result.setResultInfo("PresetAccount selected");
        result.setInteraction(new Interaction(code, reason));
        result.setRedirect(redirect);
        closeWithProceedCode(new PaymentResult(result));
    }

    private void loadPaymentSession() {
        this.session = null;
        listView.clearPaymentList();
        view.showProgress(true);
        sessionService.loadPaymentSession(listUrl, view.getActivity());
    }

    private void showMessageAndResetPaymentSession(InteractionMessage message) {
        view.showInteractionDialog(message, null);
        listView.clearPaymentList();
        listView.showPaymentSession(session);
    }

    private void showMessageAndReloadPaymentSession(InteractionMessage message) {
        PaymentDialogListener listener = new PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onNegativeButtonClicked() {
                loadPaymentSession();
            }

            @Override
            public void onDismissed() {
                loadPaymentSession();
            }
        };
        view.showInteractionDialog(message, listener);
    }

    private void showMessageAndPaymentSession(InteractionMessage message) {
        view.showInteractionDialog(message, null);
        listView.showPaymentSession(session);
    }

    private void showPaymentSession() {
        listView.showPaymentSession(session);
    }
}

