package com.htmlhigh5.network;

import com.htmlhigh5.Main;
import com.htmlhigh5.debug.Debug;

public class ControlPacket {
	public static final int MAX_PINS = Main.vehicleConfig.getInt("GPIO_PINS");

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
			Debug.printStackTrace(e);
		}
	}

	public int getPinValue(int pin) {
		PinCommand pc = pinCommands[pin];
		return pc != null ? pc.getValue() : null;
	}

	public boolean[] getPinData(PinCommand pc) {
		if (pc == null) {
			int value = PinCommand.MIN_VALUE;
			boolean[] ret = new boolean[PinCommand.SIZE];
			for (int i = PinCommand.SIZE - 1; i >= 0; i--)
				ret[i] = (value & (1 << i)) != 0;
			return ret;
		}
		return pc.getContent();
	}

	public byte[] getPinBytes(PinCommand pc) {
		if (pc == null){
			int value = PinCommand.MIN_VALUE;
			int size = PinCommand.SIZE;
			boolean[] bools = new boolean[size];
			for (int i = size - 1; i >= 0; i--)
				bools[i] = (value & (1 << i)) != 0;
			byte[] ret = new byte[(int) Math.ceil(size / 8)];
			for (int i = 0; i < ret.length; i++)
				for (int n = 0; n < 8 && i * 8 + n < size; n++)
					ret[i] |= bools[i * 8 + n] ? 1 << n : 0;
			return ret;
		}
		return pc.getBytes();
	}

	private void finalizeData() {
		finalized = true;
		for (int pin = 0; pin < MAX_PINS - 1; pin++) {
			PinCommand pc = pinCommands[pin];
			if (pc != null) {
				boolean[] pinData = getPinData(pc);
				for (int i = 0; i < PinCommand.SIZE; i++)
					data[(pin + 1) * 8 - i] = pinData[PinCommand.SIZE - i - 1];
			}
		}
	}

	public void send() {
		if (!finalized)
			finalizeData();
		Main.transmitter.sendControlPacket(this);
	}

	public boolean[] getData() {
		return data;
	}

	public byte[] getBytes() {
		int bytesPerPin = (int) Math.ceil(PinCommand.SIZE / 8);
		byte[] ret = new byte[MAX_PINS * bytesPerPin];
		for (int i = 0; i < MAX_PINS; i++) {
			byte[] pinBytes = this.getPinBytes(pinCommands[i]);
			for (int n = 0; n < bytesPerPin; n++)
				ret[i * bytesPerPin + n] = pinBytes[n];
		}
		return ret;
	}

	public void clear() {
		for (int i = 0; i < this.data.length; i++)
			this.data[i] = false;
		for (int i = 0; i < MAX_PINS; i++)
			this.pinCommands[i] = null;
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
