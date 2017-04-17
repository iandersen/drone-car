package com.htmlhigh5.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;

public class Transmitter {
	private int port;
	private String targetIP;
	private DatagramSocket clientSocket;
	private boolean intialized = false;
	
	public Transmitter(){
		this.intialized = true;
		try {
			this.clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			Debug.printStackTrace(e);
		}
		this.port = Main.config.getInt("CONTROL_PORT");
		this.targetIP = Main.config.getString("TARGET_IP");
	}

	public int sendControlPacket(ControlPacket packet) throws IOException {
		if(!this.intialized)
			try {
				throw new Exception("Transmitter was not initialized!");
			} catch (Exception e) {
				Debug.printStackTrace(e);
			}
		InetAddress IPAddress = targetIP.equalsIgnoreCase("localhost")
		        || targetIP.equals("127.0.0.1") ? InetAddress.getLocalHost()
		                : InetAddress.getByName(targetIP);
		byte[] data = packet.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		clientSocket.send(sendPacket);
		return 0;
	}
	
	public void close(){
		clientSocket.close();
	}
}
