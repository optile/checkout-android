/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.model.NetworkOperationType;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class PaymentSessionServiceTest {

    @Test
    public void isSupportedOperationType() {
        Context context = ApplicationProvider.getApplicationContext();
        PaymentSessionService service = new PaymentSessionService(context);
        assertTrue(service.isSupportedNetworkOperationType(NetworkOperationType.CHARGE));
        assertTrue(service.isSupportedNetworkOperationType(NetworkOperationType.PRESET));
        assertTrue(service.isSupportedNetworkOperationType(NetworkOperationType.UPDATE));
        assertFalse(service.isSupportedNetworkOperationType(NetworkOperationType.ACTIVATION));
        assertFalse(service.isSupportedNetworkOperationType(NetworkOperationType.PAYOUT));
        assertFalse(service.isSupportedNetworkOperationType(null));
    }
}
