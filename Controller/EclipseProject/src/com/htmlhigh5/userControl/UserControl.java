package com.htmlhigh5.userControl;

import com.htmlhigh5.Main;
import com.htmlhigh5.vehicle.*;

import java.util.ArrayList;


public class UserControl {
    
    public synchronized void keysDown(ArrayList<String> codes){
        for(GPIOComponent c : Main.vehicle.getDevices())
            c.handleKeyDown(codes);
    }
    
    public void keyPressed(String code){
        for(GPIOComponent c : Main.vehicle.getDevices())
            c.handleKeyPressed(code);
    }
}
