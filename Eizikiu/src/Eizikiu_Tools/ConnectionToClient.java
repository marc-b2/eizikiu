package Eizikiu_Tools;

import Eizikiu_Server.*;
import java.net.*;
import java.util.*;

public class ConnectionToClient implements Runnable {
	
	private LinkedList<ConnectionToClient> connectionList;
	private LinkedList<User> globalUserList;
	private LinkedList<Room> publicRooms;
	private LinkedList<Room> privateRooms;
	private OutputStreamSet netOutput;
	private InputStreamSet netInput;
	private Socket socket;
	private User user;
	
	// constructors
	public ConnectionToClient(){EZKlogger.debug();}
	
	public ConnectionToClient(Socket socket) {
		EZKlogger.debug();
		this.connectionList = Eizikiu_Server.getConnectionList();
		this.globalUserList = Eizikiu_Server.getGlobalUserList();
		this.publicRooms = Eizikiu_Server.getPublicRooms();
		this.privateRooms = Eizikiu_Server.getPrivateRooms();
		this.socket = socket;
		this.user = new User("name", "password");
		this.netInput = new InputStreamSet(socket);
		this.netOutput = new OutputStreamSet(socket);
	}
	
	// functions
	@Override
	public void run() {
		EZKlogger.debug();
		boolean exit = false;
			
		try {
			netOutput.setupStreams();
			netInput.setupStreams();
			
			EZKlogger.log("ConnectionToClient.run() -> connection to new user established");
			
			if(loginUser()) {
				// "user online" to other clients
				for(User x : globalUserList){
					if(x.isStatus() && !user.equals(x)){
						x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] is online", "Server---------->", 20, 0));
					}
				}
				
				// send current user list to client
				netOutput.sendMessage(new Message("these users are currently logged in:", "Server---------->", 20, 1));
				for(User x : globalUserList){
					if(x.isStatus() && !user.equals(x)){
						netOutput.sendMessage(new Message(x.getName(), "Server---------->", 20, 1));
					}
				}
				
				//TODO: send current room list to client
				
				// chat
				do {
					Message message = netInput.receiveMessage();
					int messageType = message.getType();
					Room room = null;
					LinkedList<User> tempUserList;
					String roomList;
					String userList;
					
					switch(messageType) {
					case 0: // exit
						exit = true;
						break;
					
					case 1: // standard public message
						room = null;
						for(Room x : publicRooms) {
							if(x.getID() == message.getRoomID()) {
								room = x;
							}
						}
						
						if(room!=null) {
							for(User x : room.getUserList()) {
								if(x.isStatus()){
									x.getConnection().netOutput.sendMessage(message);
								}
							}
						} else {
							netOutput.sendMessage(new Message("Sorry, this room does not exist anymore. Please leave!", "Server---------->", 1, message.getRoomID()));
						}
						break;
					
					case 2: // standard private message
						room = null;
						for(Room x : privateRooms) {
							if(x.getID() == message.getRoomID()) {
								room = x;
							}
						}
						
						if(room!=null) {
							for(User x : room.getUserList()) {
								if(x.isStatus()){
									x.getConnection().netOutput.sendMessage(message);
								}
							}
						} else {
							netOutput.sendMessage(new Message("Sorry, this room does not exist anymore. Please leave!", "Server---------->", 2, message.getRoomID()));
						}
						break;
						
					case 13: // new private chat request
							 // Message(name of requested chat partner, senderName, 13, 0)
						
						// check if requested chat partner is existing and online
						User chatPartner = null;
						for(User x : globalUserList) {
							if(x.isStatus() && x.getName().equals(message.getMessage())) { // due to message type is 13 'message' holds name of second user
								chatPartner = x;
							}
						}
						if(chatPartner != null) {
							// check if private chat with these two users already exists
							boolean check = false;
							for(Room x : privateRooms) {
								if(x.hasUsers(user, chatPartner)) {
									check = true;
								}
							}
							if(!check) { // all good -> create room, add users, add room to 'privateRooms', add room to users room list, send ACK to both clients
								Room newRoom = new Room(user.getName() + "PRIVATE" + chatPartner.getName());
								newRoom.addUser(user);
								newRoom.addUser(chatPartner);
								privateRooms.add(newRoom);
								user.getRooms().add(newRoom);
								chatPartner.getRooms().add(newRoom);
								netOutput.sendMessage(new Message("private chat to [" + chatPartner.getName() + "] opened", newRoom.getName(), 23, newRoom.getID()));
								chatPartner.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] opened private chat", newRoom.getName(), 23, newRoom.getID()));
								// due to message type is 23 'senderName' delivers room name
							} else {
								netOutput.sendMessage(new Message("You are already talking to [" + chatPartner.getName() + "]!", "Server---------->", 24, 0));
							}
						} else { // requested chat partner is not online or existing -> send NAK
							netOutput.sendMessage(new Message("The requested chat partner is not online!", "Server---------->", 24, 0));
						}
						break;
					
					case 14: // leave private chat notification
							 // Message(room name, senderName, 14, roomID)
						room = null;
						for(Room x : privateRooms) {
							if(x.getID() == message.getRoomID() && message.getMessage().equals(x.getName())) { // due to message type is 14 'message' holds room name
								room = x;
							}
						}
						if(room != null) {
							for(User x : room.getUserList()) {
								if(!x.equals(user)) {
									x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] has left your private chat. You may close this Window now.", "Server---------->", 2, room.getID()));
								}
								x.getRooms().remove(room);
							}
							privateRooms.remove(room);
						} // else not necessary. if room==null room is already closed by other chat partner
						break;
						
					case 15: // join room request
							 // Message(room name, senderName, 15, roomID)
						room = null;
						// check if room still exists, if yes set 'room'
						for(Room x : publicRooms) {
							if(x.getID() == message.getRoomID() && message.getMessage().equals(x.getName())) room = x;
						}
						if(room != null) { // room exists
							// check if user is already member
							if(room.getUserList().contains(user)) { // user is in list
								netOutput.sendMessage(new Message("You already joined the room [" + room.getName() + "]!", "Server---------->", 26, 0));
							} else { // user is not in list -> regular join room request
								     //-> send "user joined" to other members; add user to rooms user list; add room to users room list; send ACK to client 
								if(room.getUserList().add(user)) {
									for(User x : room.getUserList()) {
										if(!x.equals(user)) {
											x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] joined this room", "Server---------->", 1, room.getID()));
										}
									}
								}
								user.getRooms().add(room);
								netOutput.sendMessage(new Message("You joined room [" + room.getName() + "]!", "Server---------->", 25, room.getID()));
							}
						} else { // room does not exist
							netOutput.sendMessage(new Message("Sorry, the room [" + message.getMessage() + "] does not exist anymore!", "Server---------->", 26, 0));
						}
						break;
						
					case 16: // leave room notification
							 // Message(room name, senderName, 16, roomID)
						room = null;
						// check if room still exists, if yes set 'room' 
						for(Room x : publicRooms) {
							if(x.getID() == message.getRoomID() && message.getMessage().equals(x.getName())) {
								room = x;
							}
						}
						if(room != null) { // regular leave room notification
							// remove room from users room list; remove user from rooms user list; send "user left" to other members
							user.getRooms().remove(room);
							if(room.getUserList().remove(user)) {
								for(User x : room.getUserList()) {
									x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] left this room", "Server---------->", 1, room.getID()));
								}
							}
						} else { // room does not exist anymore -> remove requested room from users room list if still existing 
							for(Room x : user.getRooms()) {
								if(x.getID() == message.getRoomID() && message.getMessage().equals(x.getName())) {
									room = x;
								}
							}
							if(room != null) {
								user.getRooms().remove(room);
							}
						}
						break;
					
