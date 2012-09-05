/*
 * AlphanumericComparator.java - An alphanumeric comparator - file12 sorts after
 * file2
 * 
 * Copyright 2008 Andrew Duffy
 * http://github.com/amjjd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.amjjd.alphanum;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Sorts alphanumeric strings in a natural order - numbers are sorted
 * numerically and other text is sorted by a given comparator.
 */
public class AlphanumericComparator implements Comparator<String>
{
	/** Used to compare non-numeric strings. */
	private Comparator<? super String> collator;

	/**
	 * Creates an alphanumeric comparator that compares the
	 * {@linkplain Object#toString() string representation} of each supplied
	 * object.
	 * 
	 * @param collator Used to compare non-numeric substrings
	 */
	public AlphanumericComparator(Comparator<? super String> collator)
	{
		this.collator = collator;
	}
	
	/**
	 * Creates a default comparator for the given locale; this is likely to be
	 * case- and diacritic-insensitive and will not normalise strings.
	 *  
	 * @param locale The locale
	 * @return A default comparator for the locale
	 */
	public static AlphanumericComparator forLocale(Locale locale)
	{
		Collator collator = Collator.getInstance(locale);
		collator.setDecomposition(Collator.NO_DECOMPOSITION);
		collator.setStrength(Collator.PRIMARY);
		return new AlphanumericComparator(collator);
	}

	@Override
	public int compare(String str1, String str2)
	{
		int[] pos = {0, 0};

		if(str1.length() == 0)
			return str2.length() == 0 ? 0 : -1;
		else if(str2.length() == 0)
			return 1;

		while(pos[0] < str1.length() && pos[1] < str2.length())
		{
			int ch1 = str1.codePointAt(pos[0]);
			int ch2 = str2.codePointAt(pos[1]);

			int result = 0;

			if(isDigit(ch1))
				result = isDigit(ch2) ? compareNumbers(str1, str2, pos) : -1;
			else
				result = isDigit(ch2) ? 1 : compareNonNumeric(str1, str2, pos);

			if(result != 0)
				return result;
		}

		return str1.length() - str2.length();
	}

	private int compareNumbers(String str0, String str1, int[] pos)
	{
		int delta = 0;
		int zeroes0 = 0, zeroes1 = 0;
		int ch0 = -1, ch1 = -1;

		// Skip leading zeroes, but keep a count of them.
		while(pos[0] < str0.length() && isZero(ch0 = str0.codePointAt(pos[0])))
		{
			zeroes0++;
			pos[0] += Character.charCount(ch0);
		}
		while(pos[1] < str1.length() && isZero(ch1 = str1.codePointAt(pos[1])))
		{
			zeroes1++;
			pos[1] += Character.charCount(ch1);
		}

		// If one sequence contains more significant digits than the
		// other, it's a larger number. In case they turn out to have
		// equal lengths, we compare digits at each position; the first
		// unequal pair determines which is the bigger number.
		while(true)
		{
			boolean noMoreDigits0 = (ch0 < 0) || !isDigit(ch0);
			boolean noMoreDigits1 = (ch1 < 0) || !isDigit(ch1);

			if(noMoreDigits0 && noMoreDigits1)
				return delta != 0 ? delta : zeroes0 - zeroes1;
			else if(noMoreDigits0)
				return -1;
			else if(noMoreDigits1)
				return 1;
			else if(delta == 0 && ch0 != ch1)
				delta = valueOf(ch0) - valueOf(ch1);

			if(pos[0] < str0.length())
			{
				ch0 = str0.codePointAt(pos[0]);
				if(isDigit(ch0))
					pos[0] += Character.charCount(ch0);
				else
					ch0 = -1;
			}
			else
				ch0 = -1;
			
			if(pos[1] < str1.length())
			{
				ch1 = str1.codePointAt(pos[1]);
				if(isDigit(ch1))
					pos[1] += Character.charCount(ch1);
				else
					ch1 = -1;
			}
			else
				ch1 = -1;
		}
	}

	private static boolean isDigit(int ch)
	{
		return (ch >= '0' && ch <= '9') ||
		       (ch >= '\u0660' && ch <= '\u0669') ||
		       (ch >= '\u06F0' && ch <= '\u06F9') ||
		       (ch >= '\u0966' && ch <= '\u096F') ||
		       (ch >= '\uFF10' && ch <= '\uFF19');
	}
	
	private static boolean isZero(int ch)
	{
		return ch == '0' || ch == '\u0660' || ch == '\u06F0' || ch == '\u0966' || ch == '\uFF10';
	}

	private static int valueOf(int digit)
	{
		if(digit <= '9')
			return digit - '0';
		if(digit <= '\u0669')
			return digit - '\u0660';
		if(digit <= '\u06F9')
			return digit - '\u06F0';
		if(digit <= '\u096F')
			return digit - '\u0966';
		if(digit <= '\uFF19')
			return digit - '\uFF10';
		
		return digit;
	}

	private int compareNonNumeric(String str0, String str1, int[] pos)
	{
		// find the end of both non-numeric substrings
		int start0 = pos[0];
		int ch0 = str0.codePointAt(pos[0]);
		pos[0] += Character.charCount(ch0);
		while(pos[0] < str0.length() && !isDigit(ch0 = str0.codePointAt(pos[0])))
			pos[0] += Character.charCount(ch0);
		
		int start1 = pos[1];
		int ch1 = str1.codePointAt(pos[1]);
		pos[1] += Character.charCount(ch1);
		while(pos[1] < str1.length() && !isDigit(ch1 = str1.codePointAt(pos[1])))
			pos[1] += Character.charCount(ch1);
		
		// compare the substrings
		return collator.compare(str0.substring(start0, pos[0]), str1.substring(start1, pos[1]));
	}
}