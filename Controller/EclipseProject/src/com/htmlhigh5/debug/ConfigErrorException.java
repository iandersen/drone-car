package com.htmlhigh5.debug;

public class ConfigErrorException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 3284832586923536422L;
    
    public ConfigErrorException(Object message) {
        super("Config Error! " + message);
    }
}
