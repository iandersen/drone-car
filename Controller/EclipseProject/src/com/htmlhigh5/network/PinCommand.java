package com.htmlhigh5.network;

public class PinCommand {
	public static final int SIZE = 8; // 8 bit packets (value 0-255)
	private boolean[] content = new boolean[SIZE];
	private int value;

	public PinCommand(int value) throws BadPacketSizeException {
		this.value = value;
		if (value < 0 || value > 100)
			throw new BadPacketSizeException(value);
		else {
			for (int i = SIZE - 1; i >= 0; i--)
				content[i] = (value & (1 << i)) != 0;
		}
	}

	public boolean[] getContent() {
		return this.content;
	}

	public byte[] getBytes() {
		byte[] ret = new byte[(int) Math.ceil(SIZE / 8)];
		for (int i = 0; i < ret.length; i++)
			for (int n = 0; n < 8 && i * 8 + n < SIZE; n++)
				ret[i] |= content[i * 8 + n] ? 1 << n : 0;
		return ret;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "{PinCommand value=" + value + "}";
	}
}
