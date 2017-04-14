package com.htmlhigh5.network;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;

public class ControlPacket {
	public static final int MAX_PINS = Main.config.getInt("GPIO_PINS");

	private boolean[] data;
	private PinCommand[] pinCommands;
	private boolean finalized = false;

	public ControlPacket() {
		data = new boolean[PinCommand.SIZE * MAX_PINS];
		pinCommands = new PinCommand[MAX_PINS];
	}

	public void setPin(int pin, int value) {
		try {
			pinCommands[pin] = new PinCommand(value);
		} catch (BadPacketSizeException e) {
			Debug.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public int getPinValue(int pin) {
		PinCommand pc = pinCommands[pin];
		return pc != null ? pc.getValue() : null;
	}

	private void finalizeData() {
		finalized = true;
		for (int pin = 0; pin < MAX_PINS - 1; pin++) {
			PinCommand pc = pinCommands[pin];
			boolean[] pinData = pc.getContent();
			if (pc != null)
				for (int i = 0; i < PinCommand.SIZE; i++) {
					data[(pin + 1) * 8 - i] = pinData[PinCommand.SIZE - i - 1];
					System.out.print(data[(pin + 1) * 8 - i] + ", ");
				}
			System.out.println();
		}
	}

	public void send() {
		if (!finalized)
			finalizeData();
		try {
			Transmitter.sendControlPacket(this);
		} catch (IOException e) {
			Debug.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public boolean[] getData() {
		return data;
	}

	public byte[] getBytes() {
		int bytesPerPin = (int) Math.ceil(PinCommand.SIZE / 8);
		byte[] ret = new byte[MAX_PINS * bytesPerPin];
		for (int i = 0; i < MAX_PINS; i++) {
			byte[] pinBytes = pinCommands[i].getBytes();
			for (int n = 0; n < bytesPerPin; n++)
				ret[i * bytesPerPin + n] = pinBytes[n];
		}
		return ret;
	}

	@Override
	public String toString() {
		String ret = "{ControlPacket: ";
		for (PinCommand pc : pinCommands)
			ret += "[" + pc.toString() + "] ";
		ret += "} ";
		return ret;
	}
}
