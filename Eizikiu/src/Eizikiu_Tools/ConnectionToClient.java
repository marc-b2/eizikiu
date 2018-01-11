package Eizikiu_Tools;

import java.io.IOException;
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
	
	// Konstruktoren
	public ConnectionToClient(){}
	
	public ConnectionToClient(Socket socket) {
		this.connectionList = Eizikiu_Server.Eizikiu_Server.getConnectionList();
		this.globalUserList = Eizikiu_Server.Eizikiu_Server.getGlobalUserList();
		this.publicRooms = Eizikiu_Server.Eizikiu_Server.getPublicRooms();
		this.privateRooms = Eizikiu_Server.Eizikiu_Server.getPrivateRoooms();
		this.socket = socket;
		this.user = new User("name", "password");
		this.netInput = new InputStreamSet(socket);
		this.netOutput = new OutputStreamSet(socket);
	}
	
	// Methoden
	@Override
	public void run() {
		
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
					
					case 14: // leave private chat request
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
									x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] has left your private chat. You may close this Window now.", "Server---------->", 20, room.getID()));
								}
								x.getRooms().remove(room);
							}
							privateRooms.remove(room);
						} // else not necessary. if room==null room is already closed by other chat partner
						break;
						
					case 15: // join room request
							 // Message(room name, senderName, 15, roomID)
						room = null;
						
						
						break;
					case 16: // leave room request
						break;
					default: // error
					}
					
				}while(!exit);

				netOutput.sendMessage(new Message("exit", "Server"));
			}								
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			user.logOut();
			
			// "user offline" to other clients
			for(User x : globalUserList) {
				if(x.isStatus() && !user.equals(x)) {
					x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] is offline", "Server---------->", 20, 0));
				}
			}
			
			connectionList.remove(this);
			
			netInput.closeStreams();
			netOutput.closeStreams();
			socket.close();

			EZKlogger.debug("ConnectionToClient.run() -> connection to [" + user.getName() + "] terminated\n");

		} catch(IOException e) {
			e.printStackTrace();
		}
		}
	
	public boolean loginUser() {
		
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
//						message = netInput.receiveMessage();
//						messageType = message.getType();
						if(messageType == 12) {
							user.setName(message.getSenderName());
							user.setPassword(message.getMessage());
							
							EZKlogger.debug("ConnectionToClient.run() -> password check -> user credentials message received");
							EZKlogger.debug("ConnectionToClient.run() -> password check -> " + message);
							
							for(User x : globalUserList){
								if(x.getName().equals(user.getName())){
									EZKlogger.debug("ConnectionToClient.run() -> password check -> name already in user list");
									// send negative ACK to client to tell client to try again
									netOutput.sendMessage(new Message("The name '" + user.getName() + "' is already taken! Try again!", "server", 9, 0));
									break;
								}
							}
							// if for loop does not give 'break' name is not in list
							nameIsInUserList = false;
						} else {
							// send negative ACK to client is case of wrong message type
							EZKlogger.debug("ConnectionToClient.run() -> password check -> wrong message received, type is " + messageType);
							netOutput.sendMessage(new Message("Sorry, network error! Try again!", "server", 9, 0));
						}
					} while(nameIsInUserList);
					
					// add new user to list
					user.addTo(globalUserList);
					EZKlogger.log("ConnectionToClient.run() -> password check -> new user" + user.getName() + "added to user list");
					userValid = true;
					break;
	
				case 11: // login user
					// receive user credential messages until user name entered by client is in list 
					nameIsInUserList = false;
					do {
//						message = netInput.receiveMessage();
//						messageType = message.getType();
						if(messageType == 12) {
							user.setName(message.getSenderName());
							user.setPassword(message.getMessage());
							
							EZKlogger.debug("ConnectionToClient.run() -> password check -> user credentials message received");
							EZKlogger.debug("ConnectionToClient.run() -> password check -> " + message);
							
							for(User x : globalUserList){
								if(x.getName().equals(user.getName())){
									nameIsInUserList = true;
									EZKlogger.debug("ConnectionToClient.run() -> password check -> name is in user list");
									if(!x.isStatus()){
										EZKlogger.debug("ConnectionToClient.run() -> password check -> pw in list: " + x.getPassword() + " ----- pw client: " + user.getPassword());
										if(x.getPassword().equals(user.getPassword())){
											userValid = true;
											user = x;
											EZKlogger.debug("ConnectionToClient.run() -> password check -> password correct");
										} else { // when password is wrong
											EZKlogger.debug("ConnectionToClient.run() -> password check -> password not correct");
											netOutput.sendMessage(new Message("The entered password is wrong! Try again!", "server", 9, 0));
										}
									}else{ // when status is true
										EZKlogger.debug("ConnectionToClient.run() -> password check -> user allready logged in");
										netOutput.sendMessage(new Message("The user named '" + user.getName() + "' is already logged in! Try again!", "server", 9, 0));
									}
								}
							}
							
							if(!nameIsInUserList) {
								EZKlogger.debug("ConnectionToClient.run() -> password check -> name is not in user list");
								netOutput.sendMessage(new Message("The user named '" + user.getName() + "' does not exist! Please register first!", "server", 9, 0));
							}
						} else { // when message type is not 12
							EZKlogger.debug("ConnectionToClient.run() -> password check -> wrong message received, type is " + messageType);
							netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9, 0));
						}
					} while(!nameIsInUserList);
					break;
				default:
					EZKlogger.debug("ConnectionToClient.run() -> password check -> message type not 10 or 11");
					netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9, 0));
				} // switch
			} // while (!userValid)
			
			// user is valid now:
			user.logIn();
			user.setConnection(this);
			connectionList.add(this);
			// send positive ACK for successful login to client
			EZKlogger.log("ConnectionToClient.run() -> password check -> user '" + user.getName() + "' logged in");
			netOutput.sendMessage(new Message("Login successful!", "server", 8, 0));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public User getUser() {
		return user;
	}
	
	public void shutdown() {
		netOutput.sendMessage(new Message("connection shut down by server", "Server---------->"));
		netOutput.sendMessage(new Message("exit", "Server"));
//		user.logOut();
//		netInput.closeStreams();
//		netOutput.closeStreams();
//		socket.close();
	}

}
