package Eizikiu_Server;

import java.util.*;
import java.io.*;

import Eizikiu_Tools.*;

public class Eizikiu_Server {

	private static LinkedList<User> globalUserList;
	private static LinkedList<ConnectionToClient> connectionList;
	private static LinkedList<Room> publicRooms;
	private static LinkedList<Room> privateRooms;
	
	public static void main(String[] args) {
		
		// switch on logging to file
		EZKlogger.setLoglevel(2);
		EZKlogger.setFileOutput(true);
		EZKlogger.debug();
		EZKlogger.info("**************** server started ****************");
		
		Scanner keyboardIn = new Scanner(System.in);
		
		// create global lists
		globalUserList = new LinkedList<>();
		connectionList = new LinkedList<>();
		publicRooms = new LinkedList<>();
		privateRooms = new LinkedList<>();
		
		// load 'globalUserList' and 'publicRooms' from file
		try {
			// users
			FileInputStream readFromFile = new FileInputStream("Eizikiu.users");
			ObjectInputStream loadUsers = new ObjectInputStream(readFromFile);
			globalUserList = (LinkedList<User>) loadUsers.readObject();
			loadUsers.close();
			
			EZKlogger.log("reading 'globalUserList' from file successful!");
			EZKlogger.debug("********************************************************");
			EZKlogger.debug("users loaded:");
			for(User x : globalUserList) {
				// set transient objects != null
				x.setConnection(new ConnectionToClient());
				x.setRooms(new LinkedList<>());
				x.setStatus(false);
				EZKlogger.debug(x.toString());
			}
			EZKlogger.debug("********************************************************");

			// rooms
			readFromFile = new FileInputStream("Eizikiu.rooms");
			ObjectInputStream loadRooms = new ObjectInputStream(readFromFile);
			publicRooms = (LinkedList<Room>) loadRooms.readObject();
			loadRooms.close();
			
			EZKlogger.log("reading 'publicRooms' from file successful!");
			EZKlogger.debug("********************************************************");
			EZKlogger.debug("rooms loaded:");
			for(Room x : publicRooms) {
				// set transient objects != null
				x.setUserList(new LinkedList<User>());
				EZKlogger.debug(x.toString());
			}
			EZKlogger.debug("********************************************************");
		} catch (Exception e){
			e.printStackTrace();
			
			// create default room
			publicRooms.add(new Room("default"));
			globalUserList.add(new User("admin", "admin"));
		}
		
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
		
		// save 'globalUserList' and 'publicRooms' to file
		try {
			// users
			FileOutputStream saveToFile = new FileOutputStream("Eiziku.users");
			ObjectOutputStream saveUsers = new ObjectOutputStream(saveToFile);
			saveUsers.writeObject(globalUserList);
			saveUsers.close();
			
			EZKlogger.log("writing 'globalUserList' to file successful!");
			
			// rooms
			saveToFile = new FileOutputStream("Eiziku.rooms");
			ObjectOutputStream saveRooms = new ObjectOutputStream(saveToFile);
			saveRooms.writeObject(publicRooms);
			saveRooms.close();
			
			EZKlogger.log("writing 'publicRooms' to file successful!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// close IO
		keyboardIn.close();
		EZKlogger.setFileOutput(false);
	}
	
	// getter
	public static LinkedList<User> getGlobalUserList() {
		EZKlogger.debug();
		return globalUserList;
	}
	
	public static LinkedList<ConnectionToClient> getConnectionList() {
		EZKlogger.debug();
		return connectionList;
	}
	
	public static LinkedList<Room> getPublicRooms() {
		EZKlogger.debug();
		return publicRooms;
	}
	
	public static LinkedList<Room> getPrivateRooms() {
		EZKlogger.debug();
		return privateRooms;
	}
	
	// functions
	public static void createRoom(String roomName) {
		EZKlogger.debug();
		publicRooms.add(new Room(roomName));
	}
	
	public static void editRoom(Room room, String newName) {
		EZKlogger.debug();
		room.setName(newName);
	}
	
	public static void deleteRoom(Room room) {
		EZKlogger.debug();
		publicRooms.remove(room);
	}
}
