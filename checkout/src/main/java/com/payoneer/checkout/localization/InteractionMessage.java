/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.localization;

import com.payoneer.checkout.model.Interaction;

import android.text.TextUtils;

/**
 * Class for storing the interaction message details.
 */
public final class InteractionMessage {

    private final Interaction interaction;
    private final String operationType;

    /**
     * Construct a new InteractionMessage
     *
     * @param interaction containing the code and reason of the interaction
     * @param operationType for specific localization of interactions per flow
     */
    public InteractionMessage(Interaction interaction, String operationType) {
        this.interaction = interaction;
        this.operationType = operationType;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public String getOperationType() {
        return operationType;
    }

    public boolean hasOperationType() {
        return !TextUtils.isEmpty(operationType);
    }
}
