package com.htmlhigh5;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.network.ControlPacket;

public class Main {
	public static Configuration config;
	
	public static void main(String[] args){
		init();
		ControlPacket packet = new ControlPacket();
		for(int i = 0; i < config.getInt("GPIO_PINS"); i++)
			packet.setPin(i, 100);
		packet.send();
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
