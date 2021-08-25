/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.localization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
public final class LocalizationTest {

    @Test
    public void setInstance() {
        Localization.setInstance(new Localization(null, null));
        assertNotNull(Localization.getInstance());
    }

    @Test
    public void setLocalizations() {
        LocalizationHolder shared = createMapLocalizationHolder("sharedKey", "sharedValue", 5);
        Map<String, LocalizationHolder> networks = new HashMap<>();

        networks.put("NETWORK", createNetworkLocalizationHolder("networkKey", "networkValue", 5, shared));
        Localization loc = new Localization(shared, networks);

        assertEquals("sharedValue2", loc.getSharedTranslation("sharedKey2"));
        assertNull(loc.getSharedTranslation("foo"));
    }

    @Test
    public void getNetworkLocalizations() {
        Context context = ApplicationProvider.getApplicationContext();
        LocalizationHolder fallback = new LocalLocalizationHolder(context);

        Map<String, LocalizationHolder> networks = new HashMap<>();
        networks.put("VISA", createNetworkLocalizationHolder("VISA-Key", "VISA-Value", 5, fallback));
        networks.put("MASTERCARD", createNetworkLocalizationHolder("MASTERCARD-Key", "MASTERCARD-Value", 5, fallback));

        Localization loc = new Localization(null, networks);
        assertEquals("VISA-Value2", loc.getNetworkTranslation("VISA", "VISA-Key2"));
        assertNull(loc.getNetworkTranslation("MASTERCARD", "VISA-Key2"));

        assertEquals("Cancel", loc.getNetworkTranslation("VISA", LocalizationKey.BUTTON_CANCEL));
        assertNull(loc.getNetworkTranslation("VISA", "foo"));
    }

    @Test
    public void getSharedLocalizations() {
        Context context = ApplicationProvider.getApplicationContext();
        LocalizationHolder shared = new LocalLocalizationHolder(context);

        Localization loc = new Localization(shared, null);
        assertEquals("OK", loc.getSharedTranslation(LocalizationKey.BUTTON_OK));
        assertNull(loc.getSharedTranslation("foo"));
    }

    /**
     * Create a map based localization holder with one key value pair
     *
     * @param mapKey template for the key, index will be appended
     * @param mapValue template for the value, index will be appended
     * @param nrTranslations the number of translation key/value pairs that should be added to the holder
     * @return the newly created MapLocalizationHolder
     */
    public static MapLocalizationHolder createMapLocalizationHolder(String mapKey, String mapValue, int nrTranslations) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < nrTranslations; i++) {
            map.put(mapKey + i, mapValue + i);
        }
        return new MapLocalizationHolder(map);
    }

    /**
     * Create a network localization holder for testing,
     *
     * @param mapKey template for the key, index will be appended
     * @param mapValue template for teh value, index will be appended
     * @param nrTranslations the number of translation key/value pairs that should be added to the holder
     * @param fallback the localization holder used as fallback
     * @return newly created MultiLocalizationHolder
     */
    public static MultiLocalizationHolder createNetworkLocalizationHolder(String mapKey, String mapValue, int nrTranslations,
        LocalizationHolder fallback) {
        LocalizationHolder network = createMapLocalizationHolder(mapKey, mapValue, nrTranslations);
        return new MultiLocalizationHolder(network, fallback);
    }
}
