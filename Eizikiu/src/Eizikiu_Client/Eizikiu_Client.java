package Eizikiu_Client;

import java.net.*;
import java.util.*;
import Eizikiu_Tools.*;

public class Eizikiu_Client {

	public static void main(String args[]) {
	
		User user = null;
		String text;
		KeyboardListener keyli;
		OutputStreamSet netOutput;
		InputStreamSet netInput;

		EZKlogger.setLoglevel(0);

		try {
			Socket socket = new Socket("localhost", 1234);
			
			EZKlogger.log("Eizikiu_Client.main() -> server found");
			
			netInput = new InputStreamSet(socket);
			netOutput = new OutputStreamSet(socket);
			netInput.setupStreams();
			netOutput.setupStreams();
			
			EZKlogger.log("Eizikiu_Client.main() -> connection to server established\n\n");
						
			// password check
			Scanner keyboardIn = new Scanner(System.in);
			boolean login = false;
			while (!login) {
				user = new User(null, null);
				// get user name and password
				EZKlogger.info("Type in user name: ");
				text = keyboardIn.nextLine();
				EZKlogger.info("\n");
				user.setName(text);
				
				EZKlogger.info("Type in user password: ");
				text = keyboardIn.nextLine();
				EZKlogger.info("\n");
				user.setPassword(text);
				
				EZKlogger.debug("user: " + user.getName() + " ---- password: " + user.getPassword() + ".");
				
				// send to server
				netOutput.sendUser(user);
				
				// wait for server
				Message answer = netInput.receiveMessage();
				if (answer.getMessage().equals("userValid") && answer.getSenderName().equals("Server")) {
					login = true;
				}else{
					EZKlogger.info("\n\nThe password is wrong or the user name you entered is already in use.");
					EZKlogger.info("Please try again!\n\n");
				}
			}

			EZKlogger.info("Eizikiu_Client.main() -> You successfully logged in to the server!");
			
			keyli = new KeyboardListener(socket, netOutput, user);
			Thread keyliThread = new Thread(keyli);
			keyliThread.setDaemon(true);
			keyliThread.start();

			// chat
			boolean exit = false;
			while(!exit){
				
				Message message = netInput.receiveMessage();
				
				if(!message.getMessage().equals("exit")){
					if(!message.getSenderName().equals(user.getName())){
						EZKlogger.info(message.toString());
					}else{
						message.printOwn();
					}
				}else{
					exit = true;
				}
			}
			
			netInput.closeStreams();
			netOutput.closeStreams();
			socket.close();
			keyboardIn.close();
			
//		}catch(SocketException s){
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	
	public static boolean login(String name, String pw){
		boolean b = true;
		
		return b;
	}
	     
}
