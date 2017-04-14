package com.htmlhigh5.network;

public class PinCommand {
	public static final int SIZE = 7; //7 bit packets (value 0-127)
	boolean[] content = new boolean[SIZE];
	private int value;
	
	public PinCommand(int value) throws BadPacketSizeException{
		this.value = value;
		if (value < 0 || value > 100)
			throw new BadPacketSizeException(value);
		else{
		    for (int i = SIZE-1; i >= 0; i--) 
		        content[i] = (value & (1 << i)) != 0;
		}
	}
	
	public int getValue(){
		return value;
	}
}
