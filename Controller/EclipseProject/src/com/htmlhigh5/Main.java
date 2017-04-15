package com.htmlhigh5;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.vehicle.Vehicle;

public class Main {
	public static Configuration config;
	public static Configuration vehicleConfig;

	public static void main(String[] args) {
		init();
		Vehicle car = new Vehicle();
		car.init();
	}

	private static void init() {
		Configurations configs = new Configurations();
		try {
			config = configs.properties(new File("controller.properties"));
			vehicleConfig = configs.properties(new File("vehicle.properties"));
		} catch (ConfigurationException cex) {
			cex.printStackTrace();
		}
		Debug.init();
	}
}
