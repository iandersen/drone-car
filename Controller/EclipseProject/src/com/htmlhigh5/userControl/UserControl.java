package com.htmlhigh5.userControl;

import com.htmlhigh5.Main;
import com.htmlhigh5.vehicle.*;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;


public class UserControl {
	
	public synchronized void keysPressed(ArrayList<String> codes){
		for(String code : codes)
			for(GPIOComponent c : Main.vehicle.getDevices())
				c.handleKeyEvent(code);
	}
}