					case 17: // room list request
						roomList = "";
						for(Room x : publicRooms) {
							if(publicRooms.indexOf(x) == publicRooms.size()-1) { // last element
								roomList = roomList + x.getName() + "§" + x.getID();
							} else {
								roomList = roomList + x.getName() + "§" + x.getID() + "§"; 								
							}
						}
						netOutput.sendMessage(new Message(roomList, "Server---------->", 27, 0));
						break;
					
					case 18: // user list request - sends users of room with message.getRoomID in a string
						userList = "";
						room = null;
						tempUserList = null;
						if(message.getRoomID() == 0) {
							tempUserList = globalUserList;
						} else {
							for(Room x : publicRooms) {
								if(message.getRoomID() == x.getID()) {
									tempUserList = x.getUserList();
								}
							}
						}
						if(tempUserList != null) {
							for(User x : tempUserList) {
								if(tempUserList.indexOf(x) == tempUserList.size()-1) { // last element
									userList = userList + x.getName();
								} else {
									userList = userList + x.getName() + "§";
								}
							}
							netOutput.sendMessage(new Message(userList, "Server---------->", 28, message.getRoomID()));
						} else { // no room with specified ID existing
							netOutput.sendMessage(new Message("Room does not exist!", "Server---------->", 9, message.getRoomID()));
						}
						break;
					
