package Eizikiu_Tools;

import java.io.*;
import java.util.Date;

public class EZKlogger {
	
	private int loglevel;
	private boolean consoleOutput;
	private boolean fileOutput;
	private String logfile;
	private FileWriter fw;
	private BufferedWriter bw;
	private PrintWriter fileOut;
	private Date date;
	
	// Konstruktor
	public EZKlogger(){
		loglevel = 1;
		consoleOutput = true;
		fileOutput = false;
		logfile = null;
		fw = null;
		bw = null;
		fileOut = null;		
	}
	
	// Setter
	public void setLoglevel(int loglevel){
		this.loglevel = loglevel;
	}
	
	public void setConsoleOutput(boolean consoleOutput) {
		this.consoleOutput = consoleOutput;
	}
	
	public void setFileOutput(boolean fileOutput) throws IOException {
		if (!this.fileOutput) {
			date = new Date();
			logfile = "eizikiu.log";
			fw = new FileWriter(logfile, true); // 'true' bewirkt, dass neuer String in datei angehangen wird
		    bw = new BufferedWriter(fw);
		    fileOut = new PrintWriter(bw);
		}else {
			closeFileOutput();
		}
		this.fileOutput = fileOutput;
	}
	
	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}
	
	// Logging-Methoden
	public void info(String message) {
		
		if(loglevel >=0) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	public void debug(String message) {
		
		if(loglevel >=1) {
			if(consoleOutput) {System.out.println(message);}
			if(fileOutput) {fileOut.println(date.toString() + ": " + message);}
		}
	}
	
	public void closeFileOutput() throws IOException {
		if(!(fw == null)) {
			fileOut.flush();
			fileOut.close();
			//bw.flush();
			bw.close();
			//fw.flush();
			fw.close();
		}
	}
}
