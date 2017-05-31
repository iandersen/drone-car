package com.htmlhigh5.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;

public class Receiver {
	private int port;
	private DatagramSocket serverSocket;
	private boolean stopped = true;

	public Receiver() {
		this.port = Main.config.getInt("LISTEN_PORT");
		try {
			this.serverSocket = new DatagramSocket(this.port);
			Selector selector = Selector.open();
			ServerSocketChannel ssChannel = ServerSocketChannel.open();
			ssChannel.configureBlocking(false);
			ssChannel.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
		    ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (SocketException e) {
			Debug.printStackTrace(e);
		} catch (IOException e) {
			Debug.printStackTrace(e);
		}
	}

	private void listen() throws Exception {
		byte[] receiveData = new byte[1024];
		Debug.debug("Receiver listening on port " + this.port);
		int msSinceReceived = 0;
		while (!stopped) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			System.out.println("Receiving");
			String message = new String(receivePacket.getData());
			this.parsePacket(message);
			receiveData = new byte[1024];
			//InetAddress IPAddress = receivePacket.getAddress();
			//int port = receivePacket.getPort();
			//System.out.println("Received from " + IPAddress + ":" + port);
			Thread.sleep(50);
		}
	}
	
	private void parsePacket(String message){
		HashMap<String, String> keyPairs = new HashMap<String, String>();
		String[] objects = message.split("\\|\\|\\|");
		for(String obj : objects){
			if(obj.length() > 0){
				String[] pair = obj.split(":::");
				if(pair.length == 2)
					keyPairs.put(pair[0], pair[1]);
				else
					Debug.error("Bad object received from vehicle! Message Received: " + message + " Rejected object: " + obj);
			}
		}
		
		if(keyPairs.get("access") != null){
			this.handleAccess(keyPairs.get("access"));
		}
		
		if(keyPairs.get("0") != null){
			Debug.debug("Connection is alive");
		}
	}
	
	private void handleAccess(String response){
		if(response.equals("success")){
			Debug.debug("Connection established successfully!");
			Main.connectionEstablished = true;
		} else if (response.equals("failure")){
			Main.connectionEstablished = false;
			Debug.error("Connection Rejected! Either a bad password or the vehicle already has an active connection!");
		} else {
			Debug.error("Bad access value from receiver! Value: " + response);
		}
	}
	
	public void stop(){
		Debug.debug("Receiver Stopped!");
		this.stopped = true;
	}
	
	public void start(){
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
	}

	public int getPort() {
		return this.port;
	}
}
