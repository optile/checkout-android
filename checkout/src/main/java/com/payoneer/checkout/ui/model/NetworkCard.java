/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model;

import static com.payoneer.checkout.core.PaymentInputType.ACCOUNT_NUMBER;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.localization.LocalizationKey;
import com.payoneer.checkout.model.ExtraElements;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.util.PaymentUtils;

/**
 * Class for holding the data of a NetworkCard in the list
 */
public final class NetworkCard extends PaymentCard {
    private final List<PaymentNetwork> networks;
    private final SmartSwitch smartSwitch;

    public NetworkCard(ExtraElements extraElements) {
        super(extraElements);
        this.networks = new ArrayList<>();
        this.smartSwitch = new SmartSwitch(networks);
    }

    @Override
    public void putLanguageLinks(Map<String, URL> links) {
        for (PaymentNetwork network : networks) {
            network.putLanguageLink(links);
        }
    }

    @Override
    public boolean containsLink(String name, URL url) {
        for (PaymentNetwork network : networks) {
            if (network.containsLink(name, url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getOperationType() {
        return getVisibleNetwork().getOperationType();
    }

    @Override
    public String getPaymentMethod() {
        return getVisibleNetwork().getPaymentMethod();
    }

    @Override
    public String getNetworkCode() {
        return getVisibleNetwork().getNetworkCode();
    }

    @Override
    public String getTitle() {
        if (networks.size() == 1) {
            return getVisibleNetwork().getTitle();
        }
        return Localization.translate(LocalizationKey.LIST_GROUPEDCARDS_TITLE);
    }

    @Override
    public String getSubtitle() {
        return null;
    }

    @Override
    public List<InputElement> getInputElements() {
        return getVisibleNetwork().getInputElements();
    }

    @Override
    public boolean isPreselected() {
        for (PaymentNetwork network : networks) {
            if (network.isPreselected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getButton() {
        return getVisibleNetwork().getButton();
    }

    @Override
    public void reset() {
        super.reset();
        smartSwitch.reset();
    }

    @Override
    public boolean onTextInputChanged(String type, String text) {
        setUserInputData(type, text);

        // Smartswitch works for 2 or more PaymentNetworks but only for debit/credit cards
        // and if the input field is a "number"
        if ((getPaymentNetworkCount() >= 2) && (PaymentUtils.isCardPaymentMethod(getPaymentMethod())) &&
            (ACCOUNT_NUMBER.equals(type))) {
            return smartSwitch.validate(text);
        }
        return false;
    }

    @Override
    public Map<String, URL> getLinks() {
        return getVisibleNetwork().getLinks();
    }

    /**
     * Add a PaymentNetwork to this NetworkCard, adding may fail if InputElements of this PaymentNetwork are not similar with InputElements
     * of previously added PaymentNetworks.
     *
     * @param network to be added to this NetworkCard
     * @return true when this network was added successfully or false otherwise
     */
    public boolean addPaymentNetwork(PaymentNetwork network) {

        if (networks.size() > 0 && !network.compare(networks.get(0))) {
            return false;
        }
        networks.add(network);
        return true;
    }

    /**
     * Get the list of all PaymentNetworks supported by this NetworkCard.
     *
     * @return the list of PaymentNetworks.
     */
    public List<PaymentNetwork> getPaymentNetworks() {
        return networks;
    }

    /**
     * Get the number of networks stored in this network card
     *
     * @return the number of networks stored in this card
     */
    public int getPaymentNetworkCount() {
        return networks.size();
    }

    /**
     * Get the visible PaymentNetwork, this is either the first in the list of smart selected networks or the first network if
     * none are smart selected.
     *
     * @return active PaymentNetwork
     */
    public PaymentNetwork getVisibleNetwork() {
        PaymentNetwork network = smartSwitch.getFirstSelected();
        return network == null ? networks.get(0) : network;
    }

    /**
     * Get the SmartSwitch from this NetworkCard.
     *
     * @return true when there are selected payment networks, false otherwise
     */
    public SmartSwitch getSmartSwitch() {
        return smartSwitch;
    }
}
