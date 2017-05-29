package com.htmlhigh5.network;

public class Receiver {
	private int port;
	private String listenIP;
	
	public Receiver(String listenIP, int port){
		this.listenIP = listenIP;
		this.port = port;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public String getIP(){
		return this.listenIP;
	}
	
	
}

