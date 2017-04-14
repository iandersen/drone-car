package com.htmlhigh5.debug;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Debug {
	private static boolean ERROR = true;
	private static boolean WARN = true;
	private static boolean DEBUG = true;
	private static boolean LOG = true;
	
	private static String debugFolder = "D:\\RC\\logs\\";
	private static String debugFile;
	private static FileWriter fw;
	
	public static void init(){
		debugFile = "This should be the date";
		try {
			fw = new FileWriter(debugFolder + debugFile + ".txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(){
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String error(String text){
		if(!ERROR || text == null || text.isEmpty())
			return null;
		String ret = "ERROR!: " + text;
		System.out.println(ret);
		return ret;
	}
	
	public static String warn(String text){
		if(!WARN || text == null || text.isEmpty())
			return null;
		String ret = "WARNING!: " + text;
		System.out.println(ret);
		return ret;
	}
	
	public static String debug(String text){
		if(!DEBUG || text == null || text.isEmpty())
			return null;
		System.out.println(text);
		return text;
	}
	
	public static String log(String text){
		if(!LOG || text == null || text.isEmpty())
			return null;
		try {
			String ret = "\n[";
			ret.concat(new Date().toString());
			ret.concat("]: ");
			fw.append(ret);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
