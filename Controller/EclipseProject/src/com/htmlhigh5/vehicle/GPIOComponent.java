package com.htmlhigh5.vehicle;


public class GPIOComponent {
	private GPIOType type;
	private int value;

	public GPIOComponent(GPIOType type) {
		this.type = type;
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
	
	public void turnOn() throws BadGPIOValueException{
		this.setValue(102);
	}
	
	public void turnOff() throws BadGPIOValueException{
		this.setValue(101);
	}

	public int getValue() {
		return this.value;
	}

	public void toggle() {
		this.value = this.value == 101 ? 102 : 101;
	}
}
