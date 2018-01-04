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
				while(!userValid){
					user = netInput.receiveUser();
					EZKlogger.debug("ConnectionToClient.run() -> password check -> user object received");
					EZKlogger.debug("ConnectionToClient.run() -> password check -> " + user.getName() + " ---- " + user.getPassword());
					
					boolean nameIsInUserList = false;
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
								}
							}else{
								EZKlogger.debug("ConnectionToClient.run() -> password check -> user allready logged in");
							}
						}
					}
					
					if(!nameIsInUserList){
						userValid = true;
						user.addTo(userList);
						EZKlogger.log("ConnectionToClient.run() -> password check -> new user" + user.getName() + "added to user list");
					}
					
					if(userValid){
						connectionList.add(this);
						user.logIn();
						user.setConnection(this);
						netOutput.sendMessage(new Message("userValid", "Server"));
						
						EZKlogger.debug("ConnectionToClient.run() -> password check -> user valid\n");
					}else{
						netOutput.sendMessage(new Message("userNotValid", "Server"));
						EZKlogger.debug("ConnectionToClient.run() -> password check -> user NOT valid\n");
					}
				}
				
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
