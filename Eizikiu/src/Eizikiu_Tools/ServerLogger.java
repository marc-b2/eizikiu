package Eizikiu_Tools;

import java.time.ZonedDateTime;

public class ServerLogger extends EZKlogger {
	
	// logging methods
	public static void info(String message) { // output to user
		if(loglevel >=0) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			// output to gui here
			if(consoleOutput) {System.out.println("INFO " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " INFO " + trace + " *** " + message); fileOut.flush();}
		}
	}
	
	public static void log(String message) {
		if(loglevel >=1) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			// output to gui here
			if(consoleOutput) {System.out.println("LOG " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " LOG " + trace + " *** " + message); fileOut.flush();}
		}
	}
	
	public static void debug(String message) {
		if(loglevel >=2) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
					   new Throwable().getStackTrace()[1].getMethodName() + "():" +
					   new Throwable().getStackTrace()[1].getLineNumber();
			// output to gui here
			if(consoleOutput) {System.out.println("DEBUG " + trace + " *** " + message);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " DEBUG " + trace + " *** " + message); fileOut.flush();}
		}
	}
	
	public static void debug() {
		if(loglevel >=3) {
			String trace = new Throwable().getStackTrace()[1].getClassName() + "." +
						   new Throwable().getStackTrace()[1].getMethodName() + "():" +
						   new Throwable().getStackTrace()[1].getLineNumber();
			// output to gui here
			if(consoleOutput) {System.out.println("DEBUG " + trace);}
			if(fileOutput) {fileOut.println(ZonedDateTime.now().format(formatter) + " DEBUG: " + trace); fileOut.flush();}
		}
	}
}
