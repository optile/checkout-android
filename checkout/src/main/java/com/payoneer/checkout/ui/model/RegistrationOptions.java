/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.model;

import java.util.List;

/**
 * Class for storing the RegistrationOption for autoRegistration and allowRecurrence options
 */
public final class RegistrationOptions {
    private final String checkboxMode;
    private final List<RegistrationOption> options;
    private final String labelKey;

    /**
     * Construct a new RegistrationOptions with the combined checkboxMode
     *
     * @param options list of registration options the checkbox is used for
     * @param checkboxMode the combined checkbox mode of the registration options
     * @param labelKey localization key for this registrationOptions
     */
    public RegistrationOptions(List<RegistrationOption> options, String checkboxMode, String labelKey) {
        this.options = options;
        this.checkboxMode = checkboxMode;
        this.labelKey = labelKey;
    }

    public List<RegistrationOption> getRegistrationOptions() {
        return options;
    }

    public String getCheckboxMode() {
        return checkboxMode;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public static class RegistrationOption {

        private final String name;
        private final String type;

        public RegistrationOption(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
