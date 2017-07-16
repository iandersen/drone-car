package com.htmlhigh5;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.gui.GUIMain;
import com.htmlhigh5.network.CustomPacket;
import com.htmlhigh5.network.Receiver;
import com.htmlhigh5.network.Transmitter;
import com.htmlhigh5.userControl.UserControl;
import com.htmlhigh5.vehicle.BadGPIOValueException;
import com.htmlhigh5.vehicle.Vehicle;

public class Main {
    public static Configuration config;
    public static Configuration vehicleConfig;
    public static Transmitter transmitter;
    public static Receiver receiver;
    public static Vehicle vehicle = new Vehicle();
    public static UserControl userControl;
    public static boolean connectionEstablished = false;

    public static void main(String[] args) {
        init(); // loading up config files
        userControl = new UserControl();
        vehicle.start();
        GUIMain.startGUI();
    }

    public static void onConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Debug.debug("Connected");
                } catch (Exception e) {
                    Debug.printStackTrace(e);
                }
            }
        });//.start();
    }

    private static void init() {
        Configurations configs = new Configurations();
        try {
            config = configs.properties(new File("controller.properties"));
            File configDir = new File("Devices");
            for(File c : configDir.listFiles()){
                vehicle.addDevice(configs.properties(c));
            }
        } catch (ConfigurationException cex) {
            Debug.printStackTrace(cex);
        }
        Debug.init();
        transmitter = new Transmitter();
        receiver = new Receiver();
    }

    public static void startStream() {
        Debug.debug("Starting Stream...");
        transmitter.sendCustomPacket(new CustomPacket("stream_start"));
    }

    public static void takeScreenshot() {
        Debug.debug("Taking Screenshot...");
        transmitter.sendCustomPacket(new CustomPacket("take_screenshot"));
    }

    public static void shutDown() {
        transmitter.close();
        vehicle.stop();
    }
}
