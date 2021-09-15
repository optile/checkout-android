/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.service;

import static com.payoneer.checkout.localization.LocalizationKey.BUTTON_UPDATE_ACCOUNT;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_ACCOUNTS;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_ACCOUNTS_UPDATE;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_NETWORKS;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_NETWORKS_OTHER;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_NETWORKS_UPDATE;
import static com.payoneer.checkout.localization.LocalizationKey.LIST_HEADER_PRESET;
import static com.payoneer.checkout.model.NetworkOperationType.PRESET;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;
import static com.payoneer.checkout.model.RegistrationType.NONE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.R;
import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.LocalizationKey;
import com.payoneer.checkout.model.AccountRegistration;
import com.payoneer.checkout.model.ApplicableNetwork;
import com.payoneer.checkout.model.ExtraElements;
import com.payoneer.checkout.model.ListResult;
import com.payoneer.checkout.model.Networks;
import com.payoneer.checkout.model.PresetAccount;
import com.payoneer.checkout.resource.PaymentGroup;
import com.payoneer.checkout.ui.model.AccountCard;
import com.payoneer.checkout.ui.model.AccountCard.AccountIcon;
import com.payoneer.checkout.ui.model.NetworkCard;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.model.PaymentNetwork;
import com.payoneer.checkout.ui.model.PaymentSection;
import com.payoneer.checkout.ui.model.PaymentSession;
import com.payoneer.checkout.ui.model.PresetCard;
import com.payoneer.checkout.ui.model.RegistrationOption;
import com.payoneer.checkout.util.PaymentUtils;

import android.text.TextUtils;

/**
 * The PaymentSessionBuilder for building a PaymentSession from a ListResult
 * This builder will group PaymentNetworks together according to the provided group settings.
 */
public final class PaymentSessionBuilder {

    private ListResult listResult;
    private Map<String, PaymentGroup> paymentGroups;

    /**
     * Set the session listener which will be informed about the state of a payment session being loaded.
     *
     * @param listResult to be converted into a PaymentSession model class
     * @return this builder
     */
    public PaymentSessionBuilder setListResult(final ListResult listResult) {
        this.listResult = listResult;
        return this;
    }

    /**
     * Set the map of payment groups, this map defines which PaymentNetworks should
     * be grouped together in one NetworkCard
     *
     * @param paymentGroups contains network grouping information
     */
    public PaymentSessionBuilder setPaymentGroups(final Map<String, PaymentGroup> paymentGroups) {
        this.paymentGroups = paymentGroups;
        return this;
    }

    /**
     * Build a PaymentSession from the set ListResult and paymentGroups
     */
    public PaymentSession build() throws PaymentException {
        if (listResult == null) {
            throw new IllegalStateException("listResult may not be null");
        }
        if (paymentGroups == null) {
            throw new IllegalStateException("paymentGroups may not be null");
        }
        String operationType = listResult.getOperationType();
        List<PaymentSection> sections = new ArrayList<PaymentSection>();
        PaymentSection section = buildPresetSection(listResult);
        if (section != null) {
            sections.add(section);
        }
        section = buildAccountSection(listResult);
        if (section != null) {
            sections.add(section);
        }
        section = buildNetworkSection(listResult, section != null);
        if (section != null) {
            sections.add(section);
        }
        boolean refreshable = UPDATE.equals(operationType);
        return new PaymentSession(listResult, sections, refreshable);
    }

    private PaymentSection buildPresetSection(ListResult listResult) {
        PresetAccount account = listResult.getPresetAccount();
        if (account == null) {
            return null;
        }
        List<PaymentCard> cards = new ArrayList<>();
        cards.add(buildPresetCard(account, listResult));
        return new PaymentSection(LIST_HEADER_PRESET, cards);
    }

    private PaymentSection buildAccountSection(ListResult listResult) {
        List<PaymentCard> cards = new ArrayList<>();
        List<AccountRegistration> accounts = listResult.getAccounts();

        if (accounts == null || accounts.size() == 0) {
            return null;
        }
        for (AccountRegistration account : accounts) {
            if (NetworkServiceLookup.supports(account.getCode(), account.getMethod())) {
                cards.add(buildAccountCard(account, listResult));
            }
        }
        if (cards.size() == 0) {
            return null;
        }
        String labelKey = UPDATE.equals(listResult.getOperationType()) ?
            LIST_HEADER_ACCOUNTS_UPDATE : LIST_HEADER_ACCOUNTS;
        return new PaymentSection(labelKey, cards);
    }

    private PresetCard buildPresetCard(PresetAccount account, ListResult listResult) {
        String buttonKey = LocalizationKey.operationButtonKey(PRESET);
        ExtraElements extraElements = listResult.getExtraElements();

        PresetCard card = new PresetCard(account, buttonKey, extraElements);
        card.setCheckable(true);
        return card;
    }

    private AccountCard buildAccountCard(AccountRegistration account, ListResult listResult) {
        String operationType = account.getOperationType();
        if (UPDATE.equals(operationType)) {
            return buildUpdateAccountCard(account, listResult);
        } else {
            return buildDefaultAccountCard(account, listResult);
        }
    }

