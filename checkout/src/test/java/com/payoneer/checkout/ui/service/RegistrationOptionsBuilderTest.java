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
import static com.payoneer.checkout.model.RegistrationType.FORCED;
import static com.payoneer.checkout.model.RegistrationType.FORCED_DISPLAYED;
import static com.payoneer.checkout.model.RegistrationType.NONE;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL;
import static com.payoneer.checkout.model.RegistrationType.OPTIONAL_PRESELECTED;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.localization.LocalizationKey;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.model.NetworkOperationType;
import com.payoneer.checkout.model.RegistrationType;
import com.payoneer.checkout.ui.model.RegistrationOptions;
import com.payoneer.checkout.ui.model.RegistrationOptions.RegistrationOption;

@RunWith(RobolectricTestRunner.class)
public class RegistrationOptionsBuilderTest {

    // First value is the autoRegistration type and second value is the allowRecurrence type
    private final static String[][] EXPECTED_VALID_COMBINATIONS = {
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

    // First value is the autoRegistration type, second value is the allowRecurrence type
    // and third value is the combined checkbox mode
    private final static String[][] EXPECTED_DEFAULT_VALUES = {
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

    // First value is the autoRegistration type, second value is the allowRecurrence type
    // and third value is the combined checkbox mode
    private final static String[][] EXPECTED_UPDATE_VALUES = {
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

    @Test(expected = IllegalStateException.class)
    public void buildRegistrationOptions_missingOperationType() throws PaymentException {
        RegistrationOptionsBuilder builder = new RegistrationOptionsBuilder();
        builder.setRegistrationOptions(RegistrationType.OPTIONAL, RegistrationType.OPTIONAL);
        builder.buildRegistrationOptions();
    }

    @Test(expected = IllegalStateException.class)
    public void buildRegistrationOptions_missingRegistrationOptions() throws PaymentException {
        RegistrationOptionsBuilder builder = new RegistrationOptionsBuilder();
        builder.setOperationType(NetworkOperationType.UPDATE);
        builder.buildRegistrationOptions();
    }

    @Test(expected = PaymentException.class)
    public void buildRegistrationOptions_invalidRegistrationOptions() throws PaymentException {
        RegistrationOptionsBuilder builder = new RegistrationOptionsBuilder();
        builder.setOperationType(NetworkOperationType.UPDATE);
        builder.setRegistrationOptions(RegistrationType.NONE, RegistrationType.OPTIONAL);
        builder.buildRegistrationOptions();
    }

    @Test
    public void buildRegistrationOptions_validDefaultRegistrationOptions() throws PaymentException {
        RegistrationOptionsBuilder builder = new RegistrationOptionsBuilder();
        builder.setOperationType(NetworkOperationType.CHARGE);

        for (int i = 0; i < EXPECTED_VALID_COMBINATIONS.length; i++) {
            testValidRegistrationOptions(builder, EXPECTED_VALID_COMBINATIONS[i], EXPECTED_DEFAULT_VALUES[i]);
        }
    }

    @Test
    public void buildRegistrationOptions_validUpdateRegistrationOptions() throws PaymentException {
        RegistrationOptionsBuilder builder = new RegistrationOptionsBuilder();
        builder.setOperationType(NetworkOperationType.UPDATE);

        for (int i = 0; i < EXPECTED_VALID_COMBINATIONS.length; i++) {
            testValidRegistrationOptions(builder, EXPECTED_VALID_COMBINATIONS[i], EXPECTED_UPDATE_VALUES[i]);
        }
    }

    private void testValidRegistrationOptions(RegistrationOptionsBuilder builder, String[] srcValues, String[] expectedValues)
        throws PaymentException {
        builder.setRegistrationOptions(srcValues[0], srcValues[1]);
        RegistrationOptions registrationOptions = builder.buildRegistrationOptions();
        assertEquals(expectedValues[2], registrationOptions.getCheckboxMode());

        // Two registration options are expected, AUTO_REGISTRATION and ALLOW_RECURRENCE
        List<RegistrationOption> options = registrationOptions.getRegistrationOptions();
        assertEquals(2, options.size());
        assertEquals(LocalizationKey.NETWORKS_REGISTRATION_LABEL, registrationOptions.getLabelKey());

        // Test autoRegistration option
        RegistrationOption option = options.get(0);
        assertEquals("Expect autoRegistration name: ", AUTO_REGISTRATION, option.getName());
        assertEquals("Expect autoRegistration type: ", expectedValues[0], option.getType());

        // Test allowRecurrence option
        option = options.get(1);
        assertEquals("Expect allowRecurrence name: ", ALLOW_RECURRENCE, option.getName());
        assertEquals("Expect allowRecurrence type: ", expectedValues[1], option.getType());
    }
}
