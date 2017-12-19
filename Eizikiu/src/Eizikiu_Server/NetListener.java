package Eizikiu_Server;

import java.net.*;
import java.util.*;
import Eizikiu_Tools.*;

public class NetListener implements Runnable {
	
	LinkedList<ConnectionToClient> connectionList;
	LinkedList<User> userList;

	NetListener(LinkedList<ConnectionToClient> connectionList, LinkedList<User> userList){
		this.connectionList = connectionList;
		this.userList = userList;
	}
	
	@Override
	public void run() {
		
		System.out.println("NetListener.run() --> ...succesfully!");

		while(true) {
			
			try {
				ServerSocket listener = new ServerSocket(1234);
				System.out.println("NetListener.run() --> ServerSocket aufgebaut\n");

				Socket socket = listener.accept(); // wait for connection
				
				Thread x = new Thread(new ConnectionToClient(socket, connectionList, userList));
				x.start();
				
				listener.close();
				
			} catch (Exception e) {
				e.printStackTrace();
				}
		}

	}

}
