package Eizikiu_Tools;

import java.io.*;
import java.util.Date;

public class EZKlogger {
	
	private static int loglevel = 2;
	private static String logfile = "eizikiu.log";
	
	private static boolean consoleOutput = true;
	private static boolean fileOutput = false;
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	private static PrintWriter fileOut = null;
	private static Date date = new Date();
	
	/*/ Konstruktor
	public EZKlogger(){
		consoleOutput = true;
		fileOutput = false;
		fw = null;
		bw = null;
		fileOut = null;		
	}
	*/
	// Setter
	public static void setLoglevel(int newLoglevel){
		loglevel = newLoglevel;
	}
	
	public static void setLogfile(String newLogfile) {
		logfile = newLogfile;
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
	
	// Logging-Methoden
	public static void info(String message) {
		if(loglevel >=0) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	public static void log(String message) {
		if(loglevel >=1) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	public static void debug(String message) {
		if(loglevel >=2) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	// stream handling
	private static void initFileOutput() throws IOException{
		if(fileOut == null){
			fw = new FileWriter(logfile, true); // 'true' bewirkt, dass neuer String in Datei angehangen wird
		    bw = new BufferedWriter(fw);
		    fileOut = new PrintWriter(bw);
		}
	}
	
	private static void closeFileOutput() throws IOException {
		if(!(fw == null)) {
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
