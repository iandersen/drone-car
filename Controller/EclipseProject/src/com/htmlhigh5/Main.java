package com.htmlhigh5;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.network.BadPacketSizeException;
import com.htmlhigh5.network.PinCommand;

public class Main {
	public static Configuration config;
	
	public static void main(String[] args){
		init();
		for(int i = 0; i <= 1; i++){
			try {
				PinCommand c = new PinCommand(105);
			} catch (BadPacketSizeException e) {
				Debug.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
	
	private static void init(){
		Configurations configs = new Configurations();
		try{
		    config = configs.properties(new File("controller.properties"));
		}
		catch (ConfigurationException cex){
		    cex.printStackTrace();
		}
		Debug.init();
	}
}
