/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.resource.PaymentGroup;
import com.payoneer.checkout.ui.model.PaymentSession;

@RunWith(RobolectricTestRunner.class)
public class PaymentSessionBuilderTest {

    @Test(expected = IllegalStateException.class)
    public void build_missingListResult() throws PaymentException {
        Map<String, PaymentGroup> paymentGroups = new HashMap<>();
        new PaymentSessionBuilder()
            .setPaymentGroups(paymentGroups)
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void build_missingPaymentGroups() throws PaymentException {
        ListResult listResult = new ListResult();
        new PaymentSessionBuilder()
            .setListResult(listResult)
            .build();
    }

    @Test
    public void build_successful() throws PaymentException {
        ListResult listResult = new ListResult();
        Map<String, PaymentGroup> paymentGroups = new HashMap<>();
        PaymentSession session = new PaymentSessionBuilder()
            .setListResult(listResult)
            .setPaymentGroups(paymentGroups)
            .build();
        assertNotNull(session);
    }
}