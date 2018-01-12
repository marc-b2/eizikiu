package Eizikiu_Client;

import java.awt.EventQueue;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.swing.JOptionPane;

import Eizikiu_Tools.*;
import Eizikiu_GUI.*;

public class Eizikiu_Client {

	private static LinkedList<User> globalUserList; // local copy from servers 'globalUserList', has to be updated when changes occur on server
	private static LinkedList<Room> publicRooms; // local copy from servers 'publicRooms', has to be updated when changes occur on server
	// rooms where this client is in will be hold in 'rooms' list owned by the Clients 'user' and will be updated on every join or leave event

	private static OutputStreamSet netOutput;
	private static InputStreamSet netInput;
	private static Socket socket;
	private static User user = null;
	
	public static void main(String args[]) {

		/*	deprecated
		 * ************
		String text;
		KeyboardListener keyli;
		*/
		chat();
		
		EZKlogger.setLoglevel(1);
		
		EZKlogger.log("************Eizikiu_Client.main() -> Eizikiu_Client started ************");
		
		try {
			socket = new Socket("localhost", 1234);
			
			EZKlogger.log("Eizikiu_Client.main() -> server found");
			
			netInput = new InputStreamSet(socket);
			netOutput = new OutputStreamSet(socket);
			netInput.setupStreams();
			netOutput.setupStreams();
			
			EZKlogger.log("Eizikiu_Client.main() -> connection to server established\n\n");
			
			// start login GUI
			new LogInGUI();
			
			/*	deprecated
			 * ************
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
			*/
//		}catch(SocketException s){
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	
	// getter
	public static LinkedList<User> getGlobalUserList() {
		return globalUserList;
	}
	
	public static LinkedList<Room> getPublicRooms() {
		return publicRooms;
	}
	
	// setter
	public static void setGlobalUserList(LinkedList<User> globalUserList) {
		Eizikiu_Client.globalUserList = globalUserList;
	}
	
	public static void setPublicRooms(LinkedList<Room> publicRooms) {
		Eizikiu_Client.publicRooms = publicRooms;
	}
	
	// functions
	public static boolean login(String name, String pw, LogInGUI gui) {
		try {
			// create user
			user = new User(name,pw);
			
			// send login request
			netOutput.sendMessage(new Message("login request", name, 11, 0));
			// send user credentials
			netOutput.sendMessage(new Message(pw, name, 12, 0));
			
			// wait for answer
			Message message;
			message = netInput.receiveMessage();
			
			// react on answer
			if(message.getType() == 8) {
				return true;				
			} else if(message.getType() == 9) {
				JOptionPane.showMessageDialog(gui.getBox(), message.getMessage(), "Error:", 0);
				return false;
			} else {
				JOptionPane.showMessageDialog(gui.getBox(), "Sorry, unknown error!", "Error:", 0);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean register(String name, String pw, RegistryGUI gui) {
		try {
			// create user
			user = new User(name,pw);
			
			// send register request
			netOutput.sendMessage(new Message("register request", name, 10, 0));
			// send user credentials
			netOutput.sendMessage(new Message(pw, name, 12, 0));
			
			// wait for answer
			Message message;
			message = netInput.receiveMessage();
			
			// react on answer
			if(message.getType() == 8) {
				return true;				
			} else if(message.getType() == 9) {
				JOptionPane.showMessageDialog(gui.getPanel(), message.getMessage(), "ERROR", 0);
				return false;
			} else {
				JOptionPane.showMessageDialog(gui.getPanel(), "Sorry, unknown error!", "Error:", 0);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void chat() {
		
		try {
			// start GUI
			Eizikiu_Client_GUI gui = new Eizikiu_Client_GUI();
			EventQueue.invokeLater(gui);

			String tempString = "";
			String[] parts;
			Room room;
			LinkedList<Room> tempRoomList;
			LinkedList<User> tempUserList;
			boolean exit = false;
			while(!exit){
				Message message = netInput.receiveMessage();
				int messageType = message.getType();
				
				switch(messageType) {
				case 0:		// exit
					exit = true;
					break;
				
				case 1:		// regular public message
				case 2:		// regular private message
					gui.writeMessage(message);
					break;
				
				case 20:	// service message -> dialog box INFO
					JOptionPane.showMessageDialog(gui.getFrmEizikiuClient(), message.getMessage(), "Message from Server", 1);
					break;
				
				case 23:	// private chat ACK -> new private chat
					gui.newChat(message.getRoomID());
					break;
					
				case 25:	// join room ACK -> new room
					gui.newChat(message.getRoomID());
					break;
				
				case 9:		// general NAK
				case 24:	// private chat NAK 
				case 26:	// join room NAK 	-> dialog box ERROR
					JOptionPane.showMessageDialog(gui.getFrmEizikiuClient(), message.getMessage(), "ERROR", 0);
					break;
					
				case 27:	// receive room list
					tempRoomList = new LinkedList<>();
					tempString = message.getMessage(); // message is "roomName1§roomID1§roomName2§roomID2§....§roomNameX§roomIDX"
					parts = tempString.split("§");
					for(int i=0; i<parts.length; i+=2) {
						tempRoomList.add(new Room(parts[i], Integer.parseInt(parts[i+1])));
					}
					publicRooms = tempRoomList;
					gui.actualizeRoomJList();
					break;
					
				case 28:	// receive user list
					room = null;
					tempUserList = new LinkedList<>(); // message is "userName1§userName2§...§userNameX"
					tempString = message.getMessage();
					parts = tempString.split("§");
					for(String x : parts) {
						tempUserList.add(new User(x, "noPW"));
					}
					if(message.getRoomID() == 0) {
						globalUserList = tempUserList;
					} else {
						for(Room x : publicRooms) {
							if(x.getID() == message.getRoomID()) {
								room = x;
							}
						}
						if(room != null) {
							room.setUserList(tempUserList);
						}
					}
					// TODO: gui.actualizeUserJList(message.getRoomID());
					break;
				
				case 29:	// warning -> dialog box WARNING
					JOptionPane.showMessageDialog(gui.getFrmEizikiuClient(), message.getMessage(), "WARNING", 2);
					break;
				
				default: // error
					EZKlogger.log("chat -> received message of unexpected type: " + messageType);
				} // switch
			} // while
			
			netInput.closeStreams();
			netOutput.closeStreams();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public void privateChatRequest(String userName) {
		
	}
	
	static public void publicChatRequest(int roomID) {
		
	}
	
	static public void chatLeave(int roomID) {
		
	}
	
	static public void roomListRequest() {
		netOutput.sendMessage(new Message("room list request", user.getName(), 17, 0));
	}
	
	static public void userListRequest(int roomID) {
		netOutput.sendMessage(new Message("user list request", user.getName(), 18, roomID));
	}
	
	static public void sendMessage(String message, int roomID) {
		boolean isPublic = false;
		for(Room x : publicRooms) {
			if(x.getID() == roomID) {
				isPublic = true;
			}
		}
		if(isPublic) {
			netOutput.sendMessage(new Message(message, user.getName(), 1, roomID));
		} else {
			netOutput.sendMessage(new Message(message, user.getName(), 2, roomID));
		}
	}
	
	static public void shutdown() {
		
	}
}

