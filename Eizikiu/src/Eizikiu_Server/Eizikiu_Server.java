package Eizikiu_Server;

import java.util.*;

import Eizikiu_Tools.*;

public class Eizikiu_Server {

	public static void main(String[] args) {
		
		// Variablen der main()
		Scanner keyboardIn = new Scanner(System.in);
		
		// Logging in Datei anschalten
		EZKlogger.setFileOutput(true);
		
		// globale Listen anlegen
		LinkedList<User> userList = new LinkedList<>();
		LinkedList<ConnectionToClient> connectionList = new LinkedList<>();
		LinkedList<Room> publicRooms = new LinkedList<>();
		LinkedList<Room> privateRooms = new LinkedList<>();
		
		// NetListener erstellen und als Thread(Daemon) starten
		NetListener netListener;
		netListener = new NetListener(connectionList, userList);
		Thread NLThread = new Thread(netListener);
		NLThread.setDaemon(true);
		EZKlogger.log("Eizikiu_Server.main() -> NetListener started...");
		NLThread.start();
				
		EZKlogger.info("Eizikiu_Server.main() -> Press Return to stop Server!");
		keyboardIn.nextLine();
		EZKlogger.info("Eizikiu_Server.main() -> You pressed Return");
		
		// connections schliessen
		for(ConnectionToClient x : connectionList){
			x.shutdown();
		}
		
		// io schliessen
		keyboardIn.close();
		EZKlogger.setFileOutput(false);
	}

}
