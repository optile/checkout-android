/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.localization;

import static com.payoneer.checkout.localization.LocalizationKey.LABEL_TEXT;
import static com.payoneer.checkout.localization.LocalizationKey.NETWORK_LABEL;

import java.util.Map;

import com.payoneer.checkout.model.Interaction;

import android.text.TextUtils;

/**
 * Class holding individual localizations to provide easy access to all translations.
 */
public final class Localization {
    private final Map<String, LocalizationHolder> networks;
    private final LocalizationHolder shared;
    private static Localization instance;

    /**
     * Get the currently set Localization instance
     *
     * @return the current instance or null if not previously set
     */
    public static Localization getInstance() {
        return instance;
    }

    /**
     * Set the current Localization instance
     *
     * @param newInstance to be set as the current instance
     */
    public static void setInstance(Localization newInstance) {
        instance = newInstance;
    }

    /**
     * Construct a new Localization with a shared localization and a list of network localizations
     *
     * @param shared the shared localization
     * @param networks the list of network localizations
     */
    public Localization(LocalizationHolder shared, Map<String, LocalizationHolder> networks) {
        this.shared = shared;
        this.networks = networks;
    }

    /**
     * Helper method to obtain the translation for the given key from the shared holder
     *
     * @param key of the translation
     * @return the translation or null if not found
     */
    public static String translate(String key) {
        Localization loc = getInstance();
        return loc != null ? loc.getSharedTranslation(key) : null;
    }

    /**
     * Helper method to obtain the translation for the given key from the localization file.
     *
     * @param networkCode name of the localization file
     * @param key of the translation
     * @return the translation or null if not found
     */
    public static String translate(String networkCode, String key) {
        Localization loc = getInstance();
        return loc != null ? loc.getNetworkTranslation(networkCode, key) : null;
    }

    /**
     * Helper method to obtain the translation for the interaction message.
     * If the optional operationType in the message is null then fallback to the
     * regular localization for the interaction.
     *
     * @param interactionMessage interaction message to be translated
     * @param labelType type of the interaction label that is required, either LABEL_TEXT or LABEL_TITLE
     * @return translation of the interaction or null if not found
     */
    public static String translateInteractionMessage(InteractionMessage interactionMessage, String labelType) {
        Localization loc = getInstance();
        if (loc == null) {
            return null;
        }
        Interaction interaction = interactionMessage.getInteraction();
        if (interactionMessage.hasOperationType()) {
            String operationType = interactionMessage.getOperationType();
            String text = loc.getSharedTranslation(LocalizationKey.interactionKey(interaction, operationType, labelType));
            if (!TextUtils.isEmpty(text)) {
                return text;
            }
        }
        return loc.getSharedTranslation(LocalizationKey.interactionKey(interaction, labelType));
    }

    /**
     * Helper method to check if a translation exist for the Interaction message.
     * If the operationType is null in the message then fallback to the normal interaction
     * localizations without the operationType.
     *
     * @param interactionMessage containing the interaction message to check if a translation exists
     * @return true if it has a translation for the interaction, false otherwise
     */
    public static boolean hasInteractionMessage(InteractionMessage interactionMessage) {
        String text = translateInteractionMessage(interactionMessage, LABEL_TEXT);
        return !TextUtils.isEmpty(text);
    }

    /**
     * Helper method to check if a translation exist for the account hint.
     *
     * @param networkCode the name of the localization holder
     * @param account name of the account
     * @return true if it has a translation for the account hint, false otherwise
     */
    public static boolean hasAccountHint(String networkCode, String account) {
        String text = translateAccountHint(networkCode, account, LABEL_TEXT);
        return !TextUtils.isEmpty(text);
    }

    /**
     * Helper method to obtain the translation for the error from the specified file.
     *
     * @param networkCode name of the localization file
     * @param error the error which should be translated
     * @return the translation or null if not found
     */
    public static String translateError(String networkCode, String error) {
        return translate(networkCode, LocalizationKey.errorKey(error));
    }

    /**
     * Helper method to obtain the translation for the account label.
     *
     * @param networkCode name of the localization file
     * @param account name of the account
     * @return translation or null if not found
     */
    public static String translateAccountLabel(String networkCode, String account) {
        return translate(networkCode, LocalizationKey.accountLabelKey(account));
    }

    /**
     * Helper method to obtain the translation for the account value.
     *
     * @param networkCode name of the localization file
     * @param account name of the account
     * @param value name of the value
     * @return translation or null if not found
     */
    public static String translateAccountValue(String networkCode, String account, String value) {
        return translate(networkCode, LocalizationKey.accountValueKey(account, value));
    }

    /**
     * Helper method to obtain the translation for the account placeholder.
     *
     * @param networkCode name of the localization file
     * @param account name of the account
     * @return translation or null if not found
     */
    public static String translateAccountPlaceholder(String networkCode, String account) {
        return translate(networkCode, LocalizationKey.accountPlaceholderKey(account));
    }

    /**
     * Helper method to obtain the translation of the account hint with the given label type.
     *
     * @param networkCode name of the localization file
     * @param account name of the account
     * @param labelType type of the account hint, either LABEL_TITLE or LABEL_TEXT
     * @return the translation or null if not found
     */
    public static String translateAccountHint(String networkCode, String account, String labelType) {
        return translate(networkCode, LocalizationKey.accountHintKey(account, labelType));
    }

    /**
     * Helper method to obtain the translation of the network label given the networkCode
     *
     * @param networkCode to translate it into a label
     * @return the translated network label
     */
    public static String translateNetworkLabel(String networkCode) {
        return translate(networkCode, NETWORK_LABEL);
    }

    /**
     * Get the translation from the network localization the given network code.
     * If the localization does not exist or does not contain the translation then return null
     *
     * @param networkCode name of the network localization
     * @param key of the translation
     * @return the translation or null if the translation was not found
     */
    public String getNetworkTranslation(String networkCode, String key) {
        LocalizationHolder holder = networks != null ? networks.get(networkCode) : null;
        return holder != null ? holder.translate(key) : null;
    }

    /**
     * Get the translation from the shared localization holder.
     * If the shared holder does not exist or does not contain the translation then return null.
     *
     * @param key of the translation
     * @return the translation or null if the translation was not found
     */
    public String getSharedTranslation(String key) {
        return shared != null ? shared.translate(key) : null;
    }
}
