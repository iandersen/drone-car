package com.htmlhigh5.network;

import com.htmlhigh5.Main;

public class CustomPacket {
    
    private String content;

    public CustomPacket(String content) {
        this.content = content;
    }

    public void append(Object val) {
        this.content = this.content + val;
    }

    public String getContent() {
        return this.content;
    }

    public void send() {
        Main.transmitter.sendCustomPacket(this);
    }

    public byte[] getBytes() {
        return this.content.getBytes();
    }

    public void clear() {
        this.content = "";
    }

    @Override
    public String toString() {
        return "{CustomPacket: Content: [" + this.content + "] }";
    }
}
