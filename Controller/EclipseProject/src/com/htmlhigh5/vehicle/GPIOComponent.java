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
	
	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	
	public void toggle() {
		this.value = this.value == 100 ? 0 : 100;
	}
}
