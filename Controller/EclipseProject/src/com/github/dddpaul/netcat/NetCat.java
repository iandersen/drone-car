//Credit to https://github.com/dddpaul/java-netcat

package com.github.dddpaul.netcat;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

public class NetCat {

    // Ready to handle full-size UDP datagram or TCP segment in one step
    public static int BUFFER_LIMIT = 2 << 16 - 1;

    private ExecutorCompletionService<Long> executor;

    private Options opt;

    public NetCat(Options opt) {
        this.opt = opt;
        executor = new ExecutorCompletionService<>(Executors.newFixedThreadPool(2));
    }

    public static class Options {
        @Option(name = "-l", usage = "Listen mode, default false")
        public boolean listen = false;

        @Option(name = "-u", usage = "UDP instead TCP, default false")
        public boolean udp = false;

        @Option(name = "-p", usage = "Port number, default 9999")
        public int port = 9999;

        @Argument(usage = "Host, default 127.0.0.1", metaVar = "host")
        public String host = "127.0.0.1";

        public InputStream input = System.in;
        public OutputStream output = System.out;

        public Options() {
        }

        public Options(boolean listen, boolean udp, String host, int port, InputStream input, OutputStream output) {
            this.listen = listen;
            this.udp = udp;
            this.port = port;
            this.host = host;
            this.input = input;
            this.output = output;
        }
    }

    public static void main(String[] args) throws Exception {
        Options opt = new Options();
        CmdLineParser parser = new CmdLineParser(opt);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }

        Future<Long> future = new NetCat(opt).start();

        // Wait till one of the sides is terminated
        long bytes = future.get();
        System.err.println("Bytes sent or received = " + bytes);
        System.exit(0);
    }

    public Future<Long> start() throws Exception {
        return opt.listen ? listen() : connect();
    }

    private Future<Long> listen() throws Exception {
        System.err.println(String.format("Listening at %s:%d", opt.udp ? "UDP" : "TCP", opt.port));
        return opt.udp ? listenUdp() : listenTcp();
    }

    private Future<Long> connect() throws Exception {
        System.err.println(String.format("Connecting to [%s:%d]", opt.host, opt.port));
        return opt.udp ? connectUdp() : connectTcp();
    }

    /**
     * @return Receiver future
     */
    private Future<Long> listenUdp() throws Exception {
        DatagramChannel channel = DatagramChannel.open();
        channel.socket().bind(new InetSocketAddress(opt.port));
        channel.configureBlocking(true);

        BlockingQueue<SocketAddress> queue = new ArrayBlockingQueue<>(1);
        Future<Long> future = executor.submit(new DatagramReceiver(channel, opt.output, queue));

        // Start sender after remote address will be determined
        SocketAddress remoteAddress = queue.take();
        executor.submit(new DatagramSender(opt.input, channel, remoteAddress));

        return future;
    }

    /**
     * @return Receiver future
     */
    private Future<Long> connectUdp() throws Exception {
        DatagramChannel channel = DatagramChannel.open();
        SocketAddress remoteAddress = new InetSocketAddress(opt.host, opt.port);
        channel.connect(remoteAddress);

        executor.submit(new DatagramSender(opt.input, channel, remoteAddress));
        return executor.submit(new DatagramReceiver(channel, opt.output, null));
    }

    /**
     * @return Receiver future
     */
    private Future<Long> listenTcp() throws Exception {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(opt.port));
        serverChannel.configureBlocking(true);
        SocketChannel channel = serverChannel.accept();
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
        System.err.println(String.format("Accepted from [%s:%d]", remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort()));
        return transferStreams(channel);
    }

    /**
     * @return Receiver future
     */
    private Future<Long> connectTcp() throws Exception {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(opt.host, opt.port));
        return transferStreams(channel);
    }

    /**
     * @return Receiver future
     */
    private Future<Long> transferStreams(final SocketChannel socketChannel) throws IOException, ExecutionException, InterruptedException {
        // Shutdown socket when this program is terminated
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    socketChannel.shutdownOutput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        executor.submit(new StreamTransferer(Channels.newChannel(opt.input), socketChannel));
        executor.submit(new StreamTransferer(socketChannel, Channels.newChannel(opt.output)));
        executor.take().get();  // Wait for sender
        return executor.take(); // And return receiver future
    }
}