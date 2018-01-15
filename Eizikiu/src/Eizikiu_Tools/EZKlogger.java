package Eizikiu_Tools;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EZKlogger {
	
	private static int loglevel = 2;
	private static String logfile = "eizikiu.log";
	
	private static boolean consoleOutput = true;
	private static boolean fileOutput = false;
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	private static PrintWriter fileOut = null;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss.SSS");
	
	// setter
	public static void setLoglevel(int newLoglevel){
		loglevel = newLoglevel;
	}
	
	public static void setLogfile(String newLogfile) {
		logfile = newLogfile;
		if(fw != null) {
			try {
				closeFileOutput();
				initFileOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void setConsoleOutput(boolean newConsoleOutput) {
		consoleOutput = newConsoleOutput;
	}
	
	public static void setFileOutput(boolean newFileOutput) {
		try {
			if (!fileOutput && newFileOutput) {
				initFileOutput();
			}
			if (fileOutput && !newFileOutput){
				closeFileOutput();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		fileOutput = newFileOutput;
	}
	
	// logging methods
	public static void info(String message) { // output to user
		if(loglevel >=0) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("INFO " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " INFO " + trace + " *** " + message); fileOut.flush();}
		}
	}
	
	public static void log(String message) {
		if(loglevel >=1) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("LOG " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " LOG " + trace + " *** " + message); fileOut.flush();}
		}
	}
	
	public static void debug(String message) {
		if(loglevel >=2) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("DEBUG " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " DEBUG " + trace + " *** " + message); fileOut.flush();}
		}
	}
	
	public static void debug() {
		if(loglevel >=2) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
						   new Throwable().getStackTrace()[1].getMethodName() + "():" +
						   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("DEBUG " + trace);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " DEBUG: " + trace); fileOut.flush();}
		}
	}
	// stream handling
	private static void initFileOutput() throws IOException{
		if(fileOut == null){
			fw = new FileWriter(logfile, true); // 'true' for new String gets appended
		    bw = new BufferedWriter(fw);
		    fileOut = new PrintWriter(bw);
		}
	}
	
	private static void closeFileOutput() throws IOException {
		if(fw != null) {
			fileOut.flush();
			fileOut.close();
			fileOut = null;
			bw.close();
			bw = null;
			fw.close();
			fw = null;
		}
	}
}
