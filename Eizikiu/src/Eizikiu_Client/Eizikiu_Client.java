package Eizikiu_Client;

import java.awt.EventQueue;
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
	private static boolean wait = true;
	
	public static void main(String args[]) {
		
		EZKlogger.setLoglevel(2);
		EZKlogger.setLogfile("eizikiu_client.log");
		EZKlogger.setFileOutput(true);
		EZKlogger.debug();
		
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
			while(wait) {};
			EZKlogger.debug();
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	
	// getter
	public static LinkedList<User> getGlobalUserList() {
		EZKlogger.debug();
		return globalUserList;
	}
	
	public static LinkedList<Room> getPublicRooms() {
		EZKlogger.debug();
		return publicRooms;
	}
	
	// setter
	public static void setGlobalUserList(LinkedList<User> globalUserList) {
		EZKlogger.debug();
		Eizikiu_Client.globalUserList = globalUserList;
	}
	
	public static void setPublicRooms(LinkedList<Room> publicRooms) {
		EZKlogger.debug();
		Eizikiu_Client.publicRooms = publicRooms;
	}
	
	// functions
	public static boolean login(String name, String pw, LogInGUI gui) {
		EZKlogger.debug();
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
	
	public static boolean register(String name, String pw, Registry_GUI gui) {
		EZKlogger.debug();
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
				JOptionPane.showMessageDialog(gui.getFrame(), message.getMessage(), "ERROR", 0);
				return false;
			} else {
				JOptionPane.showMessageDialog(gui.getFrame(), "Sorry, unknown error!", "Error:", 0);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void chat() {
		EZKlogger.debug();
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
					// Message('successful opened' from server, room name, 23, roomID)
					user.getRooms().add(new Room(message.getSenderName(), message.getRoomID()));
					gui.newChat(message.getRoomID()/*, message.getMessage()*/); // TODO: erste message mit übergeben zum Ausgeben; Raum statt ID übergeben!!!!!!!!!!!!!
					break;
					
				case 25:	// join room ACK -> new room
					// Message('successful opened' from server, senderName, 25, roomID)
					for(Room x : publicRooms) {
						if(x.getID() == message.getRoomID()) user.getRooms().add(x);
					}
					gui.newChat(message.getRoomID()/*, message.getMessage()*/); // TODO: erste message mit übergeben zum Ausgeben; Raum statt ID übergeben!!!!!!!!!!!!!
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
	
	public static void privateChatRequest(String userName) throws Exception {
		EZKlogger.debug();
		// Message(name of requested chat partner, senderName, 13, 0)
		netOutput.sendMessage(new Message(userName, user.getName(), 13, 0));
	}
	
	public static void publicChatRequest(int roomID) throws Exception{
		EZKlogger.debug();
		String roomName = "";
		for(Room x : publicRooms) {
			if(x.getID() == roomID) roomName = x.getName();
		}
		// Message(room name, senderName, 15, roomID)
		netOutput.sendMessage(new Message(roomName, user.getName(), 15, roomID));
	}
	
	public static void chatLeave(int roomID) throws Exception{
		EZKlogger.debug();
		boolean isPublic = false;
		for(Room x : publicRooms) {
			if(x.getID() == roomID) {
				isPublic = true;
			}
		}
		
		Room room = null;
		for(Room x : user.getRooms()) {
			if(x.getID() == roomID) room = x;
		}
		if(room != null) {
			// Message(room name, senderName, 14(private)/16(public), roomID)
			netOutput.sendMessage(new Message(room.getName(), user.getName(), isPublic ? 16 : 14, roomID));
			user.getRooms().remove(room);
		}
	}
	
	public static void roomListRequest() throws Exception{
		EZKlogger.debug();
		netOutput.sendMessage(new Message("room list request", user.getName(), 17, 0));
	}
	
	public static void userListRequest(int roomID) throws Exception{
		EZKlogger.debug();
		netOutput.sendMessage(new Message("user list request", user.getName(), 18, roomID));
	}
	
	public static void sendMessage(String message, int roomID) throws Exception{
		EZKlogger.debug();
		boolean isPublic = false;
		for(Room x : publicRooms) {
			if(x.getID() == roomID) {
				isPublic = true;
			}
		}
		netOutput.sendMessage(new Message(message, user.getName(), isPublic ? 1 : 2, roomID));
	}
	
	public static void shutdown() throws Exception{
		EZKlogger.debug();
		netOutput.sendMessage(new Message("exit", user.getName(), 0, 0));
	}
}

