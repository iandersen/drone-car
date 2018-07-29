package com.github.dddpaul.netcat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Callable;

import static com.github.dddpaul.netcat.NetCat.BUFFER_LIMIT;

public class StreamTransferer implements Callable<Long> {

    private ReadableByteChannel input;
    private WritableByteChannel output;

    public StreamTransferer(ReadableByteChannel input, WritableByteChannel output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public Long call() {
        long total = 0;
        ByteBuffer buf = ByteBuffer.allocateDirect(BUFFER_LIMIT);
        try {
            while (input.read(buf) != -1) {
                buf.flip();
                total += output.write(buf);
                if (buf.hasRemaining()) {
                    buf.compact();
                } else {
                    buf.clear();
                }
            }
            if (output instanceof SocketChannel) {
                ((SocketChannel) output).shutdownOutput();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }
}