package Eizikiu_Server;

import java.util.*;

import Eizikiu_Tools.*;

public class Eizikiu_Server {

	private static LinkedList<User> globalUserList;
	private static LinkedList<ConnectionToClient> connectionList;
	private static LinkedList<Room> publicRooms;
	private static LinkedList<Room> privateRooms;
	
	public static void main(String[] args) {
		
		Scanner keyboardIn = new Scanner(System.in);
		
		// switch on logging to file
		EZKlogger.setFileOutput(true);
		
		// create global lists
		globalUserList = new LinkedList<>();
		connectionList = new LinkedList<>();
		publicRooms = new LinkedList<>();
		privateRooms = new LinkedList<>();
		
		// create default room
		publicRooms.add(new Room("default"));
		
		// create NetListener and start as thread (daemon)
		NetListener netListener;
		netListener = new NetListener();
		Thread NLThread = new Thread(netListener);
		NLThread.setDaemon(true);
		EZKlogger.log("Eizikiu_Server.main() -> NetListener started...");
		NLThread.start();
				
		EZKlogger.info("Eizikiu_Server.main() -> Press Return to stop Server!");
		keyboardIn.nextLine();
		EZKlogger.info("Eizikiu_Server.main() -> You pressed Return");
		
		// close connections
		for(ConnectionToClient x : connectionList){
			x.shutdown();
		}
		
		// close IO
		keyboardIn.close();
		EZKlogger.setFileOutput(false);
	}
	
	// getter
	public static LinkedList<User> getGlobalUserList() {
		return globalUserList;
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
