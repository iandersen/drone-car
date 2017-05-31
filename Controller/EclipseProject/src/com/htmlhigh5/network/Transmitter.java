package com.htmlhigh5.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;

public class Transmitter {
	private int gpioPort, customPort;
	private String targetIP;
	private DatagramSocket gpioSocket;
	private DatagramSocket customSocket;
	private boolean intialized = false;
	
	public Transmitter(){
		try {
			this.gpioSocket = new DatagramSocket();
			this.customSocket = new DatagramSocket();
			this.intialized = true;
		} catch (SocketException e) {
			Debug.printStackTrace(e);
		}
		this.gpioPort = Main.config.getInt("CONTROL_PORT");
		this.customPort = Main.config.getInt("CONTROL_PORT2");
		this.targetIP = Main.config.getString("TARGET_IP");
	}

	public int sendControlPacket(ControlPacket packet){
		try {
			this.sendGenericPacket(packet.getBytes(), this.gpioPort, this.gpioSocket);
			return 0;
		} catch (IOException e) {
			Debug.printStackTrace(e);
			return -1;
		}
	}
	
	public int sendCustomPacket(CustomPacket packet){
		try {
			this.sendGenericPacket(packet.getBytes(), this.customPort, this.customSocket);
			return 0;
		} catch (IOException e) {
			Debug.printStackTrace(e);
			return -1;
		}
	}
	
	private int sendGenericPacket(byte[] data, int port, DatagramSocket socket) throws IOException{
		if(!this.intialized)
			try {
				throw new Exception("Transmitter was not initialized!");
			} catch (Exception e) {
				Debug.printStackTrace(e);
			}
		InetAddress IPAddress = this.targetIP.equalsIgnoreCase("localhost") || this.targetIP.equals("127.0.0.1") ? InetAddress.getLocalHost() : InetAddress.getByName(this.targetIP);
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		socket.send(sendPacket);
		return 0;
	}
	
	public void close(){
		gpioSocket.close();
	}
}
