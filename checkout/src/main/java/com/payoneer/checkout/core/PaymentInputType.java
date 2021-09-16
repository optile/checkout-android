/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

/**
 * Class containing the different payment input types i.e. number, iban, bic, allowRecurrence etc.
 */
public class PaymentInputType {
    public final static String HOLDER_NAME = "holderName";
    public final static String ACCOUNT_NUMBER = "number";
    public final static String BANK_CODE = "bankCode";
    public final static String BANK_NAME = "bankName";
    public final static String BIC = "bic";
    public final static String BRANCH = "branch";
    public final static String CITY = "city";
    public final static String EXPIRY_MONTH = "expiryMonth";
    public final static String EXPIRY_YEAR = "expiryYear";
    public final static String EXPIRY_DATE = "expiryDate";
    public final static String IBAN = "iban";
    public final static String LOGIN = "login";
    public final static String OPTIN = "optIn";
    public final static String PASSWORD = "password";
    public final static String VERIFICATION_CODE = "verificationCode";
    public final static String CUSTOMER_BIRTHDAY = "customerBirthDay";
    public final static String CUSTOMER_BIRTHMONTH = "customerBirthMonth";
    public final static String CUSTOMER_BIRTHYEAR = "customerBirthYear";
    public final static String INSTALLMENT_PLANID = "installmentPlanId";
    public final static String ALLOW_RECURRENCE = "allowRecurrence";
    public final static String AUTO_REGISTRATION = "autoRegistration";
    public final static String REGISTRATION_OPTIONS = "registrationOptions";

    /**
     * Check if the given type is a valid payment input type
     *
     * @param type the payment input type to validate
     * @return true when valid, false otherwise
     */
    public static boolean isValid(final String type) {

        if (type != null) {
            switch (type) {
                case HOLDER_NAME:
                case ACCOUNT_NUMBER:
                case BANK_CODE:
                case BANK_NAME:
                case BIC:
                case BRANCH:
                case CITY:
                case EXPIRY_MONTH:
                case EXPIRY_YEAR:
                case EXPIRY_DATE:
                case IBAN:
                case LOGIN:
                case OPTIN:
                case PASSWORD:
                case VERIFICATION_CODE:
                case CUSTOMER_BIRTHDAY:
                case CUSTOMER_BIRTHMONTH:
                case CUSTOMER_BIRTHYEAR:
                case INSTALLMENT_PLANID:
                case ALLOW_RECURRENCE:
                case AUTO_REGISTRATION:
                    return true;
            }
        }
        return false;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
        HOLDER_NAME,
        ACCOUNT_NUMBER,
        BANK_CODE,
        BANK_NAME,
        BIC,
        BRANCH,
        CITY,
        EXPIRY_MONTH,
        EXPIRY_YEAR,
        EXPIRY_DATE,
        IBAN,
        LOGIN,
        OPTIN,
        PASSWORD,
        VERIFICATION_CODE,
        CUSTOMER_BIRTHDAY,
        CUSTOMER_BIRTHMONTH,
        CUSTOMER_BIRTHYEAR,
        INSTALLMENT_PLANID,
        ALLOW_RECURRENCE,
        AUTO_REGISTRATION
    })
    public @interface Definition { }
}
