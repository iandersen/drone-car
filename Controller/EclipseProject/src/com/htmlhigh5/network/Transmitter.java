package com.htmlhigh5.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Transmitter {
	public static void main(String[] args){
		try {
			udpSendReceive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int sendPacket() throws IOException{
		byte[] buffer = {10,23,12,31,43,32,24};
        byte [] IP={-64,-88,1,106};
        InetAddress address = InetAddress.getByAddress(IP);
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, address, 9999
                );
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.send(packet);
        System.out.println(InetAddress.getByName("172.91.203.163").getHostAddress());
        return 0;
	}
	
	public static void udpSendReceive() throws IOException{
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getLocalHost();
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      String sentence = "TEST";
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
      clientSocket.send(sendPacket);
      
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      String modifiedSentence = new String(receivePacket.getData());
      System.out.println("FROM SERVER:" + modifiedSentence);
      clientSocket.close();
	}
}
