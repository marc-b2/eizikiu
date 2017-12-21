package Eizikiu_Server;

import java.io.IOException;
//import java.io.*;
//import java.net.*;
import java.util.*;

import Eizikiu_Tools.*;

public class Eizikiu_Server {

	public static void main(String[] args) {
		
		// Variablen der main()
		Scanner keyboardIn = new Scanner(System.in);
		EZKlogger output = new EZKlogger();
		
		try {
			output.setFileOutput(true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// globale Listen anlegen
		LinkedList<User> userList = new LinkedList<>();
		LinkedList<ConnectionToClient> connectionList = new LinkedList<>();
		
		// NetListener erstellen und als Thread(Daemon) starten
		NetListener netListener;
		netListener = new NetListener(connectionList, userList);
		Thread NLThread = new Thread(netListener);
		NLThread.setDaemon(true);
		NLThread.start();
		output.info("Eizikiu_Server.main() -> NetListener started...");
		
		output.info("Eizikiu_Server.main() -> Press Return to stop Server!");
		keyboardIn.nextLine();
		output.info("Eizikiu_Server.main() -> You pressed Return");
		
		// connections schlieﬂen
		for(ConnectionToClient x : connectionList){
			x.shutdown();
		}
		
		// input/output schlieﬂen
		keyboardIn.close();
		try {
			output.closeFileOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
