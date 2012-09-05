/*
 * AlphanumericComparatorTest.java - JUnit tests for AlphanumericComparator.java
 * 
 * Copyright 2008 Andrew Duffy.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.junit.Test;

public class AlphanumericComparatorTest
{
	@Test
	public void test()
	{
		Comparator<String> comparator = new AlphanumericComparator(Collator.getInstance(Locale.ENGLISH));
		
		// equality
		assertEquals(0, comparator.compare("", ""));
		assertEquals(0, comparator.compare("abc", "abc"));
		assertEquals(0, comparator.compare("123", "123"));
		assertEquals(0, comparator.compare("abc123", "abc123"));
		
		// empty strings < non-empty
		assertTrue(comparator.compare("", "abc") < 0);
		assertTrue(comparator.compare("abc", "") > 0);
		
		// numbers < non numeric
		assertTrue(comparator.compare("123", "abc") < 0);
		assertTrue(comparator.compare("abc", "123") > 0);
		
		// numbers ordered numerically
		assertTrue(comparator.compare("2", "11") < 0);
		assertTrue(comparator.compare("a2", "a11") < 0);
		
		// leading zeroes
		assertTrue(comparator.compare("02", "11") < 0);
		assertTrue(comparator.compare("02", "002") < 0);
		
		// decimal points ... 
		assertTrue(comparator.compare("1.3", "1.5") < 0);
		
		// ... don't work too well
		assertTrue(comparator.compare("1.3", "1.15") < 0);
	}

	@Test
	public void testLettersAfterEqualNumbers()
	{
		Comparator<String> comparator = AlphanumericComparator.forLocale(Locale.ENGLISH);
		assertTrue(comparator.compare("2AZ", "2ZA") < 0);
	}

	@Test
	public void testDifferentLengthNumbers()
	{
		Comparator<String> comparator = AlphanumericComparator.forLocale(Locale.ENGLISH);
		assertTrue(comparator.compare("112A", "22A") > 0);
	}

	@Test
	public void testZeroes()
	{
		Comparator<String> comparator = AlphanumericComparator.forLocale(Locale.ENGLISH);
		assertTrue(comparator.compare("00000", "0000") > 0);
		assertTrue(comparator.compare("02", "1") > 0);
	}
}
