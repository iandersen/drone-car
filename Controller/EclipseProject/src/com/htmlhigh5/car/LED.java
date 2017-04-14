package com.htmlhigh5.car;

public class LED extends GPIOComponent {
	private LEDState state;

	public LED() {
		this.state = LEDState.OFF;
	}

	public LED(LEDState state) {
		this.state = state;
	}

	public LEDState getState() {
		return this.state;
	}

	public void setState(LEDState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "LED (" + this.state + ")";
	}
}
