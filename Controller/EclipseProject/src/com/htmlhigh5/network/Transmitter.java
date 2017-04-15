package com.htmlhigh5.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.htmlhigh5.Main;

public class Transmitter {
	public static int sendControlPacket(ControlPacket packet) throws IOException {
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getLocalHost();
		byte[] data = packet.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress,
		        Main.config.getInt("CONTROL_PORT"));
		clientSocket.send(sendPacket);
		clientSocket.close();
		return 0;
	}
}
