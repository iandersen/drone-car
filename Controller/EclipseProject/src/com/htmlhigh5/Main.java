package com.htmlhigh5;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.gui.GUIMain;
import com.htmlhigh5.network.Transmitter;
import com.htmlhigh5.vehicle.Vehicle;

public class Main{
	public static Configuration config;
	public static Configuration vehicleConfig;
	public static Transmitter transmitter;
	private static Vehicle vehicle;

	public static void main(String[] args) {
		init();
		GUIMain.startGui();
		vehicle = new Vehicle();
		vehicle.start();
	}

	private static void init() {
		Configurations configs = new Configurations();
		try {
			config = configs.properties(new File("controller.properties"));
			vehicleConfig = configs.properties(new File("vehicle.properties"));
		} catch (ConfigurationException cex) {
			Debug.printStackTrace(cex);
		}
		Debug.init();
		transmitter = new Transmitter();
	}
	
	public static void shutDown(){
		transmitter.close();
		vehicle.stop();
	}
}
