/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.page;

import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_ERROR;
import static com.payoneer.checkout.ui.PaymentActivityResult.RESULT_CODE_PROCEED;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.ui.PaymentResult;
import com.payoneer.checkout.ui.service.NetworkService;
import com.payoneer.checkout.ui.service.NetworkServiceLookup;
import com.payoneer.checkout.util.PaymentResultHelper;

/**
 * Base class for payment presenters
 */
abstract class BasePaymentPresenter {

    final static int STOPPED = 0;
    final static int STARTED = 1;
    final static int PROCESS = 2;

    final BasePaymentView view;
    final String listUrl;
    int state;

    /**
     * Construct a new BasePaymentPresenter
     *
     * @param listUrl self URL of the ListResult
     */
    BasePaymentPresenter(String listUrl, BasePaymentView view) {
        this.listUrl = listUrl;
        this.view = view;
    }

    boolean checkState(int state) {
        return this.state == state;
    }

    void setState(int state) {
        this.state = state;
    }

    void closeWithProceedCode(PaymentResult result) {
        view.setPaymentResult(RESULT_CODE_PROCEED, result);
        view.close();
    }

    void closeWithErrorCode(String message) {
        PaymentResult result = PaymentResultHelper.fromErrorMessage(message);
        closeWithErrorCode(result);
    }

    void closeWithErrorCode(PaymentResult result) {
        view.setPaymentResult(RESULT_CODE_ERROR, result);
        view.close();
    }

    NetworkService loadNetworkService(String code, String paymentMethod) throws PaymentException {
        NetworkService service = NetworkServiceLookup.createService(view.getActivity(), code, paymentMethod);
        if (service == null) {
            throw new PaymentException("Missing NetworkService for: " + code + ", " + paymentMethod);
        }
        return service;
    }
}
