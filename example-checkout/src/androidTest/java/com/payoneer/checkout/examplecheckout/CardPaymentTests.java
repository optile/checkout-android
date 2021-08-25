/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.examplecheckout;

import static com.payoneer.checkout.sharedtest.checkout.MagicNumbers.CHARGE_PROCEED_OK;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.payoneer.checkout.model.InteractionCode;
import com.payoneer.checkout.model.InteractionReason;
import com.payoneer.checkout.sharedtest.checkout.ChargePaymentHelper;
import com.payoneer.checkout.sharedtest.checkout.MagicNumbers;
import com.payoneer.checkout.sharedtest.checkout.PaymentDialogHelper;
import com.payoneer.checkout.sharedtest.checkout.PaymentListHelper;
import com.payoneer.checkout.sharedtest.checkout.TestDataProvider;
import com.payoneer.checkout.sharedtest.service.ListSettings;

import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public final class CardPaymentTests extends AbstractTest {

    @Rule
    public ActivityTestRule<ExampleCheckoutActivity> rule = new ActivityTestRule<>(ExampleCheckoutActivity.class);

    @Test
    public void testVisaCard_PROCEED_OK() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        ListSettings settings = createDefaultListSettings();
        settings.setAmount(CHARGE_PROCEED_OK);
        enterListUrl(createListUrl(settings));
        clickActionButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.OK);
        unregister(resultIdlingResource);
    }

    @Test
    public void testVisaCard_PROCEED_PENDING() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_PROCEED_PENDING);
        enterListUrl(createListUrl(settings));
        clickActionButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");
        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.PROCEED, InteractionReason.PENDING);
        unregister(resultIdlingResource);
    }

    @Test
    public void testVisaCard_RETRY() {
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_RETRY);
        enterListUrl(createListUrl(settings));
        clickActionButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        ChargePaymentHelper.waitForChargePaymentDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.matchesInputTextInWidget(groupCardIndex, "number", "4111 1111 1111 1111");
    }

    @Test
    public void testVisaCard_TRY_OTHER_NETWORK() {
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_TRY_OTHER_NETWORK);
        enterListUrl(createListUrl(settings));
        clickActionButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        ChargePaymentHelper.waitForChargePaymentDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 2);
    }

    @Test
    public void testVisaCard_TRY_OTHER_ACCOUNT() {
        ListSettings settings = createDefaultListSettings();
        settings.setAmount(MagicNumbers.CHARGE_TRY_OTHER_ACCOUNT);
        enterListUrl(createListUrl(settings));
        clickActionButton();

        int groupCardIndex = 1;

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.visaCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        ChargePaymentHelper.waitForChargePaymentDialog();
        PaymentDialogHelper.clickPaymentDialogButton("OK");

        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");
        PaymentListHelper.matchesCardGroupCount(groupCardIndex, 3);
    }

    @Test
    public void testRiskDeniedCard_ABORT() {
        IdlingResource resultIdlingResource = getResultIdlingResource();

        enterListUrl(createListUrl());
        clickActionButton();

        int groupCardIndex = 1;
        PaymentListHelper.waitForPaymentListLoaded(1);
        PaymentListHelper.openPaymentListCard(groupCardIndex, "card_group");

        PaymentListHelper.fillPaymentListCard(groupCardIndex, TestDataProvider.riskDeniedCardTestData());
        PaymentListHelper.clickPaymentListCardButton(groupCardIndex);

        register(resultIdlingResource);
        matchResultInteraction(InteractionCode.ABORT, InteractionReason.RISK_DETECTED);
        unregister(resultIdlingResource);
    }
}

