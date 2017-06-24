package com.htmlhigh5.userControl;

import com.htmlhigh5.Main;
import com.htmlhigh5.vehicle.*;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;


public class UserControl {
    
    public synchronized void keysDown(ArrayList<String> codes){
        for(String code : codes)
            for(GPIOComponent c : Main.vehicle.getDevices())
                c.handleKeyDown(code);
        if(codes.size() == 0)
            for(GPIOComponent c : Main.vehicle.getDevices())
                c.noKeyEvent();
    }
    
    public void keyPressed(String code){
        for(GPIOComponent c : Main.vehicle.getDevices())
            c.handleKeyPressed(code);
    }
}
