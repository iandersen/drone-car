package com.htmlhigh5.vehicle;

import org.apache.commons.configuration2.Configuration;

import com.htmlhigh5.debug.ConfigErrorException;
import com.htmlhigh5.debug.Debug;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GPIOComponent {
	private GPIOType type;
	private String name;
	private int pin;
	private int value;
	private Configuration config;
	private int maxValue = -1;
	private int minValue = -1;

	public GPIOComponent(Configuration c) {
		this.type = gpioTypeFromString(c.getString("TYPE"));
		pin = c.getInt("PIN");
		name = c.getString("NAME");
		maxValue = c.getInt("MAX_PW");
		minValue = c.getInt("MIN_PW");
		config = c;
	}

	private GPIOType gpioTypeFromString(String s) {
		switch (s.toUpperCase()) {
			case "CAR_ESC":
				return GPIOType.CAR_ESC;
			case "PLANE_ESC":
				return GPIOType.PLANE_ESC;
			case "SERVO":
				return GPIOType.SERVO;
			case "TOGGLE":
				return GPIOType.TOGGLE;
			case "MOTOR":
				return GPIOType.MOTOR;
			default:
				try {
					throw new ConfigErrorException(
					        "Unknown GPIOComponent type from config: " + s);
				} catch (ConfigErrorException e) {
					Debug.printStackTrace(e);
				}
				return GPIOType.MOTOR;
		}
	}

	public void setType(GPIOType type) {
		this.type = type;
	}

	public GPIOType getType() {
		return this.type;
	}

	// This is what the keyboard presses and releases will have to trigger
	public void setValue(int value) throws BadGPIOValueException {
		if (value < 0 || value > 102)
			throw new BadGPIOValueException(value);
		this.value = value;
	}

	public void turnOn() throws BadGPIOValueException {
		this.setValue(102);
	}

	public void turnOff() throws BadGPIOValueException {
		this.setValue(101);
	}

	public int getValue() {
		return this.value;
	}

	public void toggle() {
		this.value = this.value == 101 ? 102 : 101;
	}

	public void handleKeyEvent(String key) {
		switch (this.type) {
			case CAR_ESC:
				
			break;
			case PLANE_ESC:
			break;
			case SERVO:
			break;
			case TOGGLE:
			break;
			case MOTOR:
			break;
		}
	}
}
