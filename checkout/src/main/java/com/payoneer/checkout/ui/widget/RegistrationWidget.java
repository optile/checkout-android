/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import static com.payoneer.checkout.model.RegistrationType.FORCED;
import static com.payoneer.checkout.model.RegistrationType.FORCED_DISPLAYED;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL_PRESELECTED;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.localization.Localization;
import com.payoneer.checkout.ui.model.RegistrationOptions;
import com.payoneer.checkout.ui.model.RegistrationOptions.RegistrationOption;

/**
 * Widget for showing the RegistrationOptions, e.g. allowRecurrence and autoRegistration
 */
public class RegistrationWidget extends CheckboxWidget {

    private RegistrationOptions registrationOptions;

    public RegistrationWidget(String category, String name) {
        super(category, name);
    }

    @Override
    public void putValue(Operation operation) throws PaymentException {
        for (RegistrationOption option : registrationOptions.getRegistrationOptions()) {
            putRegistrationValue(operation, option);
        }
    }

    private void putRegistrationValue(Operation operation, RegistrationOption option) throws PaymentException {
        boolean value;
        switch (option.getType()) {
            case FORCED:
            case FORCED_DISPLAYED:
                value = true;
                break;
            case OPTIONAL:
            case OPTIONAL_PRESELECTED:
                value = switchView.isChecked();
                break;
            default:
                value = false;
        }
        operation.putBooleanValue(category, option.getName(), value);
    }

    /**
     * Bind this RegistrationWidget to the RegistrationOption
     *
     * @param registrationOptions containing the registration options
     */
    public void onBind(RegistrationOptions registrationOptions) {
        this.registrationOptions = registrationOptions;
        String label = Localization.translate(registrationOptions.getLabelKey());
        super.onBind(registrationOptions.getCheckboxMode(), label);
    }
}
