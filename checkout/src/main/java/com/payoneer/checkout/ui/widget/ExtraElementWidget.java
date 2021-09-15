/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.widget;

import com.payoneer.checkout.core.PaymentException;
import com.payoneer.checkout.form.Operation;
import com.payoneer.checkout.model.CheckboxMode;
import com.payoneer.checkout.model.ExtraElement;

/**
 * Widget for showing the ExtraElement element
 */
public class ExtraElementWidget extends CheckboxWidget {

    public ExtraElementWidget(String category, String name) {
        super(category, name);
    }

    @Override
    public void putValue(Operation operation) throws PaymentException {
        // Until optional checkboxes are supported for ExtraElements, this widget does not add any
        // value to the operation
    }

    /**
     * Bind this ExtraElementWidget to the ExtraElement
     *
     * @param extraElement containing the label and optional checkbox
     */
    public void onBind(ExtraElement extraElement) {
        String checkboxMode = (extraElement.getCheckbox() == null) ? CheckboxMode.FORCED_DISPLAYED : CheckboxMode.NONE;
        super.onBind(checkboxMode, extraElement.getLabel());
    }
}