					default: // error
						EZKlogger.log(user.getName() + ".ConnectionToClient -> chat -> received message of unexpected type: " + messageType);
					}
				}while(!exit);

				netOutput.sendMessage(new Message("exit", "Server---------->", 0, 0));
			}								
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// remove user from all public rooms user lists and send "user left" to other members
			for(Room x : publicRooms) {
				if(x.getUserList().remove(user)) {
					for(User y : x.getUserList()) {
						y.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] left this room", "Server---------->", 1, x.getID()));
					}
				}	
			}
			
			// remove all private rooms where user is member and tell affected chat partners to leave and remove room from their room lists
			LinkedList<Room> tempList = new LinkedList<>();
			for(Room x : privateRooms) {
				if(x.getUserList().contains(user)) {
					tempList.add(x);
				}
			}
			if(!tempList.isEmpty()) {
				for(Room x : tempList) {
					for(User y : x.getUserList()) {
						if(!y.equals(user)) {
							y.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] has left your private chat. You may close this Window now.", "Server---------->", 2, x.getID()));
						}
						y.getRooms().remove(x);
					}
					privateRooms.remove(x);
				}
			}
			
			// set 'user.status' false and delete room list
			user.logOut();
			
			// "user offline" to other clients
			for(User x : globalUserList) {
				if(x.isStatus()) {
					x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] is offline", "Server---------->", 20, 0));
				}
			}
			
			connectionList.remove(this);
			
			netInput.closeStreams();
			netOutput.closeStreams();
			socket.close();

			EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> connection terminated");

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean loginUser() {
		EZKlogger.debug();
		boolean userValid = false;
		boolean nameIsInUserList;
		
		try {
			while(!userValid){
				// login or registration?
				Message message = netInput.receiveMessage();
				int messageType = message.getType();
				
				switch(messageType) {
	
				case 10: // register new user and login
					// receive user credential messages until user name entered by client is unique 
					nameIsInUserList = true;
					do {
						message = netInput.receiveMessage();
						messageType = message.getType();
						if(messageType == 12) {
							user.setName(message.getSenderName());
							user.setPassword(message.getMessage());
							
							EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> user credentials message received");
							EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> " + message);
							
							for(User x : globalUserList){
								if(x.getName().equals(user.getName())){
									EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> name already in user list");
									// send negative ACK to client to tell client to try again
									netOutput.sendMessage(new Message("The name '" + user.getName() + "' is already taken! Try again!", "server", 9, 0));
									break;
								}
							}
							// if for loop does not give 'break' name is not in list
							nameIsInUserList = false;
						} else {
							// send negative ACK to client in case of wrong message type
							EZKlogger.debug(message.getSenderName() + ".ConnectionToClient.run() -> password check -> wrong message received, type is " + messageType);
							netOutput.sendMessage(new Message("Sorry, network error! Try again!", "server", 9, 0));
						}
					} while(nameIsInUserList);
					
					// add new user to list
					user.addTo(globalUserList);
					EZKlogger.log(user.getName() + ".ConnectionToClient.run() -> password check -> new user [" + user.getName() + "] added to global user list");
					userValid = true;
					break;
	
				case 11: // login user
					// receive user credential messages until user name entered by client is in list 
					nameIsInUserList = false;
					do {
						message = netInput.receiveMessage();
						messageType = message.getType();
						if(messageType == 12) {
							user.setName(message.getSenderName());
							user.setPassword(message.getMessage());
							
							EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> user credentials message received");
							EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> " + message);
							
							for(User x : globalUserList){
								if(x.getName().equals(user.getName())){
									nameIsInUserList = true;
									EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> name is in user list");
									if(!x.isStatus()){
										EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> pw in list: " + x.getPassword() + " ----- pw client: " + user.getPassword());
										if(x.getPassword().equals(user.getPassword())){
											userValid = true;
											user = x;
											EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> password correct");
										} else { // when password is wrong
											EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> password not correct");
											netOutput.sendMessage(new Message("The entered password is wrong! Try again!", "server", 9, 0));
										}
									}else{ // when status is true
										EZKlogger.debug(user.getName() + ".ConnectionToClient.run() -> password check -> user allready logged in");
										netOutput.sendMessage(new Message("The user named '" + user.getName() + "' is already logged in! Try again!", "server", 9, 0));
									}
								}
							}
							
							if(!nameIsInUserList) {
								EZKlogger.debug("ConnectionToClient.run() -> password check -> name is not in user list");
								netOutput.sendMessage(new Message("The user named '" + user.getName() + "' does not exist! Please register first!", "server", 9, 0));
							}
						} else { // when message type is not 12
							EZKlogger.debug(message.getSenderName() + ".ConnectionToClient.run() -> password check -> wrong message received, type is " + messageType);
							netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9, 0));
						}
					} while(!nameIsInUserList);
					break;
				default:
					EZKlogger.debug(message.getSenderName() + ".ConnectionToClient.run() -> password check -> message type not 10 or 11");
					netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9, 0));
				} // switch
			} // while (!userValid)
			
			// user is valid now:
			user.logIn();
			user.setConnection(this);
			connectionList.add(this);
			// send positive ACK for successful login to client
			EZKlogger.log(user.getName() + ".ConnectionToClient.run() -> password check -> user [" + user.getName() + "] logged in");
			netOutput.sendMessage(new Message("Login successful!", "server", 8, 0));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void shutdown()  throws Exception{
		EZKlogger.debug();
		netOutput.sendMessage(new Message("connection shut down by server", "Server---------->", 20, 0));
		netOutput.sendMessage(new Message("exit", "Server---------->", 0, 0));
//		user.logOut();
//		netInput.closeStreams();
//		netOutput.closeStreams();
//		socket.close();
	}

	// getter
	public User getUser() {
		EZKlogger.debug();
		return user;
	}
	
	public OutputStreamSet getNetOutput() {
		EZKlogger.debug();
		return netOutput;
	}
}
