package com.htmlhigh5.network;

import com.htmlhigh5.Main;

public class ControlPacket {
	public static final int MAX_PINS = Main.config.getInt("GPIO_PINS");
	
	private boolean[] data;
	private PinCommand[] pinCommands;
	
	public ControlPacket(){
		data = new boolean[PinCommand.SIZE * MAX_PINS];
		pinCommands = new PinCommand[MAX_PINS];
	}
	
	public void setPin(int pin, int value){
		try {
			pinCommands[pin] = new PinCommand(value);
		} catch (BadPacketSizeException e) {
			e.printStackTrace();
		}
	}
	
	public int getPinValue(int pin){
		PinCommand pc = pinCommands[pin];
		return pc != null ? pc.getValue() : null;
	}
	
	public void finalizeData(){
		for(int pin = 0; pin < MAX_PINS; pin++){
			PinCommand pc = pinCommands[pin];
			if(pc != null)
				for (int i = pin * PinCommand.SIZE; i < (pin + 1) * PinCommand.SIZE; i++)
					data[i] = pc.content[i - PinCommand.SIZE];
		}
	}
	
	public boolean[] getData(){
		return data;
	}
}
