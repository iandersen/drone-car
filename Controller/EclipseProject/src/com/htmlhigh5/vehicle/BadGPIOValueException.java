package com.htmlhigh5.vehicle;

public class BadGPIOValueException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7927993823659889751L;

	public BadGPIOValueException(Object message) {
		super("Bad GPIO Pin Value! Value must be in range 0-100. Value Received: " + message);
	}

}
