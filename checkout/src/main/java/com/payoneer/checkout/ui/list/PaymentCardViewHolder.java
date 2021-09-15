/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;

import static com.payoneer.checkout.core.PaymentInputCategory.EXTRAELEMENT;
import static com.payoneer.checkout.core.PaymentInputCategory.INPUTELEMENT;
import static com.payoneer.checkout.core.PaymentInputType.EXPIRY_DATE;
import static com.payoneer.checkout.core.PaymentInputType.EXPIRY_MONTH;
import static com.payoneer.checkout.core.PaymentInputType.EXPIRY_YEAR;
import static com.payoneer.checkout.core.PaymentInputType.VERIFICATION_CODE;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.payoneer.checkout.R;
import com.payoneer.checkout.model.ExtraElement;
import com.payoneer.checkout.model.InputElement;
import com.payoneer.checkout.model.InputElementType;
import com.payoneer.checkout.ui.model.PaymentCard;
import com.payoneer.checkout.ui.widget.ButtonWidget;
import com.payoneer.checkout.ui.widget.ExpiryDateWidget;
import com.payoneer.checkout.ui.widget.ExtraElementWidget;
import com.payoneer.checkout.ui.widget.FormWidget;
import com.payoneer.checkout.ui.widget.SelectWidget;
import com.payoneer.checkout.ui.widget.TextInputWidget;
import com.payoneer.checkout.ui.widget.VerificationCodeWidget;
import com.payoneer.checkout.util.NetworkLogoLoader;
import com.payoneer.checkout.util.PaymentUtils;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The PaymentCardViewHolder holding the header and input widgets
 */
public abstract class PaymentCardViewHolder extends RecyclerView.ViewHolder {
    final static String UIELEMENT = "uielement";
    final static String NETWORKLOGOS = "networklogos";
    final static String BUTTON = "button";

    final ViewGroup formLayout;
    final Map<String, FormWidget> widgets;
    final ImageView cardLogoView;
    final PaymentCard paymentCard;
    final CardEventHandler cardHandler;
    final ListAdapter adapter;

