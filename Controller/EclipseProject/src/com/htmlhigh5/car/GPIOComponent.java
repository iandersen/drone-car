package com.htmlhigh5.car;

import java.util.LinkedList;

import com.htmlhigh5.debug.Debug;

public class GPIOComponent {
	private int numPins;
	private LinkedList<GPIOPin> pins;

	public int getNumPins() {
		return this.numPins;
	}

	public void setNumPins(int numPins) {
		this.numPins = numPins;
	}

	public LinkedList<GPIOPin> getPins() {
		return this.pins;
	}

	public void addPin(GPIOPin pin) {
		if (this.pins.size() < numPins && !this.pins.contains(pin))
			this.pins.add(pin);
		else {
			Debug.log(Debug.error("Failed to add pin! Pin: " + pin));
		}
	}
}
