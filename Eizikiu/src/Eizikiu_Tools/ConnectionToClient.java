package Eizikiu_Tools;

import Eizikiu_Server.*;
import java.net.*;
import java.util.*;

import Eizikiu_GUI.Eizikiu_Server_GUI;

public class ConnectionToClient implements Runnable {
	
	private LinkedList<ConnectionToClient> connectionList;
	private LinkedList<User> globalUserList;
	private LinkedList<Room> publicRooms;
	private LinkedList<Room> privateRooms;
	private OutputStreamSet netOutput;
	private InputStreamSet netInput;
	private Socket socket;
	private User user;
	private Eizikiu_Server_GUI gui;
	
	// constructors
	public ConnectionToClient(){EZKlogger.debug();}
	
	public ConnectionToClient(Socket socket, Eizikiu_Server_GUI gui) {
		EZKlogger.debug();
		this.connectionList = Eizikiu_Server.getConnectionList();
		this.globalUserList = Eizikiu_Server.getGlobalUserList();
		this.publicRooms = Eizikiu_Server.getPublicRooms();
		this.privateRooms = Eizikiu_Server.getPrivateRooms();
		this.socket = socket;
		this.user = new User("name", "password");
		this.netInput = new InputStreamSet(socket);
		this.netOutput = new OutputStreamSet(socket);
		this.gui = gui;
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
				
				// send current user list to client
				String userListString = Eizikiu_Server.onlineUsersToString();
				netOutput.sendMessage(new Message(userListString, "Server---------->", 28, 0));
				
				// send current room list to client
				String roomListString = Eizikiu_Server.publicRoomsToString();
				netOutput.sendMessage(new Message(roomListString, "Server---------->", 27, 0));
				
				// "user online" to other clients
				for(ConnectionToClient x : connectionList){
					if(!this.equals(x)){
						x.netOutput.sendMessage(new Message("[" + user.getName() + "] is online", "Server---------->", 20, 0));
					}
				}
								
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
								EZKlogger.debug("requested chatpartner [" + chatPartner.getName() + "] is online");
							}
						}
						if(chatPartner != null) {
							// check if private chat with these two users already exists
							boolean check = false;
							for(Room x : privateRooms) {
								if(x.hasUsers(user, chatPartner)) {
									check = true;
									EZKlogger.debug("private chat with users [" + user.getName() + "] and [" + chatPartner.getName() + "] already exists");
								}
							}
							if(!check) { // all good -> create room, add users, add room to 'privateRooms', add room to users room list, send ACK to both clients
								EZKlogger.debug("private chat with users [" + user.getName() + "] and [" + chatPartner.getName() + "] not existing yet");
								Room newRoom = new Room(user.getName() + "PRIVATE" + chatPartner.getName());
								EZKlogger.debug("new private room created: " + newRoom.toString());
								if(newRoom.getUserList().add(user) && newRoom.getUserList().add(chatPartner)) {
									EZKlogger.debug("users [" + user.getName() + "] and [" + chatPartner.getName() + "] added to new room");
								}
								if(privateRooms.add(newRoom)) {
									EZKlogger.debug("new room added to 'privateRooms'");
								}
								if(user.getRooms().add(newRoom) && chatPartner.getRooms().add(newRoom)) {
									EZKlogger.debug("new room added to room list of [" + user.getName() + "] and [" + chatPartner.getName() + "]");
								}
								netOutput.sendMessage(new Message(chatPartner.getName(), newRoom.getName(), 23, newRoom.getID()));
								chatPartner.getConnection().netOutput.sendMessage(new Message(user.getName(), newRoom.getName(), 23, newRoom.getID()));
								// due to message type is 23 'senderName' delivers room name
								EZKlogger.log("new privat chat: " + newRoom.toString());
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
								EZKlogger.debug("requested private room to leave is: " + room.toString());
								break;
							}
						}
						if(room != null) {
							for(User x : room.getUserList()) {
								if(!x.equals(user)) {
									x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] has left your private chat. You may close this Window now.", "Server---------->", 2, room.getID()));
								}
								if(x.getRooms().remove(room)) {
									EZKlogger.debug("the following room was removed from room list of [" + x.getName() + "]");
									EZKlogger.debug(room.toString());
								}
							}
							if(privateRooms.remove(room)) {
								EZKlogger.debug("the following room was removed from 'privateRooms'");
								EZKlogger.debug(room.toString());
							}
							Integer i = room.getID();
							if(Room.getIDList().remove(i)) {
								EZKlogger.log("The ID " + i + " got removed from ID list.");
							}
						} else {
							EZKlogger.debug("requested private chat to leave has already been closed");
						}
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
								     //-> send "user joined" to other members; add user to rooms user list; add room to users room list;
									 //   send ACK to client; send changed user list to room members
								if(room.getUserList().add(user)) {
									for(User x : room.getUserList()) {
										if(!x.equals(user)) {
											x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] joined this room", "Server---------->", 1, room.getID()));
										}
									}
									Eizikiu_Server.sendUserListToAllMembersOf(room);
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
							// remove room from users room list; remove user from rooms user list; send "user left" to other members; send new user list to all members
							user.getRooms().remove(room);
							if(room.getUserList().remove(user)) {
								for(User x : room.getUserList()) {
									x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] left this room", "Server---------->", 1, room.getID()));
								}
								Eizikiu_Server.sendUserListToAllMembersOf(room);
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
						roomList = Eizikiu_Server.publicRoomsToString();
						netOutput.sendMessage(new Message(roomList, "Server---------->", 27, 0));
						break;
					
					case 18: // user list request - sends users of room with message.getRoomID in a string
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
							userList = Eizikiu_Server.makeUserListToString(tempUserList);
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
			
			// set 'user.status' false; delete users room list; remove user from all rooms and send new user lists to all members
			user.logOut();
			
			// update gui user list
			gui.actualizeUserJList();
			
			connectionList.remove(this);

			// "user offline" to other clients
			if(!user.getName().equals("name")) { // to avoid sending offline message if client is just aborting login
				for(ConnectionToClient x : connectionList) {
					x.netOutput.sendMessage(new Message("[" + user.getName() + "] is offline", "Server---------->", 20, 0));
				}				
			}
						
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
		boolean nameIsInUserList = true;
		
		try {
			while(!userValid){
				// login or registration?
				Message message = netInput.receiveMessage();
				int messageType = message.getType();
				
				switch(messageType) {
	
				case 10: // register new user and login
					EZKlogger.debug("...register new user...");
					// receive user credential messages until user name entered by client is unique 
					message = netInput.receiveMessage();
					messageType = message.getType();
					if(messageType == 12) {
						EZKlogger.debug(message.getSenderName() + ".register: user credentials message received");
						EZKlogger.debug(message.getSenderName() + ".register: " + message);
						
						user.setName(message.getSenderName());
						user.setPassword(message.getMessage());

						EZKlogger.debug(user.getName() + ".register: user credentials set");
						
						nameIsInUserList = false;
						for(User x : globalUserList){
							if(x.getName().equals(user.getName())){
								EZKlogger.debug(user.getName() + ".register: REJECTED: name already in global user list");
								// send NAK to tell client to try again
								nameIsInUserList = true;
								netOutput.sendMessage(new Message("The name '" + user.getName() + "' is already taken! Try again!", "server", 9, 0));
								break;
							}
						}
					} else {
						// send NAK to client in case of wrong message type
						EZKlogger.debug(message.getSenderName() + ".register: ERROR: expected message type 12, received type " + messageType);
						netOutput.sendMessage(new Message("Sorry, network error! Try again!", "server", 9, 0));
					}
					
					// add new user to list
					if(!nameIsInUserList) {
						EZKlogger.debug(user.getName() + ".register: name is not in global user list yet");
						if(globalUserList.add(user)) {
							EZKlogger.log(user.getName() + ".register: added to global user list");
						} else {
							EZKlogger.log(user.getName() + ".register: ERROR: user not added to global user list!");
						}
						userValid = true;	
					}					
					break;
	
				case 11: // login user
					EZKlogger.debug("...login user...");
					// receive user credential messages until user name entered by client is in list 
					nameIsInUserList = false;
					message = netInput.receiveMessage();
					messageType = message.getType();
					if(messageType == 12) {
						EZKlogger.debug(message.getSenderName() + ".login: user credentials message received");
						EZKlogger.debug(message.getSenderName() + ".login: " + message);
						
						user.setName(message.getSenderName());
						user.setPassword(message.getMessage());

						EZKlogger.debug(user.getName() + ".login: user credentials set");
						
						for(User x : globalUserList){
							if(x.getName().equals(user.getName())){
								nameIsInUserList = true;
								EZKlogger.debug(user.getName() + ".login: name is in user list");
								if(!x.isBanned()) {
									EZKlogger.debug(user.getName() + ".login: user is not banned");
									if(!x.isStatus()){
										EZKlogger.debug(user.getName() + ".login: user is not online yet");
										EZKlogger.debug(user.getName() + ".login: password on server: " + x.getPassword());
										EZKlogger.debug(user.getName() + ".login: password received : " + user.getPassword());
										if(x.getPassword().equals(user.getPassword())){
											userValid = true;
											user = x;
											EZKlogger.debug(user.getName() + ".login: password correct");
										} else { // when password is wrong
											EZKlogger.debug(user.getName() + ".login: REJECTED: password not correct");
											netOutput.sendMessage(new Message("The entered password is wrong! Try again!", "server", 9, 0));
										}
									} else { // when status is true
										EZKlogger.debug(user.getName() + ".login: REJECTED: user allready logged in");
										netOutput.sendMessage(new Message("The user named '" + user.getName() + "' is already logged in! Try again!", "server", 9, 0));
									}
								} else { // user is banned
									EZKlogger.debug(user.getName() + ".login: REJECTED: user is banned");
									netOutput.sendMessage(new Message("Sorry! You are banned from Server!", "server", 9, 0));
								}
							}
						}
						
						if(!nameIsInUserList) {
							EZKlogger.debug(user.getName() + ".login: REJECTED: name is not in global user list");
							netOutput.sendMessage(new Message("The user named '" + user.getName() + "' does not exist! Please register first!", "server", 9, 0));
						}
					} else { // when message type is not 12
						EZKlogger.debug(message.getSenderName() + ".login: ERROR: expected message type 12, received type " + messageType);
						netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9, 0));
					}
					break;
				default:
					EZKlogger.debug(message.getSenderName() + "ERROR: expected message type 10 or 11, received type " + messageType);
					netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9, 0));
				} // switch
			} // while (!userValid)
			// user is valid now:
			// send positive ACK for successful login to client
			EZKlogger.debug(user.getName() + ": user is valid");
			netOutput.sendMessage(new Message("Login successful!", "server", 8, 0));
			// log in after ACK because client is awaiting ACK before new user list of default room which is send during user.login()
			if(connectionList.add(this)) {
				EZKlogger.debug(user.getName() + ": connection added to list");
			} else {
				EZKlogger.debug(user.getName() + ": ERROR: connection not added to list");
			}
			user.setConnection(this);
			user.logIn();
			// update gui user list
			gui.actualizeUserJList();
			EZKlogger.log(user.getName() + ".ConnectionToClient.run() -> password check -> user [" + user.getName() + "] logged in");
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
