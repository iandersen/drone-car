package com.htmlhigh5.vehicle;

import java.util.ArrayList;

import org.apache.commons.configuration2.Configuration;

import com.htmlhigh5.debug.ConfigErrorException;
import com.htmlhigh5.debug.Debug;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GPIOComponent{
	private GPIOType type;
	private String name;
	private int pin;
	public IntegerProperty valueProperty = new SimpleIntegerProperty(0);
	public Configuration config;
	private int maxValue = -1;
	private int minValue = -1;
	public int id;
	private static int instances = 0;

	public GPIOComponent(Configuration c) {
		this.type = gpioTypeFromString(c.getString("TYPE"));
		pin = c.getInt("PIN");
		name = c.getString("NAME");
		maxValue = c.containsKey("MAX_PW") ? c.getInt("MAX_PW") : -1;
		minValue = c.containsKey("MIN_PW") ? c.getInt("MIN_PW") : -1;
		valueProperty.set(c.containsKey("MIN_PW") ? c.getInt("RESTING_PW") : 201);
		config = c;
		this.id = GPIOComponent.instances++;
	}

	public int getPin() {
		return this.pin;
	}

	public String getName() {
		return this.name;
	}

	public int getMin() {
		return this.minValue;
	}

	public int getMax() {
		return this.maxValue;
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
				throw new ConfigErrorException("Unknown GPIOComponent type from config: " + s);
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
	public synchronized void setValue(int value) throws BadGPIOValueException {
		if (value < 0 || value > 202)
			throw new BadGPIOValueException(value);
		this.valueProperty.set(value);
	}

	public synchronized void turnOn() throws BadGPIOValueException {
		this.setValue(202);
	}

	public synchronized void turnOff() throws BadGPIOValueException {
		this.setValue(201);
	}

	public boolean isOn() {
		return this.valueProperty.get() == 202;
	}

	public int getValue() {
		return this.valueProperty.get();
	}

	public synchronized void toggle() {
		this.valueProperty.set(this.valueProperty.get() == 201 ? 202 : 201);
	}

	public void modify(int val) {
		try {
			int newValue = this.getValue() + val;
			newValue = newValue > this.minValue ? newValue : this.minValue;
			newValue = newValue < this.maxValue ? newValue : this.maxValue;
			setValue(newValue);
		} catch (BadGPIOValueException e) {
			Debug.printStackTrace(e);
		}
	}

	public void noKeyEvent() {
		switch (this.type) {
		case CAR_ESC:
			try {
				this.setValue(100);
			} catch (BadGPIOValueException e) {
				Debug.printStackTrace(e);
			}
			break;
		case PLANE_ESC:
			try {
				this.setValue(100);
			} catch (BadGPIOValueException e) {
				Debug.printStackTrace(e);
			}
			break;
		case SERVO:
			System.out.println("NO KEY");
			try {
				this.setValue(100);
			} catch (BadGPIOValueException e) {
				Debug.printStackTrace(e);
			}
			break;
		case TOGGLE:
			break;
		case MOTOR:
			break;
		}
	}

	public void handleKeyDown(ArrayList<String> keys) {
		switch (this.type) {
		case CAR_ESC:
			if (keys.contains(config.getString("ACCELERATE_KEY").toLowerCase()))
				this.modify(config.getInt("ACCELERATION_SPEED"));
			else if (keys.contains(config.getString("DECCELERATE_KEY").toLowerCase()))
				this.modify(-config.getInt("ACCELERATION_SPEED"));
			else {
				try {
					this.setValue(config.getInt("RESTING_PW"));
				} catch (BadGPIOValueException e) {
					Debug.printStackTrace(e);
				}
			}
			break;
		case PLANE_ESC:
			if (keys.contains(config.getString("ACCELERATE_KEY").toLowerCase()))
				this.modify(config.getInt("ACCELERATION_SPEED"));
			else if (keys.contains(config.getString("DECCELERATE_KEY").toLowerCase()))
				this.modify(-config.getInt("ACCELERATION_SPEED"));
			else
				try {
					this.setValue(config.getInt("RESTING_PW"));
				} catch (BadGPIOValueException e) {
					Debug.printStackTrace(e);
				}
			break;
		case SERVO:
			if (keys.contains(config.getString("CLOCKWISE_KEY").toLowerCase()))
				this.modify(-config.getInt("ROTATION_SPEED"));
			else if (keys.contains(config.getString("COUNTERCLOCKWISE_KEY").toLowerCase()))
				this.modify(config.getInt("ROTATION_SPEED"));
			else
				try {
					this.setValue(config.getInt("RESTING_PW"));
				} catch (BadGPIOValueException e) {
					Debug.printStackTrace(e);
				}
			break;
		case TOGGLE:
			break;
		case MOTOR:
			break;
		}
	}

	public void handleKeyPressed(String key) {
		switch (this.type) {
		case CAR_ESC:
			break;
		case PLANE_ESC:
			break;
		case SERVO:
			break;
		case TOGGLE:
			if (key.equalsIgnoreCase(config.getString("TOGGLE_KEY"))) {
				this.toggle();
			}
			break;
		case MOTOR:
			break;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GPIOComponent)
			return ((GPIOComponent) o).id == this.id;
		return false;
	}

	@Override
	public int hashCode() {
		return this.id;
	}
}
