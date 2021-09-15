/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.util;

import static com.payoneer.checkout.model.PaymentMethod.CREDIT_CARD;
import static com.payoneer.checkout.model.PaymentMethod.DEBIT_CARD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.payoneer.checkout.core.PaymentInputType;
import com.payoneer.checkout.model.AccountMask;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.model.Parameter;

import android.content.res.Resources;
import android.view.View;

/**
 * The PaymentUtils class containing helper methods
 */
public final class PaymentUtils {

    /**
     * Check if the Boolean object is true, the Boolean object may be null.
     *
     * @param value the value to check
     * @return true when the val is not null and true
     */
    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    /**
     * Return the boolean value given the Boolean Object.
     * If the Object is null then return the default value.
     *
     * @param value the value to check
     * @return defaultValue if value is null, else the boolean value
     */
    public static boolean toBoolean(Boolean value, boolean defaultValue) {
        return (value == null) ? defaultValue : value.booleanValue();
    }

    /**
     * Strips whitespace from the start and end of a String returning an empty String if null input.
     *
     * @param value the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if null input
     */
    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Returns a formatted string using the default locale, format string, and arguments.
     *
     * @param format A format string
     * @param args Arguments referenced by the format specifiers in the format string
     */
    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    /**
     * Check if the paymentMethod is a card payment method.
     *
     * @param paymentMethod to be checked if it is a card payment
     * @return true when card payment, false otherwise
     */
    public static boolean isCardPaymentMethod(String paymentMethod) {
        return DEBIT_CARD.equals(paymentMethod) || CREDIT_CARD.equals(paymentMethod);
    }

    /**
     * Compare String values of two Objects by obtaining the String values using the toString method.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return true when the String values of both Objects are equal and not null, false otherwise.
     */
    public static boolean equalsAsString(Object obj1, Object obj2) {
        String str1 = obj1 != null ? obj1.toString() : null;
        String str2 = obj2 != null ? obj2.toString() : null;
        return str1 != null && (str1.equals(str2));
    }

    /**
     * Get the base integer value given the Integer object.
     * If the object is null then return the 0 value.
     *
     * @param value to convert to an integer
     * @return the value as an integer or 0 if the value is null
     */
    public static int toInt(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * Get the label for this AccountMask, if the paymentMethod is a card then return the
     * number from the mask. Else return the DisplayLabel from this mask.
     *
     * @param accountMask containing the label information
     * @param paymentMethod to which this accountMask belongs to
     * @return the label for this AccountMask
     */
    public static String getAccountMaskLabel(AccountMask accountMask, String paymentMethod) {
        return isCardPaymentMethod(paymentMethod) ? accountMask.getNumber() : accountMask.getDisplayLabel();
    }

    /**
     * Create am expiry date string from the AccountMask.
     * If the AccountMask does not contain the expiryMonth and expiryYear values then return null.
     *
     * @param mask AccountMask containing the expiryMonth and expiryYear fields
     * @return the expiry date or null if it could not be created
     */
    public static String getExpiryDateString(AccountMask mask) {
        int month = toInt(mask.getExpiryMonth());
        int year = toInt(mask.getExpiryYear());
        if (month == 0 || year == 0) {
            return null;
        }
        return PaymentUtils.format("%1$02d / %2$d", month, (year % 100));
    }

    /**
     * Does the list of InputElements contain both the expiry month and year fields.
     *
     * @param elements list of input elements to check
     * @return true when there are both an expiry month and year
     */
    public static boolean containsExpiryDate(List<InputElement> elements) {
        boolean hasExpiryMonth = false;
        boolean hasExpiryYear = false;

        for (InputElement element : elements) {
            switch (element.getName()) {
                case PaymentInputType.EXPIRY_MONTH:
                    hasExpiryMonth = true;
                    break;
                case PaymentInputType.EXPIRY_YEAR:
                    hasExpiryYear = true;
            }
        }
        return hasExpiryYear && hasExpiryMonth;
    }

    /**
     * Create a full expiry year from the last part of the expiry year.
     * This will use dynamic windowing of -30 years and +70 year.
     *
     * @param inputYear the year which the user entered, e.g. 1 or 99
     * @return complete expiry year value
     */
    public static int createExpiryYear(int inputYear) {
        if (inputYear < 0 || inputYear >= 100) {
            throw new IllegalArgumentException("Input year must be >= 0 and < 100: " + inputYear);
        }
        final int curYear = Calendar.getInstance().get(Calendar.YEAR);
        final int startYear = curYear - 30;
        final int endYear = curYear + 70;

        int year = inputYear > (startYear % 100) ? startYear : endYear;
        return (year - (year % 100)) + inputYear;
    }

    /**
     * Read the contents of the raw resource
     *
     * @param res The system Resources
     * @param resId The resource id
     * @return The String or an empty string if something went wrong
     */
    public static String readRawResource(Resources res, int resId) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        try (InputStream is = res.openRawResource(resId);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr)) {

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Resources.NotFoundException e) {
            throw new IOException("Resource not found: " + resId);
        }
        return sb.toString();
    }

    /**
     * Get the parameter value given the key of the parameter
     *
     * @param key name of the parameter
     * @param parameters list of parameters to search through
     * @return the value of the parameter or null if the parameter does not exist
     */
    public static String getParameterValue(String key, List<Parameter> parameters) {
        if (parameters == null) {
            return null;
        }
        for (Parameter parameter : parameters) {
            if (key.equals(parameter.getName())) {
                return parameter.getValue();
            }
        }
        return null;
    }

    /**
     * Return an empty list when the provided list is null.
     *
     * @param list to be checked if null
     * @return the list if not null, else return an empty list
     */
    public static <T> List<T> emptyListIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * Return an empty map when the provided map is null.
     *
     * @param map to be checked if null
     * @return the map if not null, else return an empty map
     */
    public static <K, V> Map<K, V> emptyMapIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * Set the test Id to the view with the proper formatting understood by the automated UI tests.
     *
     * @param view in which the tag should be set
     * @param type the type of the View, i.e. widget, networkcard.
     * @param name the name of the view i.e. holderName
     */
    public static void setTestId(View view, String type, String name) {
        view.setContentDescription(type + "." + name);
    }
}
