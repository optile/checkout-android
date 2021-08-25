/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import static com.payoneer.checkout.localization.LocalizationKey.CHARGE_INTERRUPTED;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_PROCEED;

import java.util.Objects;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.localization.InteractionMessage;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.model.ErrorInfo;
import com.payoneer.checkout.model.Interaction;
import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.redirect.RedirectRequest;
import com.payoneer.checkout.redirect.RedirectService;
import com.payoneer.checkout.ui.PaymentResult;
import com.payoneer.checkout.ui.PaymentUI;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment;
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.service.NetworkService;
import com.payoneer.checkout.ui.service.NetworkServiceListener;
import com.payoneer.checkout.ui.service.PaymentSessionListener;
import com.payoneer.checkout.ui.service.PaymentSessionService;
import com.payoneer.checkout.util.PaymentResultHelper;

import android.content.Context;

/**
 * The ChargePaymentPresenter takes care of posting the operation to the Payment API.
 * First this presenter will load the list, checks if the operation is present and then post the operation to the Payment API.
 */
final class ChargePaymentPresenter extends BasePaymentPresenter implements PaymentSessionListener, NetworkServiceListener {

    private final PaymentSessionService sessionService;
    private PaymentSession session;
    private Operation operation;
    private NetworkService networkService;
    private RedirectRequest redirectRequest;

    /**
     * Create a new ChargePaymentPresenter
     *
     * @param view the BasePaymentView displaying payment information
     */
    ChargePaymentPresenter(BasePaymentView view) {
        super(PaymentUI.getInstance().getListUrl(), view);
        sessionService = new PaymentSessionService(view.getActivity());
        sessionService.setListener(this);
    }

    void onStart(Operation operation) {
        this.operation = operation;
        setState(STARTED);

        if (redirectRequest != null) {
            handleRedirectRequest(redirectRequest);
            redirectRequest = null;
        } else {
            loadPaymentSession();
        }
    }

    void onStop() {
        setState(STOPPED);
        sessionService.stop();
        if (networkService != null) {
            networkService.stop();
        }
    }

    @Override
    public void onPaymentSessionSuccess(PaymentSession session) {
        ListResult listResult = session.getListResult();
        Interaction interaction = listResult.getInteraction();

        if (Objects.equals(InteractionCode.PROCEED, interaction.getCode())) {
            handleLoadSessionProceed(session);
        } else {
            ErrorInfo errorInfo = new ErrorInfo(listResult.getResultInfo(), interaction);
            PaymentResult result = new PaymentResult(errorInfo, null);
            closeWithErrorCode(result);
        }
    }

    @Override
    public void onPaymentSessionError(Throwable cause) {
        handleLoadingError(cause);
    }

    @Override
    public void showProgress(boolean visible) {
        view.showProgress(visible);
    }

    @Override
    public void onProcessPaymentResult(int resultCode, PaymentResult result) {
        setState(STARTED);
        switch (resultCode) {
            case RESULT_CODE_PROCEED:
                closeWithProceedCode(result);
                break;
            case RESULT_CODE_ERROR:
                handleProcessPaymentError(result);
                break;
        }
    }

    @Override
    public void onDeleteAccountResult(int resultCode, PaymentResult result) {
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

    boolean onBackPressed() {
        view.showWarningMessage(Localization.translate(CHARGE_INTERRUPTED));
        return true;
    }

    private void handleRedirectRequest(RedirectRequest redirectRequest) {
        networkService.onRedirectResult(redirectRequest, RedirectService.getRedirectResult());
    }

    private void handleLoadSessionProceed(PaymentSession session) {
        if (!session.containsOperationLink(operation.getURL())) {
            closeWithErrorCode("operation not found in ListResult");
            return;
        }
        try {
            this.session = session;
            networkService = loadNetworkService(operation.getNetworkCode(), operation.getPaymentMethod());
            networkService.setListener(this);
            processPayment(operation);
        } catch (PaymentException e) {
            closeWithErrorCode(PaymentResultHelper.fromThrowable(e));
        }
    }

    private void handleLoadingError(Throwable cause) {
        PaymentResult result = PaymentResultHelper.fromThrowable(InteractionCode.ABORT, cause);

        if (result.isNetworkFailure()) {
            handleLoadingNetworkFailure(result);
        } else {
            closeWithErrorCode(result);
        }
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

    private void handleProcessPaymentError(PaymentResult result) {
        if (result.isNetworkFailure()) {
            handleProcessNetworkFailure(result);
            return;
        }
        Interaction interaction = result.getInteraction();
        switch (interaction.getCode()) {
            case InteractionCode.TRY_OTHER_ACCOUNT:
            case InteractionCode.TRY_OTHER_NETWORK:
            case InteractionCode.RETRY:
                showErrorAndCloseWithErrorCode(result);
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

    private void processPayment(Operation operation) {
        setState(PROCESS);
        networkService.processPayment(operation);
    }

    private void showErrorAndCloseWithErrorCode(PaymentResult result) {
        Interaction interaction = result.getInteraction();
        String operationType = session != null ? session.getListOperationType() : null;
        InteractionMessage message = new InteractionMessage(interaction, operationType);

        view.setPaymentResult(RESULT_CODE_ERROR, result);
        PaymentDialogFragment.PaymentDialogListener listener = new PaymentDialogFragment.PaymentDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                view.close();
            }

            @Override
            public void onNegativeButtonClicked() {
                view.close();
            }

            @Override
            public void onDismissed() {
                view.close();
            }
        };
        view.showInteractionDialog(message, listener);
    }

    private void loadPaymentSession() {
        this.session = null;
        view.showProgress(true);
        sessionService.loadPaymentSession(listUrl, view.getActivity());
    }
}
