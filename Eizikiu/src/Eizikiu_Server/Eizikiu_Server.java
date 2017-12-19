package Eizikiu_Server;

//import java.io.*;
//import java.net.*;
import java.util.*;

import Eizikiu_Tools.*;

public class Eizikiu_Server {

	public static void main(String[] args) {
		
		LinkedList<User> userList = new LinkedList<>();
		LinkedList<ConnectionToClient> connectionList = new LinkedList<>();
		NetListener netListener;
		Scanner keyboardIn = new Scanner(System.in);
		
		netListener = new NetListener(connectionList, userList);
		Thread NLThread = new Thread(netListener);
		NLThread.setDaemon(true);
		NLThread.start();
		System.out.println("Eizikiu_Server.main() -> NetListener started...");
		
		System.out.println("Eizikiu_Server.main() -> Press Return to stop Server!");
		keyboardIn.nextLine();
		System.out.println("Eizikiu_Server.main() -> You pressed Return");
		
		for(ConnectionToClient x : connectionList){
			x.shutdown();
		}
		
		keyboardIn.close();
	}

}
