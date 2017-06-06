package com.htmlhigh5.debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.htmlhigh5.Main;

public class Debug {
	private static boolean ERROR = true;
	private static boolean WARN = true;
	private static boolean DEBUG = true;

	private static String debugFolder;
	private static FileWriter fw;

	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public static void init() {
		String debugLevel = Main.config.getString("DEBUG_LEVEL");
		switch (debugLevel) {
		case "SILENT":
			ERROR = false;
			WARN = false;
			DEBUG = false;
			break;
		case "ERROR":
			WARN = false;
			DEBUG = false;
			break;
		case "WARN":
			DEBUG = false;
			break;
		}
		debugFolder = Main.config.getString("DEBUG_FOLDER");
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
		String dateString = format.format(date);
		try {
			fw = new FileWriter(new File(debugFolder + dateString + ".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Debug.debug("Saving log files to " + debugFolder);
	}

	public static void close() {
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String error(String text) {
		if (!ERROR || text == null || text.isEmpty())
			return null;
		String ret = "ERROR!: " + text;
		System.out.println(ret);
		log(ret);
		return ret;
	}

	public static String warn(String text) {
		if (!WARN || text == null || text.isEmpty())
			return null;
		String ret = "WARNING!: " + text;
		System.out.println(ret);
		log(ret);
		return ret;
	}

	public static String debug(String text) {
		if (!DEBUG || text == null || text.isEmpty())
			return null;
		System.out.println(text);
		log(text);
		return text;
	}
	
	public static String printStackTrace(Exception e){
		String ret = ExceptionUtils.getStackTrace(e);
		Debug.error(ret);
		return ret;
	}

	public static String log(String text) {
		if (text == null || text.isEmpty())
			return null;
		try {
			String ret = "[";
			ret += timeFormat.format(new Date());
			ret += "]: ";
			ret += text;
			ret += System.getProperty("line.separator");
			fw.append(ret);
			fw.flush();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
