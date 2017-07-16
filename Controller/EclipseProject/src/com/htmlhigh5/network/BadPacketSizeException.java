package com.htmlhigh5.network;

public class BadPacketSizeException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3647331101036956510L;

    public BadPacketSizeException(Object message) {
        super("Bad packet size! Size must be in range 0-100. Value Received: " + message);
    }

}
