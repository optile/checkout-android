/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model;

import static com.payoneer.checkout.ui.model.PaymentSession.LINK_LOGO;
import static com.payoneer.checkout.ui.model.PaymentSession.LINK_OPERATION;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.model.ExtraElement;
import com.payoneer.checkout.model.ExtraElements;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.util.PaymentUtils;

import android.text.TextUtils;

/**
 * Base class for all payment cards like the AccountCard, PresetCard and NetworkCard
 */
public abstract class PaymentCard {

    private ExtraElements extraElements;
    private boolean checkable;
    private boolean hideInputForm;
    private boolean disabled;
    final List<String> userInputTypes;

    /**
     * Construct a PaymentCard, when a card is checkable and marked as checked
     * a highlighted border is drawn around the card.
     *
     * @param extraElements optional elements to be shown in this PaymentCard
     */
    public PaymentCard(ExtraElements extraElements) {
        this.extraElements = extraElements;
        this.userInputTypes = new ArrayList<>();
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Mark this card as checkable, if the card is checkable and checked
     * a highlighted border is drawn around the card.
     *
     * @param checkable true if checkable, false otherwise
     */
    public void setCheckable(final boolean checkable) {
        this.checkable = checkable;
    }

    /**
     * Is this payment card checkable. When the card is checked, a highlighted border
     * is drawn around the payment card.
     *
     * @return true when checkable, false otherwise
     */
    public boolean isCheckable() {
        return checkable;
    }

    /**
     * Notify that text input has changed for one of the input fields in this PaymentCard.
     *
     * @param type the type of the TextInput field
     * @param text new text of the input field
     * @return true when this PaymentCard has changed its appearance because of the new input, false otherwise
     */
    public boolean onTextInputChanged(String type, String text) {
        return false;
    }

    /**
     * Get the operation link from the active network in this card,
     * this link can be used to make i.e. a charge request
     *
     * @return the operation link or null if it does not exist
     */
    public URL getOperationLink() {
        return getLink(LINK_OPERATION);
    }

    /**
     * Get the logo link from the active network in this card,
     * this link points to the logo image
     *
     * @return the logo link or null if it does not exist
     */
    public URL getLogoLink() {
        return getLink(LINK_LOGO);
    }

    /**
     * Get the link from this PaymentCard with the given name
     *
     * @param name of the link that should be returned
     * @return the link or null if not available
     */
    public URL getLink(String name) {
        return getLinks().get(name);
    }

    /**
     * Get the InputElement given the name
     *
     * @param name of the InputElement to be returned
     * @return the InputElement with the given name or null if not found
     */
    public InputElement getInputElement(String name) {
        for (InputElement element : getInputElements()) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Get the ExtraElement given the name
     *
     * @param name of the ExtraElement to be returned
     * @return the ExtraElement with the given name or null if not found
     */
    public ExtraElement getExtraElement(String name) {
        for (ExtraElement element : getTopExtraElements()) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        for (ExtraElement element : getBottomExtraElements()) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }


    /**
     * Is the input form hidden from the user or not.
     *
     * @return true when the input form should be hidden, false otherwise
     */
    public boolean getHideInputForm() {
        return hideInputForm;
    }

    /**
     * Set in this PaymentCard that the input form must be hidden.
     * By default the input form is shown to the user.
     *
     * @param hideInputForm indicates if the input form should be hidden from the user
     */
    public void setHideInputForm(boolean hideInputForm) {
        this.hideInputForm = hideInputForm;
    }

    /**
     * Does this PaymentCard has any form elements
     *
     * @return true when the form has elements, false otherwise
     */
    public boolean hasFormElements() {
        return (getInputElements().size() > 0) || (getTopExtraElements().size() > 0) || (getBottomExtraElements().size() > 0);
    }

    /**
     * Check if the user has typed any data in the payment cards.
     * This is used to determine if e.g. the payment list can be refreshed without
     * loosing data the user typed.
     *
     * @return true when this PaymentCard contains user data, false otherwise
     */
    public boolean hasUserInputData() {
        return userInputTypes.size() != 0;
    }

    /**
     * Reset this payment card, clean up any cache or temporary values.
     * For example, temporary selection data stored in SmartSwitch.
     */
    public void reset() {
        userInputTypes.clear();
    }

    /**
     * Get the top ExtraElements that should be shown to the user in the top of the payment card
     *
     * @return list with optional top ExtraElements
     */
    public List<ExtraElement> getTopExtraElements() {
        if (extraElements == null) {
            return Collections.emptyList();
        }
        return PaymentUtils.emptyListIfNull(extraElements.getTop());
    }

    /**
     * Get the bottom ExtraElements that should be shown to the user in the bottom of the payment card
     *
     * @return list with optional bottom ExtraElements
     */
    public List<ExtraElement> getBottomExtraElements() {
        if (extraElements == null) {
            return Collections.emptyList();
        }
        return PaymentUtils.emptyListIfNull(extraElements.getBottom());
    }

    /**
     * Set the user input data. Add the type to the userInputTypes list if the text is not empty,
     * remove it otherwise. If the list has elements it implies that the user has entered text in this
     * PaymentCard.
     *
     * @param type of the input data field
     * @param text containing the user input data
     */
    void setUserInputData(String type, String text) {
        if (TextUtils.isEmpty(text)) {
            userInputTypes.remove(type);
        } else if (!userInputTypes.contains(type)) {
            userInputTypes.add(type);
        }
    }

    /**
     * Check if this card contains a link with the provided name. If the card contains multiple networks then
     * all networks must be checked if at least one of them contains the link.
     *
     * @param name of the link
     * @param url that should match
     * @return true when it contains the link, false otherwise
     */
    public abstract boolean containsLink(String name, URL url);

    /**
     * Get the action button label
     *
     * @return the action button label or null if not set
     */
    public abstract String getButton();

    /**
     * Put all language links stored in the payment card into the links map.
     * The key is the name of the network the URL belongs to.
     *
     * @param links map into which the links should be stored
     */
    public abstract void putLanguageLinks(Map<String, URL> links);

    /**
     * Get the operation type, e.g. OperationType.CHARGE or OperationType.PRESET
     *
     * @return the operationType of this payment card
     */
    public abstract String getOperationType();

    /**
     * Get the paymentMethod of this PaymentCard
     *
     * @return paymentMethod of this PaymentCard
     */
    public abstract String getPaymentMethod();

    /**
     * Get the network code of this PaymentCard.
     *
     * @return network code of this PaymentCard
     */
    public abstract String getNetworkCode();

    /**
     * Is this card preselected
     *
     * @return true when preselected, false otherwise
     */
    public abstract boolean isPreselected();

    /**
     * Get the title of this PaymentCard
     *
     * @return title of this PaymentCard
     */
    public abstract String getTitle();

    /**
     * Get the subtitle of this PaymentCard
     *
     * @return subtitle of this PaymentCard
     */
    public abstract String getSubtitle();

    /**
     * Get the map of links. The key of this map is the name of the link
     *
     * @return the map of links, this must not return null
     */
    public abstract Map<String, URL> getLinks();

    /**
     * Get the list of input elements supported by this  payment card.
     *
     * @return list of InputElements, this must not return null
     */
    public abstract List<InputElement> getInputElements();
}
