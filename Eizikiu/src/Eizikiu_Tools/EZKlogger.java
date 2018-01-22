package Eizikiu_Tools;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import Eizikiu_GUI.Eizikiu_Server_GUI;

public class EZKlogger {
	
	protected static int loglevel = 2;
	protected static String logfile = "eizikiu.log";
	
	protected static boolean consoleOutput = true;
	protected static boolean fileOutput = false;
	protected static FileWriter fw = null;
	protected static BufferedWriter bw = null;
	protected static PrintWriter fileOut = null;
	protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss.SSS");
	protected static Eizikiu_Server_GUI gui = null;
	
	// setter
	public static void setLoglevel(int newLoglevel){
		loglevel = newLoglevel;
		info("log level set to " + loglevel);
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
		info("log file set to " + logfile);
	}
	
	public static void setConsoleOutput(boolean newConsoleOutput) {
		consoleOutput = newConsoleOutput;
	}
	
	public static void setFileOutput(boolean newFileOutput) {
		try {
			if (!fileOutput && newFileOutput) {
				initFileOutput();
				info("logging to file enabled");
			}
			if (fileOutput && !newFileOutput){
				info("logging to file disabled");
				closeFileOutput();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		fileOutput = newFileOutput;	
	}
	
	public static void setGui(Eizikiu_Server_GUI gui) {
		EZKlogger.gui = gui;
	}
	
	// logging methods
	public static void info(String message) { // output to user
		if(loglevel >=0) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("INFO " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " INFO " + trace + " *** " + message); fileOut.flush();}
			if(gui != null) {gui.writeLogger("INFO " + trace + " *** " + message +"\n");}
		}
	}
	
	public static void log(String message) {
		if(loglevel >=1) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("LOG " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " LOG " + trace + " *** " + message); fileOut.flush();}
			if(gui != null) {gui.writeLogger("LOG " + trace + " *** " + message +"\n");}
		}
	}
	
	public static void debug(String message) {
		if(loglevel >=2) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("DEBUG " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " DEBUG " + trace + " *** " + message); fileOut.flush();}
			if(gui != null) {gui.writeLogger("DEBUG " + trace + " *** " + message +"\n");}
		}
	}
	
	public static void debug() {
		if(loglevel >=3) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
						   new Throwable().getStackTrace()[1].getMethodName() + "():" +
						   new Throwable().getStackTrace()[1].getLineNumber();
			if(consoleOutput) {System.out.println("DEBUG " + trace);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " DEBUG: " + trace); fileOut.flush();}
			if(gui != null) {gui.writeLogger("DEBUG " + trace +"\n");}
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
