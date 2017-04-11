package com.htmlhigh5.gui;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.network.BadPacketSizeException;
import com.htmlhigh5.network.PinCommand;

public class Main {
	public static void main(String[] args){
		//Debug.init();
		for(int i = 0; i <= 100; i++){
			try {
				PinCommand c = new PinCommand(i);
			} catch (BadPacketSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
