package Eizikiu_Tools;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class ConnectionToClient implements Runnable{
	
	LinkedList<ConnectionToClient> connectionList;
	LinkedList<User> userList;
	OutputStreamSet netOutput;
	InputStreamSet netInput;
	Socket socket;
	User user;
	
	// Konstruktoren
	public ConnectionToClient(){}
	
	public ConnectionToClient(Socket socket, LinkedList<ConnectionToClient> connectionList, LinkedList<User> userList){
		this.connectionList = connectionList;
		this.userList = userList;
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
				
				// password check
				boolean userValid = false;
				boolean nameIsInUserList;
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
								
								EZKlogger.debug("ConnectionToClient.run() -> password check -> user credentials message received");
								EZKlogger.debug("ConnectionToClient.run() -> password check -> " + message);
								
								for(User x : userList){
									if(x.getName().equals(user.getName())){
										EZKlogger.debug("ConnectionToClient.run() -> password check -> name already in user list");
										// send negative ACK to client to tell client to try again
										netOutput.sendMessage(new Message("The name '" + user.getName() + "' is already taken! Try again!", "server", 9));
										break;
									}
								}
								// if for loop does not give 'break' name is not in list
								nameIsInUserList = false;
							} else {
								// send negative ACK to client is case of wrong message type
								EZKlogger.debug("ConnectionToClient.run() -> password check -> wrong message received, type is " + messageType);
								netOutput.sendMessage(new Message("Sorry, network error! Try again!", "server", 9));
							}
						} while(nameIsInUserList);
						
						// add new user to list
						user.addTo(userList);
						EZKlogger.log("ConnectionToClient.run() -> password check -> new user" + user.getName() + "added to user list");
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
								
								EZKlogger.debug("ConnectionToClient.run() -> password check -> user credentials message received");
								EZKlogger.debug("ConnectionToClient.run() -> password check -> " + message);
								
								for(User x : userList){
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
												netOutput.sendMessage(new Message("The entered password is wrong! Try again!", "server", 9));
											}
										}else{ // when status is true
											EZKlogger.debug("ConnectionToClient.run() -> password check -> user allready logged in");
											netOutput.sendMessage(new Message("The user named '" + user.getName() + "' is already logged in! Try again!", "server", 9));
										}
									}
								}
								
								if(!nameIsInUserList) {
									EZKlogger.debug("ConnectionToClient.run() -> password check -> name is not in user list");
									netOutput.sendMessage(new Message("The user named '" + user.getName() + "' does not exist! Please register first!", "server", 9));
								}
							} else { // when message type is not 12
								EZKlogger.debug("ConnectionToClient.run() -> password check -> wrong message received, type is " + messageType);
								netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9));
							}
						} while(!nameIsInUserList);
						break;
					default:
						EZKlogger.debug("ConnectionToClient.run() -> password check -> message type not 10 or 11");
						netOutput.sendMessage(new Message("Sorry, unknown error! Try again!", "server", 9));
					} // switch
				} // while (!userValid)
				
				// user is valid now:
				user.logIn();
				user.setConnection(this);
				connectionList.add(this);
				// send positive ACK for successful login to client
				EZKlogger.log("ConnectionToClient.run() -> password check -> user '" + user.getName() + "' logged in");
				netOutput.sendMessage(new Message("Login successful!", "server", 8));
				
				// +++++++++++++++++++++ TODO
				// "user joined" to other clients
				for(User x : userList){
					if(x.isStatus() && !user.equals(x)){
						x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] joined channel", "Server---------->"));
					}
				}
				
				// send current user list to client
				netOutput.sendMessage(new Message("these users are currently logged in:", "Server---------->"));
				for(User x : userList){
					if(x.isStatus() && !user.equals(x)){
						netOutput.sendMessage(new Message(x.getName(), "Server---------->"));
					}
				}
				
				// chat
				do{
					Message message = netInput.receiveMessage();

					if(!message.getMessage().equals("exit")){
						for(User x : userList){
							if(x.isStatus()){
								x.getConnection().netOutput.sendMessage(message);
							}
						}
					}else{
						exit=true;
					}				
				}while(!exit);

				netOutput.sendMessage(new Message("exit", "Server"));
								
			} catch (Exception e) {
				e.printStackTrace();
			}

			try{
				user.logOut();
				
				// "user left" to other clients
				for(User x : userList){
					if(x.isStatus() && !user.equals(x)){
						x.getConnection().netOutput.sendMessage(new Message("[" + user.getName() + "] left channel", "Server---------->"));
					}
				}
				
				connectionList.remove(this);
				
				netInput.closeStreams();
				netOutput.closeStreams();
				socket.close();

				EZKlogger.debug("ConnectionToClient.run() -> connection to [" + user.getName() + "] terminated\n");

			}catch(IOException e){
				e.printStackTrace();
			}
		}
	
	public User getUser(){
		return user;
	}
	
	public void shutdown(){
		netOutput.sendMessage(new Message("connection shut down by server", "Server---------->"));
		netOutput.sendMessage(new Message("exit", "Server"));
//		user.logOut();
//		netInput.closeStreams();
//		netOutput.closeStreams();
//		socket.close();
	}

}
