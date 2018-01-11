package Eizikiu_Server;

import java.util.*;

import Eizikiu_Tools.*;

public class Eizikiu_Server {

	private static LinkedList<User> userList;
	private static LinkedList<ConnectionToClient> connectionList;
	private static LinkedList<Room> publicRooms;
	private static LinkedList<Room> privateRooms;
	
	public static void main(String[] args) {
		
		// Variablen der main()
		Scanner keyboardIn = new Scanner(System.in);
		
		// Logging in Datei anschalten
		EZKlogger.setFileOutput(true);
		
		// globale Listen instanzieren
		userList = new LinkedList<>();
		connectionList = new LinkedList<>();
		publicRooms = new LinkedList<>();
		privateRooms = new LinkedList<>();
		
		// NetListener erstellen und als Thread (Daemon) starten
		NetListener netListener;
		netListener = new NetListener();
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
	
	public static LinkedList<User> getUserList() {
		return userList;
	}
	
	public static LinkedList<ConnectionToClient> getConnectionList() {
		return connectionList;
	}
	
	public static LinkedList<Room> getPublicRooms() {
		return publicRooms;
	}
	
	public static LinkedList<Room> getPrivateRoooms() {
		return privateRooms;
	}
}
