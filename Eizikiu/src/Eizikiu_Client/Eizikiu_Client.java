package Eizikiu_Client;

import java.net.*;
import java.util.*;
import Eizikiu_Tools.*;

public class Eizikiu_Client {

	public static void main(String args[]) {
	
		User user = null;
		String text;
		KeyboardListener keyli;
		EZKlogger output = new EZKlogger();
		EZKlogger.setLoglevel(0);
		OutputStreamSet netOutput;
		InputStreamSet netInput;
		
		try {
			Socket socket = new Socket("localhost", 1234);
			
			System.out.println("Eizikiu_Client.main() -> server found");
			
			netInput = new InputStreamSet(socket);
			netOutput = new OutputStreamSet(socket);
			netInput.setupStreams();
			netOutput.setupStreams();
			
			System.out.println("Eizikiu_Client.main() -> connection to server established\n\n");
						
			// password check
			Scanner keyboardIn = new Scanner(System.in);
			boolean login = false;
			while (!login) {
				user = new User(null, null);
				// get user name and password
				output.info("Type in user name: ");
				text = keyboardIn.nextLine();
				output.info("\n");
				user.setName(text);
//				netOutput.sendString(text);
				
				output.info("Type in user password: ");
				text = keyboardIn.nextLine();
				output.info("\n");
				user.setPassword(text);
//				netOutput.sendString(text);
				
				output.debug("user: " + user.getName() + " ---- password: " + user.getPassword() + ".");
				
				// send to server
				netOutput.sendUser(user);
				
				// wait for server
				Message answer = netInput.receiveMessage();
				if (answer.getMessage().equals("userValid") && answer.getSenderName().equals("Server")) {
					login = true;
				}else{
					output.info("\n\nThe password is wrong or the user name you entered is already in use.");
					output.info("Please try again!\n\n");
				}
			}

			output.info("Eizikiu_Client.main() -> You successfully logged in to the server!");
			
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
						output.info(message.toString());
					}else{
						message.printOwn(output);
					}
				}else{
					exit = true;
				}
			}
			
//			conThread.interrupt();
			netInput.closeStreams();
			netOutput.closeStreams();
			socket.close();
			keyboardIn.close();
			
//		}catch(SocketException s){
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	     
}
