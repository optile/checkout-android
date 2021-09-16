/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model;

import static com.payoneer.checkout.ui.model.PaymentSession.LINK_LANGUAGE;

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.model.ApplicableNetwork;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.util.PaymentUtils;

/**
 * Class for holding the ApplicableNetwork
 */
public final class PaymentNetwork {

    private final ApplicableNetwork network;
    private final String buttonKey;
    private final RegistrationOptions registrationOptions;

    public PaymentNetwork(ApplicableNetwork network, String buttonKey, RegistrationOptions registrationOptions) {
        this.network = network;
        this.buttonKey = buttonKey;
        this.registrationOptions = registrationOptions;
    }

    public void putLanguageLink(Map<String, URL> links) {
        URL url = getLink(LINK_LANGUAGE);
        if (url != null) {
            links.put(getNetworkCode(), url);
        }
    }

    public URL getLink(String name) {
        return getLinks().get(name);
    }

    public boolean containsLink(String name, URL url) {
        return PaymentUtils.equalsAsString(getLink(name), url);
    }

    public String getOperationType() {
        return network.getOperationType();
    }

    public String getPaymentMethod() {
        return network.getMethod();
    }

    public String getNetworkCode() {
        return network.getCode();
    }

    public String getTitle() {
        return Localization.translateNetworkLabel(getNetworkCode());
    }

    public String getButton() {
        return Localization.translate(getNetworkCode(), buttonKey);
    }

    public RegistrationOptions getRegistrationOptions() {
        return registrationOptions;
    }

    public boolean isPreselected() {
        return PaymentUtils.isTrue(network.getSelected());
    }

    public List<InputElement> getInputElements() {
        return PaymentUtils.emptyListIfNull(network.getInputElements());
    }

    public Map<String, URL> getLinks() {
        return PaymentUtils.emptyMapIfNull(network.getLinks());
    }

    public boolean compare(PaymentNetwork network) {
        List<InputElement> srcItems = getInputElements();
        List<InputElement> cmpItems = network.getInputElements();

        if (srcItems.size() != cmpItems.size()) {
            return false;
        }
        InputElement srcItem;
        InputElement cmpItem;

        for (int i = 0, e = srcItems.size(); i < e; i++) {
            srcItem = srcItems.get(i);
            cmpItem = cmpItems.get(i);

            if (!(srcItem.getName().equals(cmpItem.getName()) && srcItem.getType().equals(cmpItem.getType()))) {
                return false;
            }
        }
        return true;
    }
}
