package de.tudresden.inf.lat.wikihypergraph.module;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An object of this class manages strings representing page identifiers as
 * integers. These identifiers can only be of the form P<i>x</i> or Q<i>x</i>
 * where <i>x</i> is an integer number.
 * 
 * @author Julian Mendez
 *
 */
public class IntegerManager {

	public static final String NEGATIVE_NUMBER_PREFIX = "P";
	public static final String POSITIVE_NUMBER_PREFIX = "Q";

	/**
	 * Constructs a new integer manager.
	 */
	public IntegerManager() {
	}

	/**
	 * Tells whether the given string is valid to be managed by this manager.
	 * 
	 * @param str
	 *            string
	 * @return <code>true</code> if and only if the given string is valid to be
	 *         managed by this manager
	 */
	public boolean isValid(String str) {
		return asNumberX(str) != 0;

	}

	/**
	 * Return a number representing the given string. The string has to be of
	 * the form P<i>x</i> or Q<i>x</i> where <i>x</i> is an integer number.
	 * Number 0 is reserved for invalid strings.
	 * 
	 * @param str
	 *            string
	 * @return a number representing the given string
	 */
	int asNumberX(String str) {
		if (str == null) {
			return 0;
		}

		int number = 0;
		try {
			if (str.startsWith(NEGATIVE_NUMBER_PREFIX)) {
				number = (-1) * Integer.parseInt(str.substring(NEGATIVE_NUMBER_PREFIX.length()));
			} else if (str.startsWith(POSITIVE_NUMBER_PREFIX)) {
				number = Integer.parseInt(str.substring(POSITIVE_NUMBER_PREFIX.length()));
			}
		} catch (NumberFormatException e) {
		}
		return number;
	}

	/**
	 * Return a number representing the given string. The string has to be of
	 * the form P<i>x</i> or Q<i>x</i> where <i>x</i> is an integer number.
	 * 
	 * @param str
	 *            string
	 * @return a number representing the given string
	 */
	public int asNumber(String str) {
		if (str == null) {
			throw new IllegalArgumentException("String cannot be null.");
		}

		int ret = asNumberX(str);
		if (ret == 0) {
			throw new IllegalArgumentException("Invalid identifier: '" + str + "'.");
		}
		return ret;
	}

	/**
	 * Returns the string represented the given number. The number cannot be 0.
	 * 
	 * @param number
	 *            number
	 * @return the string represented the given number
	 */
	public String asString(int number) {
		if (number > 0) {
			return POSITIVE_NUMBER_PREFIX + number;
		} else if (number < 0) {
			return NEGATIVE_NUMBER_PREFIX + ((-1) * number);
		} else {
			throw new IllegalArgumentException("Number cannot be 0");
		}
	}

	/**
	 * Returns a collection of numbers representing the given collection of
	 * strings.
	 * 
	 * @param collection
	 *            collection of strings
	 * @return a collection of numbers representing the given collection of
	 *         strings
	 */
	public Collection<Integer> asNumber(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		Collection<Integer> ret = new ArrayList<Integer>();
		for (String str : collection) {
			ret.add(asNumber(str));
		}
		return ret;
	}

	/**
	 * Returns a collection of strings represented by the given collection of
	 * numbers.
	 * 
	 * @param collection
	 *            collection of numbers
	 * @return a collection of strings represented by the given collection of
	 *         numbers
	 */
	public Collection<String> asString(Collection<Integer> collection) {
		if (collection == null) {
			return null;
		}
		Collection<String> ret = new ArrayList<String>();
		for (Integer number : collection) {
			ret.add(asString(number));
		}
		return ret;
	}

}
