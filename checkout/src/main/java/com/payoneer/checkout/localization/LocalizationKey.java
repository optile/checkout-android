/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.localization;

import com.payoneer.checkout.model.Interaction;

/**
 * Class holding the localization keys and helper methods to create keys.
 */
public final class LocalizationKey {

    public final static String NETWORKS_REGISTRATION_LABEL = "networks.registration.label";

    public final static String BUTTON_OK = "button.ok.label";
    public final static String BUTTON_CANCEL = "button.cancel.label";
    public final static String BUTTON_DELETE = "button.delete.label";
    public final static String BUTTON_RETRY = "button.retry.label";
    public final static String BUTTON_REFRESH = "button.refresh.label";
    public final static String BUTTON_UPDATE_ACCOUNT = "button.operation.UPDATE.account.label";

    public final static String LIST_TITLE = "paymentpage.title";
    public final static String LIST_HEADER_PRESET = "networks.preset.title";
    public final static String LIST_HEADER_ACCOUNTS = "accounts.title";
    public final static String LIST_HEADER_NETWORKS = "networks.title";
    public final static String LIST_HEADER_NETWORKS_OTHER = "networks.other.title";

    public final static String LIST_HEADER_ACCOUNTS_UPDATE = "accounts.operation.UPDATE.title";
    public final static String LIST_HEADER_NETWORKS_UPDATE = "networks.operation.UPDATE.title";

    public final static String LIST_GROUPEDCARDS_TITLE = "groups.cards.title";
    public final static String CHARGE_TITLE = "messages.processing.title";
    public final static String CHARGE_TEXT = "messages.processing.text";
    public final static String CHARGE_INTERRUPTED = "messages.processing.interrupted.text";
    public final static String NETWORK_LABEL = "network.label";

    public final static String VERIFICATIONCODE_GENERIC_PLACEHOLDER = "account.verificationCode.generic.placeholder";
    public final static String VERIFICATIONCODE_SPECIFIC_PLACEHOLDER = "account.verificationCode.specific.placeholder";

    public final static String ERROR_CONNECTION_TITLE = "messages.error.internet.title";
    public final static String ERROR_CONNECTION_TEXT = "messages.error.internet.text";
    public final static String ERROR_DEFAULT_TITLE = "messages.error.default.title";
    public final static String ERROR_DEFAULT_TEXT = "messages.error.default.text";

    public final static String ACCOUNTS_DELETE_TITLE = "accounts.delete.title";
    public final static String ACCOUNTS_DELETE_TEXT = "accounts.delete.text";
    public final static String ACCOUNTS_DELETE_DISPLAYLABEL = "${account.displayLabel}";

    public final static String MESSAGES_UNSAVED_TITLE = "messages.unsaved.title";
    public final static String MESSAGES_UNSAVED_TEXT = "messages.unsaved.text";

    public final static String LABEL_TITLE = "title";
    public final static String LABEL_TEXT = "text";

    public static String errorKey(String error) {
        return "error." + error;
    }

    public static String accountValueKey(String account, String value) {
        return "account." + account + "." + value;
    }

    public static String accountLabelKey(String account) {
        return accountValueKey(account, "label");
    }

    public static String accountPlaceholderKey(String account) {
        return accountValueKey(account, "placeholder");
    }

    public static String accountHintKey(String account, String labelType) {
        return accountValueKey(account, "hint.where." + labelType);
    }

    public static String operationButtonKey(String operationType) {
        return "button.operation." + operationType + ".label";
    }

    public static String interactionKey(Interaction interaction, String labelType) {
        return "interaction." + interaction.getCode() + "." + interaction.getReason() + "." + labelType;
    }

    public static String interactionKey(Interaction interaction, String operationType, String labelType) {
        return "interaction." + operationType + "." + interaction.getCode() + "." + interaction.getReason() + "." + labelType;
    }
}
