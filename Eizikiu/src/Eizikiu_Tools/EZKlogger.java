package Eizikiu_Tools;

import java.io.*;
import java.util.Date;

public class EZKlogger {
	
	private static int loglevel = 2;
	private static String logfile = "eizikiu.log";
	
	private boolean consoleOutput;
	private boolean fileOutput;
	private FileWriter fw;
	private BufferedWriter bw;
	private PrintWriter fileOut;
	private Date date;
	
	// Konstruktor
	public EZKlogger(){
		consoleOutput = true;
		fileOutput = false;
		fw = null;
		bw = null;
		fileOut = null;		
	}
	
	// Setter
	public static void setLoglevel(int newLoglevel){
		loglevel = newLoglevel;
	}
	
	public static void setLogfile(String newLogfile) {
		logfile = newLogfile;
	}
	
	public void setConsoleOutput(boolean consoleOutput) {
		this.consoleOutput = consoleOutput;
	}
	
	public void setFileOutput(boolean fileOutput) {
		try {
			if (!this.fileOutput && fileOutput) {
				initFileOutput();
			}
			if (this.fileOutput && !fileOutput){
				closeFileOutput();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		this.fileOutput = fileOutput;
	}
	
	// Logging-Methoden
	public void info(String message) {
		
		if(loglevel >=0) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	public void log(String message) {
			
		if(loglevel >=1) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	public void debug(String message) {
		
		if(loglevel >=2) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	// stream handling
	private void initFileOutput() throws IOException{
		if(fw==null){
			date = new Date();
			fw = new FileWriter(logfile, true); // 'true' bewirkt, dass neuer String in datei angehangen wird
		    bw = new BufferedWriter(fw);
		    fileOut = new PrintWriter(bw);
		}
	}
	
	private void closeFileOutput() throws IOException {
		if(!(fw == null)) {
			fileOut.flush();
			fileOut.close();
			fileOut = null;
			bw.close();
			bw = null;
			fw.close();
			fw = null;
			date = null;
		}
	}
}
