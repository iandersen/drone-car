package com.htmlhigh5.userControl;
import com.htmlhigh5.vehicle.*;
import java.util.ArrayList;

// error if src not included
    public class UserControl {
	static boolean off = true;
	static Vehicle v;
	static GPIOComponent[] devices;
	static int[] understand;
	public static void main(String[] args) {
		v = new Vehicle();
		init();
	}
	
	public static void changeServo(char m) {
		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < devices.length; i++)
        {
        	if (understand[i] == 1)
        	{
        		list.add(i);
        	}
        }
        for (Integer number : list) {
        	if (devices[number].getValue() < 90)
        		switch(m) 
        		{
        			case 'i': devices[number].setValue(devices[number].getValue() + 10);
        		}
        }

		/*
		 * cycle through devices and setValue to the incremented value 
		 */
	}
	
	public static void changeMotor() {
		
	}

    public static void changeESC() {
    	
    }
	
    public static void init() {
    	devices = v.getDevices();
    	final int numberOfDevices = devices.length;
    	understand = new int[numberOfDevices];
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
				    	// if (keyisPressed)
				    	/* {
				    	 *     switch(keyisPressed.toUpperCase()) 
				    	 *     {
				    	 *         case W: // change x, y, z
				    	 *         changeServo();
				    	 *         changeESC();
				    	 *         changeMotor();
				    	 *         case A: //// change x, y, z
				    	 *         case S: // change x, y, z
				    	 *         case D: // change x, y, z
				    	 *         case N:
				    	 *     }
				    	 *     updateGUI()
				    	 * }
				    	 */
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
    