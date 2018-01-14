package Eizikiu_Tools;

import java.net.*;
import java.util.Scanner;

public class KeyboardListener implements Runnable {

	Socket socket;
	User user;
	OutputStreamSet netOutput;
	String text;
	
	// constructor
	public KeyboardListener(Socket socket, OutputStreamSet netOutput, User user){
		EZKlogger.debug();
		this.socket = socket;
		this.user = user;
		this.netOutput = netOutput;
	}
	
	// functions
	@Override
	public void run() {
		EZKlogger.debug();
		Scanner keyboardIn = new Scanner(System.in);
		
		try {
			while(true){
				text = keyboardIn.nextLine();
				
				if(!text.equals("")){
					netOutput.sendMessage(new Message(text, user.getName(), 0));
//					netOutput.sendAvailableByte();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		keyboardIn.close();
	}
}
