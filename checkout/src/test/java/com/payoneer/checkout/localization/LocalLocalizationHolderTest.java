/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.localization;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public class LocalLocalizationHolderTest {

    @Test
    public void loadLocalLocalization() {
        Context context = ApplicationProvider.getApplicationContext();
        LocalLocalizationHolder holder = new LocalLocalizationHolder(context);
        assertNotNull(holder.translate(LocalizationKey.BUTTON_OK));
        assertNotNull(holder.translate(LocalizationKey.BUTTON_CANCEL));
        assertNotNull(holder.translate(LocalizationKey.BUTTON_RETRY));
        assertNotNull(holder.translate(LocalizationKey.BUTTON_UPDATE_ACCOUNT));

        assertNotNull(holder.translate(LocalizationKey.ERROR_CONNECTION_TEXT));
        assertNotNull(holder.translate(LocalizationKey.ERROR_DEFAULT_TEXT));

        assertNotNull(holder.translate(LocalizationKey.LIST_HEADER_ACCOUNTS_UPDATE));
        assertNotNull(holder.translate(LocalizationKey.LIST_HEADER_NETWORKS_UPDATE));

        assertNotNull(holder.translate(LocalizationKey.NETWORKS_REGISTRATION_LABEL));
    }
}
