/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;

import com.google.android.material.card.MaterialCardView;
import com.payoneer.checkout.R;
import com.payoneer.checkout.ui.model.AccountCard;
import com.payoneer.checkout.ui.model.AccountCard.AccountIcon;
import com.payoneer.checkout.ui.widget.FormWidget;
import com.payoneer.checkout.util.PaymentUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/**
 * The AccountCardViewHolder class holding and binding views for an AccountCard
 */
public final class AccountCardViewHolder extends PaymentCardViewHolder {

    private final static int ICON_COLLAPSED = 0;
    private final static int ICON_EXPANDED = 1;

    private final TextView titleView;
    private final TextView subtitleView;
    private final IconView iconView;
    private final MaterialCardView cardView;

    private AccountCardViewHolder(ListAdapter listAdapter, View parent, AccountCard accountCard) {
        super(listAdapter, parent, accountCard);
        this.titleView = parent.findViewById(R.id.text_title);
        this.subtitleView = parent.findViewById(R.id.text_subtitle);

        iconView = new IconView(parent);
        iconView.setListener(new IconView.IconClickListener() {

            public void onIconClick(int index) {
                handleIconClicked(index);
            }
        });
        cardView = parent.findViewById(R.id.card_account);

        addExtraElementWidgets(accountCard.getTopExtraElements());
        addInputElementWidgets(accountCard.getInputElements());
        addExtraElementWidgets(accountCard.getBottomExtraElements());
        addButtonWidget();
        layoutWidgets();
        setLastImeOptions();
    }

    static ViewHolder createInstance(ListAdapter listAdapter, AccountCard accountCard, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_accountcard, parent, false);
        return new AccountCardViewHolder(listAdapter, view, accountCard);
    }

    @Override
    void onBind() {
        PaymentUtils.setTestId(itemView, "card", "account");
        AccountCard card = (AccountCard) paymentCard;
        cardView.setCheckable(card.isCheckable());

        bindLabel(titleView, card.getTitle(), false);
        bindLabel(subtitleView, card.getSubtitle(), true);
        bindCardLogo(card.getNetworkCode(), card.getLogoLink());

        for (FormWidget widget : widgets.values()) {
            bindFormWidget(widget);
        }
        bindAccountIcon(card.getAccountIcon());
    }

    @Override
    void expand(boolean expand) {
        super.expand(expand);
        AccountCard accountCard = (AccountCard) paymentCard;
        if (accountCard.isCheckable()) {
            cardView.setChecked(expand);
        }
        int icon = (expand) ? ICON_EXPANDED : ICON_COLLAPSED;
        iconView.showIcon(icon);
    }

    private void bindAccountIcon(AccountIcon icon) {
        if (icon != null) {
            iconView.setVisible(true);
            iconView.setIconResource(ICON_COLLAPSED, icon.getCollapsedResId());
            iconView.setIconResource(ICON_EXPANDED, icon.getExpandedResId());
        } else {
            iconView.setVisible(false);
        }
    }

    private void handleIconClicked(int index) {
        boolean deletable = ((AccountCard) paymentCard).isDeletable();
        if (index == ICON_EXPANDED && deletable) {
            cardHandler.onDeleteClicked();
        } else {
            cardHandler.onCardClicked();
        }
    }
}
