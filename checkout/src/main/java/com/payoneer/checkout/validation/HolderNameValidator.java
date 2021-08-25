/*
 * Copyright (c) 2021 Payoneer Germany GmbH
 * https://payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.validation;

/**
 * Class for validating the HolderName value.
 */
public final class HolderNameValidator {
	private static final int MIN_LENGTH = 3;

    /** 
     * Validate if the provided holderName is valid
     * 
     * @param holderName to the validated
     * @return true when holderName is valid, false otherwise 
     */
    public static boolean isValidHolderName(String holderName) {
        if (holderName == null || holderName.length() < MIN_LENGTH) {
            return false;
        }
        return !CardNumberMatcher.match(holderName);
    }

	/**
	 * Linear search for card-number-like substrings in a given input string.
	 * Used for catching accidental input of credit card numbers into the holder name field.
	 * A card-number-like string is a sequence of digits, possibly separated by an arbitrary number of
	 * whitespaces, dashes and/or dots, that contains at least 11 digits, e.g.:
     *
	 * 12345678901
	 * 1234 5678 9012 3456
	 * 1234-5678-9012-3456
	 * 1234.5678.9012.3456
     *
     * The performance of this check degrades linearly with the increase of the input size.
     * Therefore this validation rule should ideally be checked last or a limit must be set on the 
     * holderName input length.
	 */
	public static class CardNumberMatcher {

		public static final int NUMBER_OF_DIGITS_TO_MATCH = 11;

		public static boolean match(final String input) {
			final int inputLength = input.length();

			int i = 0;
			while (i < inputLength) {
				if (Character.isDigit(input.charAt(i))) {
					i = handleFirstDigit(input, i);
					if (i < 0) {
						return true;
					}
				}

				++i;
			}

			return false;
		}

		private static int handleFirstDigit(final String input, final int atIndex) {
			final int inputLength = input.length();

			int matchCount = 1;
			for (int i = atIndex + 1; i < inputLength; ++i) {
				final char c = input.charAt(i);

				if (Character.isDigit(c)) {
					++matchCount;
					if (matchCount == NUMBER_OF_DIGITS_TO_MATCH) {
						return -1; // found
					}
				} else if ((c != '-') && (c != '.') && !Character.isWhitespace(c)) {
					return i; // index with a non-matching char
				}
			}

			return inputLength; // not found
		}
	}
}
