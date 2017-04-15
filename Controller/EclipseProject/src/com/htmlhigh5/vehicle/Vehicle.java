package com.htmlhigh5.vehicle;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.network.ControlPacket;

public class Vehicle {
	private int numDevices;
	private GPIOType deviceTypes[];
	private GPIOComponent[] devices;
	private int[] pinNumbers;
	private boolean running = false;
	private ControlPacket packet = new ControlPacket();

	public Vehicle() {
		this.numDevices = Main.vehicleConfig.getInt("NUM_ATTACHED_DEVICES");
		String[] deviceTypeNames = Main.vehicleConfig.getString("DEVICE_TYPES").replaceAll(" ", "").split(",");
		String[] pinNumberStrings = Main.vehicleConfig.getString("DEVICE_PINS").replaceAll(" ", "").split(",");
		this.pinNumbers = new int[this.numDevices];
		this.deviceTypes = new GPIOType[this.numDevices];
		this.devices = new GPIOComponent[this.numDevices];
		for (int i = 0; i < this.numDevices; i++) {
			this.pinNumbers[i] = Integer.parseInt(pinNumberStrings[i]);
			switch (deviceTypeNames[i].toUpperCase()) {
				case "ESC":
					deviceTypes[i] = GPIOType.ESC;
				break;
				case "SERVO":
					deviceTypes[i] = GPIOType.SERVO;
				break;
				case "TOGGLE":
					deviceTypes[i] = GPIOType.TOGGLE;
				break;
				case "MOTOR":
					deviceTypes[i] = GPIOType.MOTOR;
				break;
			}
			devices[i] = new GPIOComponent(deviceTypes[i]);
		}
	}

	public void init() {
		if (this.running)
			return;
		this.running = true;
		int packetsPerSecond = Main.config.getInt("PACKETS_PER_SECOND");
		Debug.debug("Vehicle intialized!");
		Vehicle self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (self.running) {
					try {
						self.updatePacket();
						Thread.sleep((int) (1000 * 1 / packetsPerSecond));
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
		Debug.debug("Vehicle shut down");
	}

	private void updatePacket() {
		packet.clear();
		for (int i = 0; i < this.numDevices; i++){
			this.devices[i].setValue((int)(Math.random() * 100));
			packet.setPin(this.pinNumbers[i], this.devices[i].getValue());
		}
	}

	private void sendPacket() {
		packet.send();
	}
}
