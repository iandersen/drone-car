package com.htmlhigh5.vehicle;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.ConfigErrorException;
import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.network.ControlPacket;
import com.htmlhigh5.network.CustomPacket;

public class Vehicle {
	public int numDevices;
	private ArrayList<GPIOComponent> devices = new ArrayList<GPIOComponent>();
	private int[] pinNumbers;
	private boolean running = false;
	private ControlPacket packet;
	private int packetsPerSecond;
	public long pingSendTime = 0;

	public void start() {
		this.packetsPerSecond = Main.config.getInt("PACKETS_PER_SECOND");
		int startPin = 100;
		for(GPIOComponent c : devices)
			startPin = (int)Math.min(startPin, c.getPin());
		this.packet = new ControlPacket(startPin);
		if (this.running)
			return;
		this.running = true;
		Debug.debug("Vehicle intialized!");
		Vehicle self = this;
		Main.receiver.start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				int i = 0;
				while (self.running) {
					try {
						self.updatePacket();
						Thread.sleep((int) (1000 * 1 / self.packetsPerSecond));
						if (Main.receiver.isConnected) {
							// Check the latency every three seconds
							if (i >= self.packetsPerSecond * 3) {
								i = 0;
								Main.transmitter.sendCustomPacket(new CustomPacket("ping"));
								self.pingSendTime = System.currentTimeMillis();
							}
							self.sendPacket();
						} else {
							Debug.debug("Attempting to connect...");
							self.sendInitPacket();
							Thread.sleep(4000);
						}
						i++;
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
			packet.setPin(this.devices.get(i).getPin(), this.devices.get(i).getValue());
	}

	private void sendPacket() {
		packet.send();
	}

	public ArrayList<GPIOComponent> getDevices() {
		return devices;
	}

	public boolean isRunning() {
		return running;
	}

	public void sendInitPacket() {
		String password = Main.config.getString("PASSWORD");
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			String toSend = DatatypeConverter.printHexBinary(hash).toLowerCase();
			CustomPacket initPacket = new CustomPacket(toSend);
			initPacket.send();
		} catch (NoSuchAlgorithmException e) {
			Debug.printStackTrace(e);
		}
	}

	public void addDevice(Configuration c) {
		devices.add(new GPIOComponent(c));
		this.numDevices ++;
	}
}
