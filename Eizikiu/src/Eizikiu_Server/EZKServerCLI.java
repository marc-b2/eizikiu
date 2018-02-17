package Eizikiu_Server;

import java.util.Scanner;

public class EZKServerCLI implements Runnable {

	private Scanner scanner;
	
	// CmdLineParser
	
	// constructor
	public EZKServerCLI() {
		// TODO
	}
	
	// functions
	@Override
	public void run() {
		
		do {
			
			
			String command = "input";
			
			switch (command) {

			case "":
		
				// show all users
				// show private rooms
				// show public rooms
				// show connection list
				
				// logger show
				// logger set log level
				// logger set console output
				// logger set gui output
				// logger set file output
				// logger set file name
				
				// user warn
				// user kick
				// user ban
				// user unban
				// user show
				
				// room create
				// room edit
				// room delete
				// room show
				
				// start listener
				// stop listener
				
				// shutdown
				
				break;

			default:
				break;
			}
			
			
		} while (true);
		
	}
	
	// getter
	// setter

}
