package com.htmlhigh5;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.gui.GUIMain;
import com.htmlhigh5.network.CustomPacket;
import com.htmlhigh5.network.Receiver;
import com.htmlhigh5.network.Transmitter;
import com.htmlhigh5.vehicle.BadGPIOValueException;
import com.htmlhigh5.vehicle.Vehicle;

public class Main {
	public static Configuration config;
	public static Configuration vehicleConfig;
	public static Transmitter transmitter;
	public static Receiver receiver;
	private static Vehicle vehicle;
	public static boolean connectionEstablished = false;

	public static void main(String[] args) {
		init(); // loading up config files
		vehicle = new Vehicle();
		vehicle.start();
		GUIMain.startGUI();
		motorTest();
		lightTest();
		//servoTest();
	}
	
	private static void motorTest(){
		try {
			vehicle.getDevices()[1].setValue(50);
			Thread.sleep(3000);
			vehicle.getDevices()[1].setValue(60);
			Thread.sleep(3000);
			vehicle.getDevices()[1].setValue(70);
			Thread.sleep(3000);
			vehicle.getDevices()[1].setValue(50);
		} catch (BadGPIOValueException e) {
			// TODO Auto-generated catch block
			Debug.printStackTrace(e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Debug.printStackTrace(e);
		}
	}
	
	private static void servoTest(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						vehicle.getDevices()[0].setValue((int)Math.floor(Math.random()*100));
						Thread.sleep(300);
					}
				} catch (Exception e) {
					Debug.printStackTrace(e);
				}
			}
		}).start();
	}
	
	private static void lightTest(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						vehicle.getDevices()[2].toggle();
						Thread.sleep(500);
					}
				} catch (Exception e) {
					Debug.printStackTrace(e);
				}
			}
		}).start();
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
		receiver = new Receiver();
	}
	
	public static void startStream(){
		Debug.debug("Starting Stream...");
		transmitter.sendCustomPacket(new CustomPacket("stream_start"));
	}
	
	public static void takeScreenshot(){
		Debug.debug("Taking Screenshot...");
		transmitter.sendCustomPacket(new CustomPacket("take_screenshot"));
	}
	
	public static void shutDown(){
		transmitter.close();
		vehicle.stop();
	}
}
