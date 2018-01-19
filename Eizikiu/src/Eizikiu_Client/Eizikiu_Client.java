package Eizikiu_Client;

import java.awt.EventQueue;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;

import Eizikiu_Tools.*;
import Eizikiu_GUI.*;

public class Eizikiu_Client {

	private static LinkedList<User> globalUserList = null; // local copy from servers 'globalUserList', has to be updated when changes occur on server
	private static LinkedList<Room> publicRooms = null; // local copy from servers 'publicRooms', has to be updated when changes occur on server
	// rooms where this client is in will be hold in 'rooms' list owned by the Clients 'user' and will be updated on every join or leave event

	private static OutputStreamSet netOutput;
	private static InputStreamSet netInput;
	private static Socket socket;
	private static User user = null;
	private static String address = "localhost";
	public final static CountDownLatch latch = new CountDownLatch(1);
	
	public static void main(String args[]) {
		
		try {
			address = args[0];
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		EZKlogger.setLoglevel(3);
		EZKlogger.setLogfile("eizikiu_client.log");
		EZKlogger.setFileOutput(true);
		EZKlogger.debug();
		
		EZKlogger.log("************Eizikiu_Client.main() -> Eizikiu_Client started ************");
		EZKlogger.log("server address set to '" + address + "'");
		
		try {
			socket = new Socket(address, 1234);
			
			EZKlogger.log("Eizikiu_Client.main() -> server found");
			
			netInput = new InputStreamSet(socket);
			netOutput = new OutputStreamSet(socket);
			netInput.setupStreams();
			netOutput.setupStreams();
			
			EZKlogger.log("Eizikiu_Client.main() -> connection to server established\n\n");
			
			// start login GUI
			new LogInGUI();
			latch.await();
			EZKlogger.debug("returned from LogIn/Registry GUI");
			
			// get globalUserList and pubicRooms
			
			do { // because user is already logged in and can receive messages from default room
				EZKlogger.debug("do-while-loop get user and room list");
				Message message = netInput.receiveMessage();
				int messageType = message.getType();
				if(messageType == 27) { // room list from server
					publicRooms = new LinkedList<>();
					String tempString = message.getMessage(); // message is "roomName1ï¿½roomID1ï¿½roomName2ï¿½roomID2ï¿½....ï¿½roomNameXï¿½roomIDX"
					String[] parts = tempString.split("§");
					for(int i=0; i<parts.length; i+=2) {
						publicRooms.add(new Room(parts[i], Integer.parseInt(parts[i+1])));
					}
				}
				if(messageType == 28) { // user list from server
					globalUserList = new LinkedList<>(); // message is "userName1ï¿½userName2ï¿½...ï¿½userNameX"
					String tempString = message.getMessage();
					String[] parts = tempString.split("§");
					for(String x : parts) {
						globalUserList.add(new User(x, "noPW"));
					}
				}
			} while(publicRooms == null || globalUserList == null);
			
			// start GUI
			Eizikiu_Client_GUI gui = new Eizikiu_Client_GUI();
			EventQueue.invokeLater(gui);
			gui.getFrmEizikiuClient().setTitle("Eizikiu  " + user.getName());
			// start chat
			chat(gui);
		} catch(Exception e) {
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
	
	public static User getUser() {
		return user;
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
				latch.countDown();
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
				latch.countDown();
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
	
	public static void chat(Eizikiu_Client_GUI gui) {
		EZKlogger.debug();
		try {
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
					// Message(name of chat partner, room name, 23, roomID)
					user.getRooms().add(new Room(message.getSenderName(), message.getRoomID()));
					gui.newChat(message.getMessage(), message.getRoomID());
					break;
					
				case 25:	// join room ACK -> new room
					// Message('successful opened' from server, senderName, 25, roomID)
					for(Room x : publicRooms) {
						EZKlogger.debug("x.roomID = " + x.getID() + "; message.roomID = " + message.getRoomID());
						if(x.getID() == message.getRoomID()) {
							EZKlogger.debug("x.roomID == message.roomID");
							if(user.getRooms().add(x)) {
								EZKlogger.debug("the following room was added to users room list:");
								EZKlogger.debug(x.toString());
							}
							break;
						}
					}
					gui.newChat(message.getRoomID());
					break;
				
				case 9:		// general NAK
				case 24:	// private chat NAK 
				case 26:	// join room NAK 	-> dialog box ERROR
					JOptionPane.showMessageDialog(gui.getFrmEizikiuClient(), message.getMessage(), "ERROR", 0);
					break;
					
				case 27:	// receive room list
					EZKlogger.debug("room list received");
					tempRoomList = new LinkedList<>();
					tempString = message.getMessage(); // message is "roomName1ï¿½roomID1ï¿½roomName2ï¿½roomID2ï¿½....ï¿½roomNameXï¿½roomIDX"
					parts = tempString.split("ï¿½");
					for(int i=0; i<parts.length; i+=2) {
						tempRoomList.add(new Room(parts[i], Integer.parseInt(parts[i+1])));
					}
					publicRooms = tempRoomList;
					gui.actualizeRoomJList();
					break;
					
				case 28:	// receive user list
					EZKlogger.debug("user list received");
					room = null;
					tempUserList = new LinkedList<>();
					tempString = message.getMessage(); // message is "userName1ï¿½userName2ï¿½...ï¿½userNameX"
					parts = tempString.split("ï¿½");
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
					if(message.getRoomID() == 0) gui.actualizeUserJList();
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
		if(!userName.equals(user.getName())) {
			netOutput.sendMessage(new Message(userName, user.getName(), 13, 0));			
		}
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
		EZKlogger.debug("room is public: " + isPublic);
		
		Room room = null;
		for(Room x : user.getRooms()) {
			EZKlogger.debug("for each rooms of user");
			if(roomID != 1) {
				if(x.getID() == roomID) {
					room = x;
					EZKlogger.debug("if roomID == roomIDï¿½bergeben");
				}
			}
		}
		if(room != null) {
			// Message(room name, senderName, 14(private)/16(public), roomID)
			netOutput.sendMessage(new Message(room.getName(), user.getName(), isPublic ? 16 : 14, roomID));
			if(user.getRooms().remove(room)) {
				EZKlogger.debug("removed the following room from users room list:");
				EZKlogger.debug(room.toString());
			}
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

