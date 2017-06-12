package com.htmlhigh5.userControl;
import com.htmlhigh5.vehicle.*;
// error if src not included
public class UserControl {
	static Vehicle v;
	static GPIOComponent[] devices;
	public static void main(String[] args) {
		v = new Vehicle();
		init();
	}
    public static void init() {
    	devices = v.getDevices();
    	final int numberOfDevices = devices.length;
    	int[] understand = new int[numberOfDevices];
    	GPIOComponent s;
    	
    	for(int i = 0; i < devices.length; i++) {
    		s = devices[i];
    		switch(s.getType()) {
    		case ESC: understand[i] = 0; break;
    		case SERVO: understand[i] = 1; break;
    		case MOTOR: understand[i] = 2; break;
    		case TOGGLE: understand[i] = 3; break;
    		}
    	}
    	// 0 is ESC
    	// 1 is Servo
    	// 2 is Motor
    	// 3 is Toggle
    	v.start();
    	new Thread(new Runnable() {
			public void run() {
				while (v.isRunning()) {
				    try {
				    	Thread.sleep(33);
				    	// Check if key K is pressed
				    	// check what key K is
				    	// ArrayList<Integer> list = typesChangedBy(K);
				    	/*
				    	 * 
				    	 */
				    	/*
				    	for(int i = 0; i < numberOfDevices; i++) {
				    		if (list.contains(understand[i])) {
				    		    if yes setValue accordingly
				    		}
				    	}
				    	*/
				    	
				    }
				    catch (Exception e) {}
				}
			}
		}).start();
    			
    	
    }
}
    