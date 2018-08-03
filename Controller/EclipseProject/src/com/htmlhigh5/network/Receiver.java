package com.htmlhigh5.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Scanner;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Receiver {
	private int port;
	private DatagramSocket serverSocket;
	private boolean stopped = true;
	private boolean ping = false;
	public IntegerProperty latencyProperty = new SimpleIntegerProperty();
	public boolean hasConnected = false;
	public boolean isConnected = false;
	public int connectionTime = 0;

	public Receiver() {
		this.port = Main.config.getInt("LISTEN_PORT");
		this.serverSocket = Main.transmitter.customSocket;
	}
	
	public int getLatency(){
		return latencyProperty.get();
	}

	private void listen() throws Exception {
		byte[] receiveData = new byte[1024];
		Debug.debug("Receiver listening on port " + this.port);
		while (!stopped) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String message = new String(receivePacket.getData());
			this.parsePacket(message);
			receiveData = new byte[1024];
			connectionTime++;
			// InetAddress IPAddress = receivePacket.getAddress();
			// int port = receivePacket.getPort();
			// System.out.println("Received from " + IPAddress + ":" + port);
		}
	}

	private void parsePacket(String message) {
		HashMap<String, String> keyPairs = new HashMap<String, String>();
		String[] objects = message.split("\\|\\|\\|");
		for (String obj : objects) {
			if (obj.length() > 0) {
				String[] pair = obj.split(":::");
				if (pair.length == 2)
					keyPairs.put(pair[0], pair[1]);
				else
					Debug.error("Bad object received from vehicle! Message Received: " + message + " Rejected object: "
							+ obj);
			}
		}

		if (keyPairs.get("ping") != null) {
			this.setLatency((int)(System.currentTimeMillis() - Main.vehicle.pingSendTime));
		}

		if (keyPairs.get("access") != null) {
			this.handleAccess(keyPairs.get("access"));
		}

		if (keyPairs.get("0") != null) {
			this.ping = true;
		}

		if (keyPairs.get("debug") != null) {
			Debug.debug(keyPairs.get("debug"));
		}

		if (keyPairs.get("warn") != null) {
			Debug.warn(keyPairs.get("warn"));
		}

		if (keyPairs.get("error") != null) {
			Debug.error(keyPairs.get("error"));
		}

		if (keyPairs.get("position") != null) {
			Main.vehicle.setLatLon(keyPairs.get("position"));
		}

		if (keyPairs.get("speed") != null) {
			Scanner s = new Scanner(keyPairs.get("speed"));
			if (s.hasNextDouble())
				Main.vehicle.setSpeedMPS(s.nextDouble());
			s.close();
		}

		if (keyPairs.get("alt") != null) {
			Scanner s = new Scanner(keyPairs.get("alt"));
			if (s.hasNextDouble())
				Main.vehicle.setAltitude(s.nextDouble());
			s.close();
		}
	}

	private void handleAccess(String response) {
		if (response.equals("success")) {
			Debug.debug("Connection established successfully!");
			Main.connectionEstablished = true;
			Main.onConnect();
			Main.startStream();
		} else if (response.equals("failure")) {
			Main.connectionEstablished = false;
			Debug.error("Connection Rejected! Either a bad password or the vehicle already has an active connection!");
		} else {
			Debug.error("Bad access value from receiver! Value: " + response);
		}
	}

	public void stop() {
		Debug.debug("Receiver Stopped!");
		this.stopped = true;
	}

	public void start() {
		this.stopped = false;
		Receiver self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					self.listen();
				} catch (Exception e) {
					Debug.printStackTrace(e);
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					self.checkActivity();
				} catch (Exception e) {
					Debug.printStackTrace(e);
				}
			}
		}).start();
	}

	private void checkActivity() {
		int secondsSinceConnection = 0;
		while (!stopped) {
			try {
				Thread.sleep(1000);
				secondsSinceConnection++;
				if (this.ping) {
					secondsSinceConnection = 0;
					this.ping = false;
					if (!this.hasConnected)
						this.hasConnected = true;
				}
				if (secondsSinceConnection < 5)
					this.isConnected = true;
				else
					this.isConnected = false;
				if (this.hasConnected) {
					if (secondsSinceConnection == 25)
						Debug.error("Connection Lost");
					else if (secondsSinceConnection % 5 == 0 && secondsSinceConnection > 0
							&& secondsSinceConnection < 25)
						Debug.warn("No connection in " + secondsSinceConnection + " seconds");
				}
			} catch (InterruptedException e) {
				Debug.printStackTrace(e);
			}
		}
	}

	public int getPort() {
		return this.port;
	}
	
	public void setLatency(int latency){
		this.latencyProperty.set(latency);
	}
}
