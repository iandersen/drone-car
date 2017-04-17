package com.htmlhigh5.vehicle;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.ConfigErrorException;
import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.network.ControlPacket;

public class Vehicle {
	private int numDevices;
	private GPIOComponent[] devices;
	private int[] pinNumbers;
	private boolean running = false;
	private ControlPacket packet = new ControlPacket();
	private int packetsPerSecond;

	public Vehicle() {
		this.numDevices = Main.vehicleConfig.getInt("NUM_ATTACHED_DEVICES");
		this.packetsPerSecond = Main.config.getInt("PACKETS_PER_SECOND");
		String[] deviceTypeNames = Main.vehicleConfig.getString("DEVICE_TYPES").replaceAll(" ", "")
		        .split(",");
		String[] pinNumberStrings = Main.vehicleConfig.getString("DEVICE_PINS").replaceAll(" ", "")
		        .split(",");
		this.pinNumbers = new int[this.numDevices];
		this.devices = new GPIOComponent[this.numDevices];
		if (pinNumberStrings.length != deviceTypeNames.length
		        || deviceTypeNames.length != this.numDevices)
			try {
				throw new ConfigErrorException(
				        "Their is an inconsistency in the number of GPIO devices and pins in vehicle.properties! Number of devices: "
				                + this.numDevices + " Devices Listed: " + deviceTypeNames.length
				                + " Pin Numbers Listed: " + pinNumberStrings.length);
			} catch (ConfigErrorException e) {
				Debug.printStackTrace(e);
			}
		for (int i = 0; i < this.numDevices; i++) {
			this.pinNumbers[i] = Integer.parseInt(pinNumberStrings[i]);
			GPIOType type;
			switch (deviceTypeNames[i].toUpperCase()) {
				case "ESC":
					type = GPIOType.ESC;
				break;
				case "SERVO":
					type = GPIOType.SERVO;
				break;
				case "TOGGLE":
					type = GPIOType.TOGGLE;
				break;
				case "MOTOR":
					type = GPIOType.MOTOR;
				break;
				default:
					type = GPIOType.MOTOR;
					try {
						throw new ConfigErrorException(
						        "Unknown GPIOComponent type from vehicle.properties: "
						                + deviceTypeNames[i]);
					} catch (ConfigErrorException e) {
						Debug.printStackTrace(e);
					}
				break;
			}
			devices[i] = new GPIOComponent(type);
		}
	}

	public void start() {
		if (this.running)
			return;
		this.running = true;
		Debug.debug("Vehicle intialized!");
		Vehicle self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (self.running) {
					try {
						self.updatePacket();
						Thread.sleep((int) (1000 * 1 / self.packetsPerSecond));
						self.sendPacket();
					} catch (InterruptedException e) {
						Debug.printStackTrace(e);
					}
				}
			}
		}).start();
	}

	public void stop() {
		this.running = false;
		Debug.debug("Vehicle shut down!");
	}

	private void updatePacket() {
		packet.clear();
		for (int i = 0; i < this.numDevices; i++)
			packet.setPin(this.pinNumbers[i], this.devices[i].getValue());
	}

	private void sendPacket() {
		packet.send();
	}

	public GPIOComponent[] getDevices() {
		return devices;
	}

	public boolean isRunning() {
		return running;
	}
}
