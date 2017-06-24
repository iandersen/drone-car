package com.htmlhigh5.vehicle;

import org.apache.commons.configuration2.Configuration;

import com.htmlhigh5.debug.ConfigErrorException;
import com.htmlhigh5.debug.Debug;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GPIOComponent {
    private GPIOType type;
    private String name;
    private int pin;
    private int value;
    private Configuration config;
    private int maxValue = -1;
    private int minValue = -1;

    public GPIOComponent(Configuration c) {
        this.type = gpioTypeFromString(c.getString("TYPE"));
        pin = c.getInt("PIN");
        name = c.getString("NAME");
        maxValue = c.containsKey("MAX_PW") ? c.getInt("MAX_PW") : -1;
        minValue = c.containsKey("MIN_PW") ? c.getInt("MIN_PW") : -1;
        config = c;
    }
    
    public int getPin(){
        return this.pin;
    }

    private GPIOType gpioTypeFromString(String s) {
        switch (s.toUpperCase()) {
            case "CAR_ESC":
                return GPIOType.CAR_ESC;
            case "PLANE_ESC":
                return GPIOType.PLANE_ESC;
            case "SERVO":
                return GPIOType.SERVO;
            case "TOGGLE":
                return GPIOType.TOGGLE;
            case "MOTOR":
                return GPIOType.MOTOR;
            default:
                try {
                    throw new ConfigErrorException("Unknown GPIOComponent type from config: " + s);
                } catch (ConfigErrorException e) {
                    Debug.printStackTrace(e);
                }
                return GPIOType.MOTOR;
        }
    }

    public void setType(GPIOType type) {
        this.type = type;
    }

    public GPIOType getType() {
        return this.type;
    }

    // This is what the keyboard presses and releases will have to trigger
    public void setValue(int value) throws BadGPIOValueException {
        if (value < 0 || value > 202)
            throw new BadGPIOValueException(value);
        this.value = value;
    }

    public void turnOn() throws BadGPIOValueException {
        this.setValue(202);
    }

    public void turnOff() throws BadGPIOValueException {
        this.setValue(201);
    }

    public int getValue() {
        return this.value;
    }

    public void toggle() {
        this.value = this.value == 201 ? 202 : 201;
    }

    public void modify(int val) {
        try {
            int newValue = this.getValue() + val;
            newValue = newValue > this.minValue ? newValue : this.minValue;
            newValue = newValue < this.maxValue ? newValue : this.maxValue;
            setValue(newValue);
        } catch (BadGPIOValueException e) {
            Debug.printStackTrace(e);
        }
    }

    public void noKeyEvent() {
        switch (this.type) {
            case CAR_ESC:
                try {
                    this.setValue(100);
                } catch (BadGPIOValueException e) {
                    Debug.printStackTrace(e);
                }
            break;
            case PLANE_ESC:
                try {
                    this.setValue(100);
                } catch (BadGPIOValueException e) {
                    Debug.printStackTrace(e);
                }
            break;
            case SERVO:
            break;
            case TOGGLE:
            break;
            case MOTOR:
            break;
        }
    }

    public void handleKeyDown(String key) {
        switch (this.type) {
            case CAR_ESC:
                if (key.equalsIgnoreCase(config.getString("ACCELERATE_KEY")))
                    this.modify(config.getInt("ACCELERATION_SPEED"));
                else if (key.equalsIgnoreCase(config.getString("DECCELERATE_KEY")))
                    this.modify(-config.getInt("ACCELERATION_SPEED"));
                else
                    try {
                        this.setValue(100);
                    } catch (BadGPIOValueException e) {
                        Debug.printStackTrace(e);
                    }
            break;
            case PLANE_ESC:
                if (key.equalsIgnoreCase(config.getString("ACCELERATE_KEY")))
                    this.modify(config.getInt("ACCELERATION_SPEED"));
                else if (key.equalsIgnoreCase(config.getString("DECCELERATE_KEY")))
                    this.modify(-config.getInt("ACCELERATION_SPEED"));
                else
                    try {
                        this.setValue(100);
                    } catch (BadGPIOValueException e) {
                        Debug.printStackTrace(e);
                    }
            break;
            case SERVO:
                if (key.equalsIgnoreCase(config.getString("CLOCKWISE_KEY")))
                    this.modify(config.getInt("ROTATION_SPEED"));
                else if (key.equalsIgnoreCase(config.getString("COUNTERCLOCKWISE_KEY")))
                    this.modify(-config.getInt("ROTATION_SPEED"));
            break;
            case TOGGLE:
            break;
            case MOTOR:
            break;
        }
    }

    public void handleKeyPressed(String key) {
        switch (this.type) {
            case CAR_ESC:
            break;
            case PLANE_ESC:
            break;
            case SERVO:
            break;
            case TOGGLE:
                if (key.equalsIgnoreCase(config.getString("TOGGLE_KEY"))){
                    this.toggle();
                }
            break;
            case MOTOR:
            break;
        }
    }
}
