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
		this.socket = socket;
		this.user = user;
		this.netOutput = netOutput;
	}
	
	// functions
	@Override
	public void run() {

		Scanner keyboardIn = new Scanner(System.in);
		
		try {
			while(true){
				text = keyboardIn.nextLine();
				
				if(!text.equals("")){
					netOutput.sendMessage(new Message(text, user.getName()));
//					netOutput.sendAvailableByte();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		keyboardIn.close();
	}
}
