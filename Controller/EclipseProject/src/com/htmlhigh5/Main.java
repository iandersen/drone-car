package com.htmlhigh5;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.boxes.Box;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.Transform;
import org.jcodec.scale.Yuv420pToRgb;

import com.htmlhigh5.debug.Debug;
import com.htmlhigh5.gui.GUIMain;
import com.htmlhigh5.network.CustomPacket;
import com.htmlhigh5.network.Receiver;
import com.htmlhigh5.network.Transmitter;
import com.htmlhigh5.userControl.UserControl;
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
		});// .start();
	}

	private static void init() {
		Configurations configs = new Configurations();
		try {
			config = configs.properties(new File("controller.properties"));
			File configDir = new File("Devices");
			for (File c : configDir.listFiles()) {
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//listenTcp();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		//transmitter.sendCustomPacket(new CustomPacket("stream_start"));
	}

	private static void listenTcp() throws Exception {
		System.out.println("Starting TCP listen");
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.socket().bind(new InetSocketAddress(Main.config.getInt("VIDEO_STREAM_PORT")));
		serverChannel.configureBlocking(true);
		SocketChannel channel = serverChannel.accept();
		InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
		System.out.println(String.format("Accepted from [%s:%d]", remoteAddress.getAddress().getHostAddress(),
				remoteAddress.getPort()));
		int BUFFER_SIZE = 2 << 16 - 1;
		ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);
		Transform transform = new Yuv420pToRgb();
		Picture rgb = Picture.create(300, 300, ColorSpace.RGB);
		BufferedImage bi = new BufferedImage(300, 300, BufferedImage.TYPE_3BYTE_BGR);
		H264Decoder decoder = new H264Decoder();
		AvcCBox avcCBox = new AvcCBox(null);
		FileOutputStream fos = new FileOutputStream("C:\\Users\\Ian\\Downloads\\chessboardjs-0.3.0\\js\\bytes.txt");
		while (channel.read(buff) != -1) {
            buff.flip();
            if (buff.hasRemaining()) {
            	buff.compact();
            	
            } else {
            	buff.clear();
            }
            System.out.println(new String(buff.array()));
            fos.write(buff.array());
            fos.write("\r\n".getBytes());
    		Picture out = Picture.create(300, 300, ColorSpace.RGB); // Allocate output frame of max size
//    		System.out.println(out.toString());
//    		Picture real = decoder.decodeFrame(buff, out.getData());
    		avcCBox.parse(buff);
    		List<ByteBuffer> frame = H264Utils.splitFrame(buff);
    		if(frame.size() > 0){
	    		Picture dec = decoder.decodeFrame(frame.get(0), out.getData());
	    		transform.transform(dec, rgb);
	    		if(rgb != null){
		    		Debug.debug(rgb.toString());
	//	    		transform.transform(real, rgb);
		    		AWTUtil.toBufferedImage(rgb, bi);
		    		Debug.debug("Saving picture");
		    		ImageIO.write(bi, "png", new File("C:\\Users\\Ian\\Downloads\\chessboardjs-0.3.0\\js\\picture" + Math.round(Math.random() * 100) +".png"));
		//    		JCodecUtil.savePictureAsPPM(real, new File("C:\\Users\\Ian\\Downloads\\chessboardjs-0.3.0\\js\\picture" + Math.round(Math.random() * 100) +".png"));
		//    		Thread.sleep(1000);
	    		}else{
	    			Debug.debug("rgb is null");
	    		}
    		}else{
    			Debug.debug("Frame is of size 0");
    		}
        }
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
