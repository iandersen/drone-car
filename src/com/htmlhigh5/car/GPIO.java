package com.htmlhigh5.car;

import java.util.LinkedList;

public abstract class GPIO {
	private LinkedList<GPIOPin> pins;
	
	public LinkedList<GPIOPin> getPins(){
		return this.pins;
	}
	
	public void addPin(GPIOPin pin){
		if(!this.pins.contains(pin))
			this.pins.add(pin);
	}
}
