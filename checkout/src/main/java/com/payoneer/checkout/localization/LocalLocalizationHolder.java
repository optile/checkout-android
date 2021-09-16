/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.localization;

import static com.payoneer.checkout.localization.LocalizationKey.BUTTON_CANCEL;
import static com.payoneer.checkout.localization.LocalizationKey.BUTTON_OK;
import static com.payoneer.checkout.localization.LocalizationKey.BUTTON_REFRESH;
import static com.payoneer.checkout.localization.LocalizationKey.BUTTON_RETRY;
import static com.payoneer.checkout.localization.LocalizationKey.BUTTON_UPDATE_ACCOUNT;
import static com.payoneer.checkout.localization.LocalizationKey.ERROR_CONNECTION_TEXT;
import static com.payoneer.checkout.localization.LocalizationKey.ERROR_CONNECTION_TITLE;
import static com.payoneer.checkout.localization.LocalizationKey.ERROR_DEFAULT_TEXT;
import static com.payoneer.checkout.localization.LocalizationKey.ERROR_DEFAULT_TITLE;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_ACCOUNTS_UPDATE;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_NETWORKS_UPDATE;
import static com.payoneer.checkout.localization.LocalizationKey.MESSAGES_UNSAVED_TEXT;
import static com.payoneer.checkout.localization.LocalizationKey.MESSAGES_UNSAVED_TITLE;
import static com.payoneer.checkout.localization.LocalizationKey.NETWORKS_REGISTRATION_LABEL;

import java.util.HashMap;

import com.payoneer.checkout.R;

import android.content.Context;

/**
 * Class storing local localizations
 */
public final class LocalLocalizationHolder extends MapLocalizationHolder {

    /**
     * Construct a new local localization holder
     *
     * @param context containing the local localizations
     */
    public LocalLocalizationHolder(Context context) {
        super(new HashMap<>());
        map.put(BUTTON_CANCEL, context.getString(R.string.button_cancel_label));
        map.put(BUTTON_RETRY, context.getString(R.string.button_retry_label));
        map.put(BUTTON_OK, context.getString(R.string.button_ok_label));
        map.put(BUTTON_REFRESH, context.getString(R.string.button_refresh_label));
        map.put(BUTTON_UPDATE_ACCOUNT, context.getString(R.string.button_update_account_label));

        map.put(ERROR_CONNECTION_TITLE, context.getString(R.string.error_connection_title));
        map.put(ERROR_CONNECTION_TEXT, context.getString(R.string.error_connection_text));
        map.put(ERROR_DEFAULT_TITLE, context.getString(R.string.error_default_title));
        map.put(ERROR_DEFAULT_TEXT, context.getString(R.string.error_default_text));

        map.put(LIST_HEADER_ACCOUNTS_UPDATE, context.getString(R.string.list_header_accounts_update));
        map.put(LIST_HEADER_NETWORKS_UPDATE, context.getString(R.string.list_header_networks_update));

        map.put(MESSAGES_UNSAVED_TITLE, context.getString(R.string.messages_unsaved_title));
        map.put(MESSAGES_UNSAVED_TEXT, context.getString(R.string.messages_unsaved_text));

        map.put(NETWORKS_REGISTRATION_LABEL, context.getString(R.string.networks_registration_label));
    }
}
