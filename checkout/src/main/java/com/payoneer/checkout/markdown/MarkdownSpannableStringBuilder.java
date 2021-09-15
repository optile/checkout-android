/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.markdown;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;

/**
 * Builder Class to create SpannableStringBuilder from a text with markdown links
 */
public final class MarkdownSpannableStringBuilder {

    // Named groups removed from regex to support API 25 and less.
    // Example regex with named groups:
    // "\\[(?<text>[^\\]]+)\\]\\((?<url>[^ \\]]+)(?:[ ]\"(?<title>[^\\]]+)\")?\\)"
    private final static String MARKDOWN_LINK_REGEX = "\\[([^]]+)]\\(([^ \\]]+)([ ]\"([^]]+)\")?\\)";
    private final static int GROUP_COUNT = 4;

    /**
     * Convenience static method to build a SpannableStringBuilder from the provided text.
     *
     * @param text to be parsed
     * @return newly created SpannableStringBuilder containing the parsed markdown text
     */
    public static SpannableStringBuilder createFromText(String text) {
        MarkdownSpannableStringBuilder builder = new MarkdownSpannableStringBuilder();
        return builder.build(text);
    }

    /**
     * Build a SpannableStringBuilder from the provided text containing optional markdown links
     *
     * @param text containing text and optional markdown links
     * @return SpannableStringBuilder containing text and optional link spans
     */
    public SpannableStringBuilder build(String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder();
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        return parseMarkdownLinks(builder);
    }

    private SpannableStringBuilder parseMarkdownLinks(SpannableStringBuilder builder) {
        Pattern p = Pattern.compile(MARKDOWN_LINK_REGEX);
        for (Matcher m = p.matcher(builder); m.find(); ) {
            parseMarkdownLink(m, builder);
        }
        return builder;
    }

    private SpannableStringBuilder parseMarkdownLink(Matcher matcher, SpannableStringBuilder builder) {
        if (matcher.groupCount() != GROUP_COUNT) {
            return builder;
        }
        String name = matcher.group(1);
        String url = matcher.group(2);

        // ignore this url if either name or url are empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url)) {
            return builder;
        }
        int start = matcher.start();
        int end = matcher.end();
        int regionEnd = start + name.length();

        ClickableSpan span = new MarkdownLinkSpan(url);
        builder.replace(start, end, name).setSpan(span, start, regionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        matcher.reset(builder);
        matcher.region(regionEnd, builder.length());
        return builder;
    }
}

