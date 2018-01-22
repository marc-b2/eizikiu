package Eizikiu_Client;

import java.awt.EventQueue;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;

import Eizikiu_Tools.*;
import Eizikiu_GUI.*;

public class Eizikiu_Client {

	private static LinkedList<User> globalUserList = null; // local copy from servers 'globalUserList', has to be updated when changes occur on server
	private static LinkedList<Room> publicRooms = null; // local copy from servers 'publicRooms', has to be updated when changes occur on server
	// rooms where this client is in will be hold in 'rooms' list owned by the Clients 'user' and will be updated on every join or leave event

	private static OutputStreamSet netOutput;
	private static InputStreamSet netInput;
	private static Socket socket;
	private static User user = null;
	private static String address = "localhost";
	public final static CountDownLatch latch = new CountDownLatch(1);
	
	public static void main(String args[]) {
		
		try {
			address = args[0];
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		EZKlogger.setLoglevel(3);
		EZKlogger.setLogfile("eizikiu_client.log");
		EZKlogger.setFileOutput(true);
		EZKlogger.debug();
		
		EZKlogger.log("************Eizikiu_Client.main() -> Eizikiu_Client started ************");
		EZKlogger.log("server address set to '" + address + "'");
		
		try {
			socket = new Socket(address, 1234);
			
			EZKlogger.log("server found");
			
			netInput = new InputStreamSet(socket);
			netOutput = new OutputStreamSet(socket);
			netInput.setupStreams();
			netOutput.setupStreams();
			
			EZKlogger.log("connection to server established\n\n");
			
			// start login GUI
			EZKlogger.debug("starting login GUI...");
			new LogInGUI(); // calls Eizikiu_Client.login() or ...register() until one of them returns true
			latch.await();
			EZKlogger.debug("returned from login GUI");
			
			// get globalUserList and pubicRooms
			do { // because user is already logged in and can receive messages from default room
				EZKlogger.debug("do-while-loop get user and room list");
				Message message = netInput.receiveMessage();
				int messageType = message.getType();
				if(messageType == 27) { // room list from server
					publicRooms = new LinkedList<>();
					String tempString = message.getMessage(); // message is "roomName1§roomID1§roomName2§roomID23....§roomNameX§roomIDX"
					String[] parts = tempString.split("§");
					for(int i=0; i<parts.length; i+=2) {
						publicRooms.add(new Room(parts[i], Integer.parseInt(parts[i+1])));
					}
					EZKlogger.debug("public room list received");
				}
				if(messageType == 28) { // user list from server
					globalUserList = new LinkedList<>(); // message is "userName1§userName2§...§userNameX"
					String tempString = message.getMessage();
					String[] parts = tempString.split("§");
					for(String x : parts) {
						globalUserList.add(new User(x, "noPW"));
					}
					EZKlogger.debug("global user list received");
				}
			} while(publicRooms == null || globalUserList == null);
			
			EZKlogger.log("received rooms:");
			for(Room x : publicRooms) EZKlogger.log(x.toString());
			EZKlogger.log("\n received users:");
			for(User x : globalUserList) EZKlogger.log(x.getName());
			
			// start GUI
			EZKlogger.debug("starting GUI...");
			Eizikiu_Client_GUI gui = new Eizikiu_Client_GUI();
			EventQueue.invokeLater(gui);
			gui.getFrmEizikiuClient().setTitle("Eizikiu  " + user.getName());
			// start chat
			EZKlogger.debug("starting chat...");
			chat(gui);
		} catch(Exception e) {
			EZKlogger.debug("ERROR: connection interrupted!");
			e.printStackTrace();
		}
	} 
	
	// getter
	public static LinkedList<User> getGlobalUserList() {
		EZKlogger.debug();
		return globalUserList;
	}
	
	public static LinkedList<Room> getPublicRooms() {
		EZKlogger.debug();
		return publicRooms;
	}
	
	public static User getUser() {
		EZKlogger.debug();
		return user;
	}
	
	// setter
	public static void setGlobalUserList(LinkedList<User> globalUserList) {
		EZKlogger.debug();
		Eizikiu_Client.globalUserList = globalUserList;
	}
	
	public static void setPublicRooms(LinkedList<Room> publicRooms) {
		EZKlogger.debug();
		Eizikiu_Client.publicRooms = publicRooms;
	}
	
	// functions
	public static boolean login(String name, String pw, LogInGUI gui) {
		EZKlogger.debug();
		try {
			// create user
			user = new User(name,pw);
			
			EZKlogger.debug("sending login request...");
			// send login request
			netOutput.sendMessage(new Message("login request", name, 11, 0));
			// send user credentials
			netOutput.sendMessage(new Message(pw, name, 12, 0));
			
			EZKlogger.debug("waiting for answer from server...");
			// wait for answer
			Message message;
			message = netInput.receiveMessage();
			
			// react on answer
			if(message.getType() == 8) {
				EZKlogger.debug("login successful");
				latch.countDown();
				return true;				
			} else if(message.getType() == 9) {
				EZKlogger.debug("REJECTED: " + message.getMessage());
				JOptionPane.showMessageDialog(gui.getBox(), message.getMessage(), "Error:", 0);
				return false;
			} else {
				EZKlogger.debug("ERROR: expected type 8 or 9, received message of type: " + message.getType());
				JOptionPane.showMessageDialog(gui.getBox(), "Sorry, unknown error!", "Error:", 0);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean register(String name, String pw, Registry_GUI gui) {
		EZKlogger.debug();
		try {
			// create user
			user = new User(name,pw);
			
			EZKlogger.debug("sending register request...");
			// send register request
			netOutput.sendMessage(new Message("register request", name, 10, 0));
			// send user credentials
			netOutput.sendMessage(new Message(pw, name, 12, 0));
			
			EZKlogger.debug("waiting for answer from server...");
			// wait for answer
			Message message;
			message = netInput.receiveMessage();
			
			// react on answer
			if(message.getType() == 8) {
				EZKlogger.debug("registering successful");
				latch.countDown();
				return true;				
			} else if(message.getType() == 9) {
				EZKlogger.debug("REJECTED: " + message.getMessage());
				JOptionPane.showMessageDialog(gui.getFrame(), message.getMessage(), "ERROR", 0);
				return false;
			} else {
				EZKlogger.debug("ERROR: expected type 8 or 9, received message of type: " + message.getType());
				JOptionPane.showMessageDialog(gui.getFrame(), "Sorry, unknown error!", "Error:", 0);
				return false;
			}
		} catch (Exception e) {
			EZKlogger.debug("ERROR: connection to server interrupted");
			e.printStackTrace();
			return false;
		}
	}
	
	public static void chat(Eizikiu_Client_GUI gui) {
		EZKlogger.debug();
		try {
			String tempString = "";
			String[] parts;
			Room room;
			LinkedList<Room> tempRoomList;
			LinkedList<User> tempUserList;
			LinkedList<User> targetUserList;
			boolean exit = false;
			while(!exit){
				Message message = netInput.receiveMessage();
				int messageType = message.getType();
				
				switch(messageType) {
				case 0:		// exit
					EZKlogger.debug("exit message received");
					exit = true;
					break;
				
				case 1:		// regular public message
				case 2:		// regular private message
					EZKlogger.debug(messageType == 1 ? "regular public message received" : "regular private message received");
					gui.writeMessage(message);
					break;
				
				case 20:	// service message -> dialog box INFO
					EZKlogger.debug("service message received");
					EZKlogger.log("service message from server: " + message.getMessage());
					JOptionPane.showMessageDialog(gui.getFrmEizikiuClient(), message.getMessage(), "Message from Server", 1);
					break;
				
				case 23:	// private chat ACK -> new private chat
							// Message(name of chat partner, room name, 23, roomID)
					EZKlogger.debug("private chat ACK received");
					room = new Room(message.getSenderName(), message.getRoomID());
					if(user.getRooms().add(room)) {
						EZKlogger.debug("room " + room.toString() + " was added to users room list");
						gui.newChat(message.getMessage(), room);
						EZKlogger.log("user joined private chat " + room);
					} else {
						EZKlogger.debug("ERROR: could not add " + room.toString() + " to users room list");
					}
					break;
					
				case 25:	// join room ACK -> get room from public room list
							// Message('successful opened' from server, senderName, 25, roomID)
					EZKlogger.debug("join room ACK received");
					room = null;
					for(Room x : publicRooms) {
//						EZKlogger.debug("x.roomID = " + x.getID() + "; message.roomID = " + message.getRoomID());
						if(x.getID() == message.getRoomID()) {
							room = x;
							if(user.getRooms().add(room)) {
								EZKlogger.debug("the room " + room + " was added to users room list:");
								gui.newChat(room);
								EZKlogger.log("user joined room " + room);
							}
							break;
						}
					}
					if(room == null) {
						EZKlogger.debug("ERROR: room is not in public rooms list!");
					}
					break;
				
				case 9:		// general NAK
				case 24:	// private chat NAK 
				case 26:	// join room NAK 	-> dialog box ERROR
					EZKlogger.debug("NAK received");
					EZKlogger.log("error message from server: " + message.getMessage());
					JOptionPane.showMessageDialog(gui.getFrmEizikiuClient(), message.getMessage(), "ERROR", 0);
					break;
					
				case 27:	// receive room list
					EZKlogger.debug("room list received");
					
					tempRoomList = new LinkedList<>();
					tempString = message.getMessage(); // message is "roomName1§roomID1§roomName2§roomID2§....§roomNameX§roomIDX"
					parts = tempString.split("§");
					for(int i=0; i<parts.length; i+=2) {
						String name = parts[i];
						int ID = Integer.parseInt(parts[i+1]);
						tempRoomList.add(new Room(name, ID));
					}
					
					for(Room x : tempRoomList) {
						boolean oldListHasID = false;
						for(Room y : publicRooms) {
							if(y.getID() == x.getID()) {
								oldListHasID = true;
								if(y.getName().equals(x.getName())) { // room did not change
									x = y;
								} else { // room name changed
									EZKlogger.debug(y.toString() + " changed name to " + x.toString());
									y.setName(x.getName());
									x = y;
								}
							}
						}
						if(!oldListHasID) { // new room
							EZKlogger.debug("new room " + x.toString() + " added to list");
						}
					}
					publicRooms = tempRoomList;
					gui.actualizeRoomJList();
					break;
					
				case 28:	// receive user list
					EZKlogger.debug("user list received");
					room = null;
					tempUserList = new LinkedList<>();
					tempString = message.getMessage(); // message is "userName1§userName2§...§userNameX"
					parts = tempString.split("§");
					// make new user list
					for(String x : parts) {
						tempUserList.add(new User(x, "noPW"));
					}
					// get target room and user list
					if(message.getRoomID() == 0) {
						targetUserList = globalUserList;
						EZKlogger.debug("target is global user list");
					} else {
						for(Room x : publicRooms) {
							if(x.getID() == message.getRoomID()) {
								room = x;
							}
						}
						if(room != null) {
							EZKlogger.debug("target room is " + room.toString());
							targetUserList = room.getUserList();
						} else {
							EZKlogger.debug("ERROR: no room with ID " + message.getRoomID() + " in public room list!");
							break;
						}
					}
					// copy users that not have changed to new list  
					for(User x : tempUserList) {
						boolean oldListHasUser = false;
						for(User y : targetUserList) {
							if(y.getName().equals(x.getName())) {
								oldListHasUser = true;
								x = y;
							}
						}
						if(!oldListHasUser) { // new room
							EZKlogger.debug("new user " + x.toString() + " added to list");
						}
					}
					// set new list
					if(message.getRoomID() == 0) {
						globalUserList = tempUserList;
						gui.actualizeUserJList();
					} else {
						room.setUserList(tempUserList);
					}
					break;
				
				case 29:	// warning -> dialog box WARNING
					EZKlogger.debug("warning message received");
					EZKlogger.log("warning from server: " + message.getMessage());
					JOptionPane.showMessageDialog(gui.getFrmEizikiuClient(), message.getMessage(), "WARNING", 2);
					break;
				
				default: // error
					EZKlogger.debug("received message of unexpected type: " + messageType);
				} // switch
			} // while
			
			EZKlogger.log("shutting down...");
			netInput.closeStreams();
			netOutput.closeStreams();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void privateChatRequest(String userName) throws Exception {
		EZKlogger.debug();
		// Message(name of requested chat partner, senderName, 13, 0)
		if(!userName.equals(user.getName())) {
			netOutput.sendMessage(new Message(userName, user.getName(), 13, 0));			
		}
	}
	
	public static void publicChatRequest(int roomID) throws Exception{
		EZKlogger.debug();
		String roomName = "";
		for(Room x : publicRooms) {
			if(x.getID() == roomID) roomName = x.getName();
		}
		// Message(room name, senderName, 15, roomID)
		netOutput.sendMessage(new Message(roomName, user.getName(), 15, roomID));
	}
	
	public static void chatLeave(int roomID) throws Exception{
		EZKlogger.debug();
		boolean isPublic = false;
		for(Room x : publicRooms) {
			if(x.getID() == roomID) {
				isPublic = true;
			}
		}
		EZKlogger.debug(isPublic ? "room is public" : "room is private");
		
		Room room = null;
		for(Room x : user.getRooms()) {
//			EZKlogger.debug("for each rooms of user");
			if(roomID != 1) {
				if(x.getID() == roomID) {
					room = x;
					EZKlogger.debug("leaving room " + room.toString());
				}
			}
		}
		if(room != null) {
			// Message(room name, senderName, 14(private)/16(public), roomID)
			netOutput.sendMessage(new Message(room.getName(), user.getName(), isPublic ? 16 : 14, roomID));
			if(user.getRooms().remove(room)) {
				EZKlogger.debug("removed the room " + room.toString() + " from users room list");
			} else {
				EZKlogger.debug("ERROR: could not remove " + room.toString() + " from users room list!");
			}
		} else {
			EZKlogger.debug("ERROR: room with id " + roomID + " is not in users room list!");
		}
	}
	
	public static void roomListRequest() throws Exception{
		EZKlogger.debug();
		netOutput.sendMessage(new Message("room list request", user.getName(), 17, 0));
	}
	
	public static void userListRequest(int roomID) throws Exception{
		EZKlogger.debug();
		netOutput.sendMessage(new Message("user list request", user.getName(), 18, roomID));
	}
	
	public static void sendMessage(String message, int roomID) throws Exception{ 
		EZKlogger.debug();
		boolean isPublic = false;
		for(Room x : publicRooms) {
			if(x.getID() == roomID) {
				isPublic = true;
			}
		}
		netOutput.sendMessage(new Message(message, user.getName(), isPublic ? 1 : 2, roomID));
	}
	
	public static void shutdown() throws Exception{
		EZKlogger.debug();
		netOutput.sendMessage(new Message("exit", user.getName(), 0, 0));
	}
}

