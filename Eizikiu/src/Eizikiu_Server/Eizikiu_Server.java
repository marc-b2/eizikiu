package Eizikiu_Server;

import Eizikiu_GUI.*;
import Eizikiu_Tools.*;

import java.util.*;
import java.io.*;
import java.awt.EventQueue;

public class Eizikiu_Server {

	private static LinkedList<User> globalUserList;
	private static LinkedList<ConnectionToClient> connectionList;
	private static LinkedList<Room> publicRooms;
	private static LinkedList<Room> privateRooms;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		// switch on logging to file
		EZKlogger.setLoglevel(2);
		EZKlogger.setLogfile("eizikiu_server.log");
		EZKlogger.setFileOutput(true);
		EZKlogger.debug();
		EZKlogger.log("**************** server started ****************");
		
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
				// set transient objects != null and add IDs to IDList
				x.setUserList(new LinkedList<User>());
				Eizikiu_Tools.Room.getIDList().add(x.getID());
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
		
		// start GUI
		Eizikiu_Server_GUI gui = new Eizikiu_Server_GUI();
		EventQueue.invokeLater(gui);
		
		
		EZKlogger.info("Eizikiu_Server.main() -> Press Return to stop Server!");
		keyboardIn.nextLine();
		EZKlogger.info("Eizikiu_Server.main() -> You pressed Return");
		
		// close connections
		for(ConnectionToClient x : connectionList){
			try {
				x.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// save 'globalUserList' and 'publicRooms' to file
		try {
			// users
			FileOutputStream saveToFile = new FileOutputStream("Eizikiu.users");
			ObjectOutputStream saveUsers = new ObjectOutputStream(saveToFile);
			saveUsers.writeObject(globalUserList);
			saveUsers.close();
			
			EZKlogger.log("writing 'globalUserList' to file successful!");
			
			// rooms
			saveToFile = new FileOutputStream("Eizikiu.rooms");
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
		sendRoomListToAllClients();
	}
	
	public static void editRoom(Room room, String newName) {
		EZKlogger.debug();
		room.setName(newName);
		sendRoomListToAllClients();
	}
	
	public static void deleteRoom(Room room) {
		EZKlogger.debug();
		if(room.getID() != 1) publicRooms.remove(room); // not allowed to delete default room
		sendRoomListToAllClients();
	}
	
	public static void sendRoomListToAllClients() {
		String list = makeListToString();
		for(ConnectionToClient x : connectionList) {
			try {
				x.getNetOutput().sendMessage(new Message(list, "Server", 27, 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * @return String of 'publicRooms' to send to clients (for message type 27)
	 */
	public static String makeListToString(){
		String roomList = "";
		for(Room x : publicRooms) {
			if(publicRooms.indexOf(x) == publicRooms.size()-1) { // last element
				roomList = roomList + x.getName() + "§" + x.getID();
			} else {
				roomList = roomList + x.getName() + "§" + x.getID() + "§"; 								
			}
		}
		return roomList;
	}
	
	/**
	 * @param userList
	 * @return String of passed user list to send to clients (for message type 28)
	 */
	public static String makeListToString(LinkedList<User> userList) {
		String userString = "";
		for(User x : userList) {
			if(userList.indexOf(x) == userList.size()-1) { // last element
				userString = userString + x.getName();
			} else {
				userString = userString + x.getName() + "§";
			}
		}
		return userString;
	}
	/**
	 * sends warn message to user
	 */
	public static void warnUser(User user, String message) {
		try {
			user.getConnection().getNetOutput().sendMessage(new Message(message, "Server", 29, 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
