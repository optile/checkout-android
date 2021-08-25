/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is designed to hold interaction information that prescribes further reaction of merchant portal to this transaction or operation.
 */
@Getter
@Setter
public class Interaction {
    /** Simple API, always present */
    @InteractionCode.Definition
    private String code;
    /** Simple API, always present */
    @InteractionReason.Definition
    private String reason;

    /**
     * Construct an empty Interaction Object
     */
    public Interaction() {
    }

    /**
     * Construct a new Interaction Object with the predefined code and reason
     *
     * @param code the code to set.
     * @param reason the reason to set.
     */
    public Interaction(@InteractionCode.Definition String code, @InteractionReason.Definition String reason) {
        this.code = code;
        this.reason = reason;
    }
}