    private AccountCard buildUpdateAccountCard(AccountRegistration account, ListResult listResult) {
        AccountCard card = new AccountCard(account, BUTTON_UPDATE_ACCOUNT, listResult.getExtraElements());
        boolean deletable = PaymentUtils.toBoolean(listResult.getAllowDelete(), true);
        boolean hasFormElements = card.hasFormElements();

        // AccountCard is disabled in update flow when it has no form elements and is not deletable
        if (!(deletable || hasFormElements)) {
            card.setDisabled(true);
            return card;
        }
        card.setDeletable(deletable);
        card.setHideInputForm(!hasFormElements);
        card.setCheckable(true);

        int expandedIcon = deletable ? R.drawable.ic_delete : R.drawable.ic_transparent;
        AccountIcon icon = new AccountIcon(R.drawable.ic_edit, expandedIcon);
        card.setAccountIcon(icon);
        return card;
    }

    private AccountCard buildDefaultAccountCard(AccountRegistration account, ListResult listResult) {
        String buttonKey = LocalizationKey.operationButtonKey(account.getOperationType());
        AccountCard card = new AccountCard(account, buttonKey, listResult.getExtraElements());
        boolean deletable = PaymentUtils.toBoolean(listResult.getAllowDelete(), false);

        if (deletable) {
            card.setDeletable(true);
            AccountIcon icon = new AccountIcon(R.drawable.ic_transparent, R.drawable.ic_delete);
            card.setAccountIcon(icon);
        }
        return card;
    }

    private PaymentSection buildNetworkSection(ListResult listResult, boolean containsAccounts)
        throws PaymentException {
        Map<String, PaymentNetwork> networks = buildPaymentNetworks(listResult);
        Map<String, NetworkCard> cards = new LinkedHashMap<>();
        PaymentGroup group;

        for (PaymentNetwork network : networks.values()) {
            group = paymentGroups.get(network.getNetworkCode());

            if (group == null) {
                addNetwork2SingleCard(cards, network, listResult);
            } else {
                addNetwork2GroupCard(cards, network, group, listResult);
            }
        }
        if (cards.size() == 0) {
            return null;
        }
        String labelKey;
        if (UPDATE.equals(listResult.getOperationType())) {
            labelKey = LIST_HEADER_NETWORKS_UPDATE;
        } else if (containsAccounts) {
            labelKey = LIST_HEADER_NETWORKS_OTHER;
        } else {
            labelKey = LIST_HEADER_NETWORKS;
        }
        return new PaymentSection(labelKey, new ArrayList<>(cards.values()));
    }

    private Map<String, PaymentNetwork> buildPaymentNetworks(ListResult listResult) {
        LinkedHashMap<String, PaymentNetwork> items = new LinkedHashMap<>();
        Networks nw = listResult.getNetworks();

        if (nw == null) {
            return items;
        }
        List<ApplicableNetwork> an = nw.getApplicable();
        if (an == null || an.size() == 0) {
            return items;
        }
        for (ApplicableNetwork network : an) {
            String code = network.getCode();
            if (supportsApplicableNetwork(listResult, network)) {
                items.put(code, buildPaymentNetwork(network));
            }
        }
        return items;
    }

    private boolean supportsApplicableNetwork(ListResult listResult, ApplicableNetwork network) {
        String operationType = listResult.getOperationType();
        String recurrence = network.getRecurrence();
        String registration = network.getRegistration();

        // Special case to hide networks in Update flow with both registration settings set to NONE.
        if (UPDATE.equals(operationType) && NONE.equals(recurrence) && NONE.equals(registration)) {
            return false;
        }
        return NetworkServiceLookup.supports(network.getCode(), network.getMethod());
    }

    private PaymentNetwork buildPaymentNetwork(ApplicableNetwork network) {
        String operationType = network.getOperationType();
        String buttonKey = LocalizationKey.operationButtonKey(operationType);

        RegistrationOptionsBuilder builder = new RegistrationOptionsBuilder()
            .setOperationType(operationType)
            .setAutoRegistration(network.getRegistration())
            .setAllowRecurrence(network.getRecurrence());

        RegistrationOption registration = builder.buildAutoRegistrationOption();
        RegistrationOption recurrence = builder.buildAllowRecurrenceOption();
        return new PaymentNetwork(network, buttonKey, registration, recurrence);
    }

    private void addNetwork2SingleCard(Map<String, NetworkCard> cards, PaymentNetwork network, ListResult listResult) {
        NetworkCard card = new NetworkCard(listResult.getExtraElements());
        card.addPaymentNetwork(network);
        cards.put(network.getNetworkCode(), card);
    }

    private void addNetwork2GroupCard(Map<String, NetworkCard> cards, PaymentNetwork network, PaymentGroup group, ListResult listResult)
        throws PaymentException {
        String code = network.getNetworkCode();
        String groupId = group.getId();
        String regex = group.getSmartSelectionRegex(code);
        ExtraElements extraElements = listResult.getExtraElements();

        if (TextUtils.isEmpty(regex)) {
            throw new PaymentException("Missing regex for network: " + code + " in group: " + groupId);
        }
        NetworkCard card = cards.get(groupId);
        if (card == null) {
            card = new NetworkCard(extraElements);
            cards.put(groupId, card);
        }
        // a network can always be added to an empty card
        if (!(card.addPaymentNetwork(network))) {
            addNetwork2SingleCard(cards, network, listResult);
            return;
        }
        card.getSmartSwitch().addSelectionRegex(code, regex);
    }
}