    /**
     * Construct a new PaymentCardViewHolder, this is the base class for other card ViewHolder
     *
     * @param adapter maintaining the items in the payment list
     * @param parent view of the list
     * @param paymentCard to be shown and bind to this ViewHolder
     */
    PaymentCardViewHolder(ListAdapter adapter, View parent, PaymentCard paymentCard) {
        super(parent);
        this.adapter = adapter;
        this.paymentCard = paymentCard;
        this.cardHandler = new CardEventHandler(this, adapter);
        this.formLayout = parent.findViewById(R.id.layout_form);
        this.widgets = new LinkedHashMap<>();
        this.cardLogoView = parent.findViewById(R.id.image_logo);

        View view = parent.findViewById(R.id.layout_header);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCardClicked(v);
            }
        });
    }

    /**
     * Sub classes must implement this method and make sure that all FormWidgets are
     * bound to the correct data elements.
     */
    abstract void onBind();

    PaymentCard getPaymentCard() {
        return paymentCard;
    }

    boolean hasUserInputData() {
        for (FormWidget widget : widgets.values()) {
            if (widget.hasUserInputData()) {
                return true;
            }
        }
        return false;
    }

    public FormWidget getFormWidget(String key) {
        return widgets.get(key);
    }

    Map<String, FormWidget> getFormWidgets() {
        return widgets;
    }

    boolean hasValidPosition() {
        return adapter.validPosition(getAdapterPosition());
    }

    void addInputElementWidgets(List<InputElement> inputElements) {
        String code = paymentCard.getNetworkCode();
        boolean elementsContainExpiryDate = PaymentUtils.containsExpiryDate(inputElements);

        for (InputElement element : inputElements) {
            String name = element.getName();
            if (cardHandler.isInputTypeHidden(code, name)) {
                continue;
            }
            switch (element.getName()) {
                case VERIFICATION_CODE:
                    addVerificationCodeWidget();
                    break;
                case EXPIRY_MONTH:
                case EXPIRY_YEAR:
                    if (elementsContainExpiryDate) {
                        addExpiryDateWidget();
                        break;
                    }
                default:
                    addInputElementWidget(element);
            }
        }
    }

    void addInputElementWidget(InputElement element) {
        String category = INPUTELEMENT;
        String name = element.getName();
        FormWidget widget;
        switch (element.getType()) {
            case InputElementType.SELECT:
                widget = new SelectWidget(category, name);
                break;
            default:
                widget = new TextInputWidget(category, name);
                break;
        }
        putFormWidget(widget);
    }

    void addExtraElementWidgets(List<ExtraElement> extraElements) {
        for (ExtraElement element : extraElements) {
            addExtraElementWidget(element);
        }
    }

    void addExtraElementWidget(ExtraElement extraElement) {
        ExtraElementWidget widget = new ExtraElementWidget(EXTRAELEMENT, extraElement.getName());
        putFormWidget(widget);
    }

    void addButtonWidget() {
        ButtonWidget widget = new ButtonWidget(UIELEMENT, BUTTON);
        putFormWidget(widget);
    }

    void addVerificationCodeWidget() {
        VerificationCodeWidget widget = new VerificationCodeWidget(INPUTELEMENT, VERIFICATION_CODE);
        putFormWidget(widget);
    }

    void addExpiryDateWidget() {
        String category = INPUTELEMENT;
        String name = EXPIRY_DATE;
        String key = FormWidget.createWidgetKey(category, name);
        if (!widgets.containsKey(key)) {
            putFormWidget(new ExpiryDateWidget(category, name));
        }
    }

    void putFormWidget(FormWidget widget) {
        widget.setPresenter(cardHandler);
        widgets.put(widget.getKey(), widget);
    }

    void layoutWidgets() {
        ViewGroup rowLayout = null;
        boolean rowAdded = false;
        String category = INPUTELEMENT;

        if (checkLayoutWidgetsInRow(category, VERIFICATION_CODE, EXPIRY_DATE)) {
            LayoutInflater inflater = LayoutInflater.from(formLayout.getContext());
            rowLayout = (ViewGroup) inflater.inflate(R.layout.layout_widget_row, formLayout, false);
        }

        for (FormWidget widget : widgets.values()) {

            if (rowLayout != null && (widget.matches(category, VERIFICATION_CODE) || widget.matches(category, EXPIRY_DATE))) {
                layoutWidgetInRow(widget, rowLayout);
                if (!rowAdded) {
                    formLayout.addView(rowLayout);
                    rowAdded = true;
                }
                continue;
            }
            formLayout.addView(widget.inflate(formLayout));
        }
    }

    private void layoutWidgetInRow(FormWidget widget, ViewGroup rowLayout) {
        View view = widget.inflate(rowLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = 1;
        view.setLayoutParams(params);
        rowLayout.addView(view);
    }

    /**
     * Check if two widgets can be placed next to each other in a row
     *
     * @param category both widgets must belong to the same category
     * @param name1 contains key of the first widget
     * @param name2 contains keyey of the second widget
     * @return true when they can be combined into one row, false otherwise
     */
    private boolean checkLayoutWidgetsInRow(String category, String name1, String name2) {
        String nextName = null;
        for (FormWidget widget : widgets.values()) {
            if (nextName != null) {
                return widget.matches(category, nextName);
            }
            if (widget.matches(category, name1)) {
                nextName = name2;
            } else if (widget.matches(category, name2)) {
                nextName = name1;
            }
        }
        return false;
    }

    boolean requestFocusNextWidget(FormWidget currentWidget) {
        boolean requestFocus = false;
        for (Map.Entry<String, FormWidget> entry : widgets.entrySet()) {
            FormWidget widget = entry.getValue();
            if (requestFocus) {
                if (widget.requestFocus()) {
                    return true;
                }
            } else {
                requestFocus = (widget == currentWidget);
            }
        }
        return false;
    }

    void handleCardClicked(View view) {
        if (!paymentCard.isDisabled()) {
            cardHandler.onCardClicked();
        }
    }

    void expand(boolean expand) {
        if (paymentCard.getHideInputForm()) {
            formLayout.setVisibility(View.GONE);
            return;
        }
        formLayout.setVisibility(expand ? View.VISIBLE : View.GONE);
    }

    void bindFormWidget(FormWidget widget) {
        String category = widget.getCategory();

        if (widget.matches(UIELEMENT, BUTTON)) {
            bindButtonWidget((ButtonWidget) widget);
        } else if (EXTRAELEMENT.equals(category)) {
            bindExtraElementWidget((ExtraElementWidget) widget);
        } else if (INPUTELEMENT.equals(category)) {
            bindInputElementWidget(widget);
        } else {
            throw new IllegalStateException("Bind error for FormWidget with category: " + category);
        }
    }

    void bindInputElementWidget(FormWidget widget) {
        if (widget instanceof VerificationCodeWidget) {
            bindVerificationCodeWidget((VerificationCodeWidget) widget);
        } else if (widget instanceof ExpiryDateWidget) {
            bindExpiryDateWidget((ExpiryDateWidget) widget);
        } else if (widget instanceof SelectWidget) {
            bindSelectWidget((SelectWidget) widget);
        } else if (widget instanceof TextInputWidget) {
            bindTextInputWidget((TextInputWidget) widget);
        } else {
            throw new IllegalStateException("Bind error for InputElement widget with key: " + widget.getKey());
        }
    }

    void bindButtonWidget(ButtonWidget widget) {
        widget.onBind(paymentCard.getButton());
    }

    void bindVerificationCodeWidget(VerificationCodeWidget widget) {
        InputElement element = paymentCard.getInputElement(VERIFICATION_CODE);
        widget.onBind(paymentCard.getNetworkCode(), element);
    }

    void bindExpiryDateWidget(ExpiryDateWidget widget) {
        InputElement month = paymentCard.getInputElement(EXPIRY_MONTH);
        InputElement year = paymentCard.getInputElement(EXPIRY_YEAR);
        widget.onBind(paymentCard.getNetworkCode(), month, year);
    }

    void bindSelectWidget(SelectWidget widget) {
        InputElement element = paymentCard.getInputElement(widget.getName());
        String code = paymentCard.getNetworkCode();
        widget.onBind(code, element);
    }

    void bindTextInputWidget(TextInputWidget widget) {
        InputElement element = paymentCard.getInputElement(widget.getName());
        String code = paymentCard.getNetworkCode();
        widget.onBind(code, element);
    }

    void bindExtraElementWidget(ExtraElementWidget widget) {
        ExtraElement element = paymentCard.getExtraElement(widget.getName());
        widget.onBind(element);
    }

    void bindLabel(TextView view, String label, boolean hideWhenEmpty) {
        int visibility = (hideWhenEmpty && TextUtils.isEmpty(label)) ? View.GONE : View.VISIBLE;
        view.setVisibility(visibility);
        view.setText(label);
    }

    void bindCardLogo(int logoResId) {
        cardLogoView.setImageResource(logoResId);
    }

    void bindCardLogo(String networkCode, URL url) {
        if (networkCode == null || url == null) {
            return;
        }
        NetworkLogoLoader.loadNetworkLogo(cardLogoView, networkCode, url);
    }

    void setLastImeOptions() {
        List<String> keys = new ArrayList<>(widgets.keySet());
        Collections.reverse(keys);

        for (String key : keys) {
            FormWidget widget = widgets.get(key);
            if (widget != null && widget.setLastImeOptionsWidget()) {
                break;
            }
        }
    }

    String createWidgetKey(String inputCategory, String inputName) {
        return inputCategory + "." + inputName;
    }
}
