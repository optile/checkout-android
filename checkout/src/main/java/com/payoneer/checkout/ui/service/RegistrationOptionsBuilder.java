/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.service;

import static com.payoneer.checkout.core.PaymentInputType.ALLOW_RECURRENCE;
import static com.payoneer.checkout.core.PaymentInputType.AUTO_REGISTRATION;
import static com.payoneer.checkout.model.NetworkOperationType.UPDATE;
import static com.payoneer.checkout.model.RegistrationType.FORCED;
import static com.payoneer.checkout.model.RegistrationType.FORCED_DISPLAYED;
import static com.payoneer.checkout.model.RegistrationType.NONE;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL_PRESELECTED;

import java.util.ArrayList;
import java.util.List;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.LocalizationKey;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.ui.model.RegistrationOptions;
import com.payoneer.checkout.ui.model.RegistrationOptions.RegistrationOption;

import android.text.TextUtils;

/**
 * Class for building the RegistrationOptions for both
 * allowRecurrence and autoRegistration options.
 */
public final class RegistrationOptionsBuilder {

    private String autoRegistration;
    private String allowRecurrence;
    private String operationType;

    private final static String[][] VALID_COMBINATIONS = {
        { NONE, NONE },
        { FORCED, NONE },
        { FORCED_DISPLAYED, NONE },
        { FORCED, FORCED },
        { FORCED_DISPLAYED, FORCED_DISPLAYED },
        { OPTIONAL, NONE },
        { OPTIONAL_PRESELECTED, NONE },
        { OPTIONAL, OPTIONAL },
        { OPTIONAL_PRESELECTED, OPTIONAL_PRESELECTED }
    };

    private final static String[][] DEFAULT_FLOW_OPTIONS = {
        { NONE, NONE, CheckboxMode.NONE },
        { FORCED, NONE, CheckboxMode.NONE },
        { FORCED_DISPLAYED, NONE, CheckboxMode.FORCED_DISPLAYED },
        { FORCED, FORCED, CheckboxMode.NONE },
        { FORCED_DISPLAYED, FORCED_DISPLAYED, CheckboxMode.FORCED_DISPLAYED },
        { OPTIONAL, NONE, CheckboxMode.OPTIONAL },
        { OPTIONAL_PRESELECTED, NONE, CheckboxMode.OPTIONAL_PRESELECTED },
        { OPTIONAL, OPTIONAL, CheckboxMode.OPTIONAL },
        { OPTIONAL_PRESELECTED, OPTIONAL_PRESELECTED, CheckboxMode.OPTIONAL_PRESELECTED }
    };

    private final static String[][] UPDATE_FLOW_OPTIONS = {
        { NONE, NONE, CheckboxMode.NONE },
        { FORCED, NONE, CheckboxMode.NONE },
        { FORCED, NONE, CheckboxMode.NONE },
        { FORCED, FORCED, CheckboxMode.NONE },
        { FORCED, FORCED, CheckboxMode.NONE },
        { FORCED, NONE, CheckboxMode.NONE },
        { FORCED, NONE, CheckboxMode.NONE },
        { FORCED, FORCED, CheckboxMode.NONE },
        { FORCED, FORCED, CheckboxMode.NONE }
    };

    /**
     * Build the RegistrationOption for autoRegistration
     *
     * @return the newly build RegistrationOption for autoRegistration
     * @throws PaymentException when an invalid registration combination is set
     */
    public RegistrationOptions buildRegistrationOptions() throws PaymentException {
        if (TextUtils.isEmpty(operationType)) {
            throw new IllegalStateException("operationType may not be null or empty");
        }
        if (TextUtils.isEmpty(autoRegistration)) {
            throw new IllegalStateException("autoRegistration may not be null or empty");
        }
        if (TextUtils.isEmpty(allowRecurrence)) {
            throw new IllegalStateException("allowRecurrence may not be null or empty");
        }
        int index = getRegistrationOptionIndex();
        String[][] settings = (UPDATE.equals(operationType)) ? UPDATE_FLOW_OPTIONS : DEFAULT_FLOW_OPTIONS;

        List<RegistrationOption> options = new ArrayList<>();
        options.add(new RegistrationOption(AUTO_REGISTRATION, settings[index][0]));
        options.add(new RegistrationOption(ALLOW_RECURRENCE, settings[index][1]));
        return new RegistrationOptions(options, settings[index][2], LocalizationKey.NETWORKS_REGISTRATION_LABEL);
    }

    private int getRegistrationOptionIndex() throws PaymentException {
        for (int i = 0; i < VALID_COMBINATIONS.length; i++) {
            String[] types = VALID_COMBINATIONS[i];
            if (types[0].equals(autoRegistration) && types[1].equals(allowRecurrence)) {
                return i;
            }
        }
        String message = "Unsupported combination of autoRegistration (" + autoRegistration + ") "
            + "and allowRecurrence (" + allowRecurrence + ") "
            + "for operationType (" + operationType + ")";
        throw new PaymentException(message);
    }

    public RegistrationOptionsBuilder setOperationType(String operationType) {
        this.operationType = operationType;
        return this;
    }

    public RegistrationOptionsBuilder setRegistrationOptions(String autoRegistration, String allowRecurrence) {
        this.autoRegistration = autoRegistration;
        this.allowRecurrence = allowRecurrence;
        return this;
    }
}
