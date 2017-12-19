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

	public ConnectionToClient(){}
	
	public ConnectionToClient(Socket socket, LinkedList<ConnectionToClient> connectionList, LinkedList<User> userList){
		this.connectionList = connectionList;
		this.userList = userList;
		this.socket = socket;
		this.user = new User("name", "password");
		this.netInput = new InputStreamSet(socket);
		this.netOutput = new OutputStreamSet(socket);
	}
	
	@Override
	public void run() {
		
		boolean exit = false;
			
			try {
				netOutput.setupStreams();
				netInput.setupStreams();
				
				System.out.println("ConnectionToClient.run() -> connection to new user established");
				
				// password check
				boolean userValid = false;
				while(!userValid){
//					System.out.println("ConnectionToClient.run() -> password check -> wait for name string object from client");
//					user.setName(netInput.receiveString());
					
//					System.out.println("ConnectionToClient.run() -> password check -> wait for password string object from client\n");
//					user.setPassword(netInput.receiveString());
					System.out.println("ConnectionToClient.run() -> password check -> user string objects received");
					user = netInput.receiveUser();
					System.out.println("ConnectionToClient.run() -> password check -> " + user.getName() + " ---- " + user.getPassword());
					
					boolean nameIsInUserList = false;
					for(User x : userList){
						if(x.getName().equals(user.getName())){
							nameIsInUserList = true;
							System.out.println("ConnectionToClient.run() -> password check -> name is in user list");
							if(!x.isStatus()){	
								System.out.println("ConnectionToClient.run() -> password check -> pw in list: " + x.getPassword() + " ----- pw client: " + user.getPassword());
								if(x.getPassword().equals(user.getPassword())){
									userValid = true;
									user = x;
									System.out.println("ConnectionToClient.run() -> password check -> password correct");
								}
							}else{
								System.out.println("ConnectionToClient.run() -> password check -> user allready logged in");
							}
						}
					}
					
					if(!nameIsInUserList){
						userValid = true;
						user.addTo(userList);
						System.out.println("ConnectionToClient.run() -> password check -> new user added to user list");
					}
					
					if(userValid){
						connectionList.add(this);
						user.logIn();
						user.setConnection(this);
						netOutput.sendMessage(new Message("userValid", "Server"));
						
						System.out.println("ConnectionToClient.run() -> password check -> user valid\n");
					}else{
						netOutput.sendMessage(new Message("userNotValid", "Server"));
						System.out.println("ConnectionToClient.run() -> password check -> user NOT valid\n");
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
//					System.out.println(message);
//					System.exit(0);
//					
					if(!message.getMessage().equals("exit")){
						for(User x : userList){
							if(x.isStatus()){
								x.getConnection().netOutput.sendMessage(message);
							}
						}
					}else{
						exit=true;
					}				
				}while(!exit);// && netInput.isAvailable());

				netOutput.sendMessage(new Message("exit", "Server"));
								
			} catch (Exception e) {
//				System.out.println("you catched em all");
				e.printStackTrace();
//				exit = true;
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

				System.out.println("ConnectionToClient.run() -> connection to [" + user.getName() + "] terminated\n");

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
