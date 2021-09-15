/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.ui.list;

import com.payoneer.checkout.R;
import com.payoneer.checkout.util.PaymentUtils;

import android.view.View;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

/**
 * Class displaying the icon in a payment card
 */
class IconView {
    private final ViewSwitcher switcher;
    private final ImageView iconView0;
    private final ImageView iconView1;

    /**
     * Construct a new IconView
     *
     * @param parent of this view
     */
    IconView(View parent) {
        switcher = parent.findViewById(R.id.viewswitcher_icon);
        iconView0 = parent.findViewById(R.id.image_icon0);
        iconView1 = parent.findViewById(R.id.image_icon1);
        PaymentUtils.setTestId(switcher, "card", "iconview");
    }

    void setIconResource(int index, int resourceId) {
        if (index == 0) {
            iconView0.setImageResource(resourceId);
        } else {
            iconView1.setImageResource(resourceId);
        }
    }

    void setListener(final IconClickListener listener) {
        switcher.setClickable(true);
        switcher.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                listener.onIconClick(switcher.getDisplayedChild());
            }
        });
    }

    void setVisible(boolean visible) {
        switcher.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    void showIcon(int index) {
        if (switcher.getDisplayedChild() != index) {
            switcher.setDisplayedChild(index);
        }
    }

    interface IconClickListener {
        void onIconClick(int index);
    }
}
