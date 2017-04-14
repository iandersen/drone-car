package com.htmlhigh5;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.htmlhigh5.debug.Debug;

public class Main {
	public static Configuration config;

	public static void main(String[] args) {
		init();
	}

	private static void init() {
		Configurations configs = new Configurations();
		try {
			config = configs.properties(new File("controller.properties"));
		} catch (ConfigurationException cex) {
			cex.printStackTrace();
		}
		Debug.init();
	}
}
