package Eizikiu_Tools;

import java.io.Serializable;
import java.util.LinkedList;

import Eizikiu_Server.Eizikiu_Server;

public class Room implements Serializable{

	private static final long serialVersionUID = 636461425992742159L;
	
	private static LinkedList<Integer> IDList = new LinkedList<>();
	
	private String name;
	private int ID;
	private transient LinkedList<User> userList;
	
	// constructor
	public Room(String name) {
		EZKlogger.debug();
		this.name = name;
		userList = new LinkedList<User>();
		if(name.equals("default")) {
			ID = 1;
		} else {
			ID = 0;
			Integer i = 2; // 0 - reserved for no/all rooms; 1 - reserved for default room
			while(ID == 0) { // get first unused ID
				if(!IDList.contains(i)) ID = i;
				i++;
			}
			IDList.add(ID);
		}
	}
	
	public Room(String name, int ID) { // for user's room list on client side
		EZKlogger.debug();
		this.name = name;
		this.ID = ID;
		userList = null;
	}
		
	// getter
	public String getName() {
		EZKlogger.debug();
		return name;
	}
	
	public LinkedList<User> getUserList() {
		EZKlogger.debug();
		return userList;
	}
	
	public int getID() {
		EZKlogger.debug();
		return ID;
	}
	
	public static LinkedList<Integer> getIDList() {
		return IDList;
	}
	
	// setter
	public void setName(String name) {
		EZKlogger.debug();
		this.name = name;
	}
	
	public void setUserList(LinkedList<User> userList){
		EZKlogger.debug();
		this.userList = userList;
	}
	
	public void setID(int ID) {
		EZKlogger.debug();
		this.ID = ID;
	}
	
	// functions
	/**
	 * adds 'user' to public rooms user list and sends "user joined" to other members 
	 * @param user
	 * @return true: if 'user' added successfully<br>false: if room is private or 'user' already member
	 */
	public boolean addToPublicRoom(User user) {
		EZKlogger.debug();
		if(Eizikiu_Server.getPrivateRooms().contains(this)) {
			EZKlogger.debug(this.toString() + ": ERROR: this is a private room!");
			return false;
		}
		if(userList.add(user)) {
			for(User x: userList) {
				if(!x.equals(user)) {
					try {
						x.getConnection().getNetOutput().sendMessage(new Message("[" + user.getName() + "] joined this room", "Server---------->", 1, ID));
					} catch (Exception e) {
						EZKlogger.debug(this.toString() + ": ERROR: cannot send 'user joined' to user [" + x.getName() +"]");
						e.printStackTrace();
					}
				}
			}
			return true;
		} else {
			EZKlogger.debug(this.toString() + ": ERROR: user already in user list");
			return false;
		}
	}
	
	/**
	 * removes 'user' from public rooms user list and sends "user left" to other members 
	 * @param user
	 * @return true: if 'user' removed successfully<br>false: if room is private or 'user' not member
	 */
	public boolean removeFromPublicRoom(User user) {
		EZKlogger.debug();
		if(Eizikiu_Server.getPrivateRooms().contains(this)) {
			EZKlogger.debug(this.toString() + ": ERROR: this is a private room!");
			return false;
		}
		if(userList.remove(user)) {
			for(User x: userList) {
				try {
					x.getConnection().getNetOutput().sendMessage(new Message("[" + user.getName() + "] left this room", "Server---------->", 1, ID));
				} catch (Exception e) {
					EZKlogger.debug(this.toString() + ": ERROR: cannot send 'user left' to user [" + x.getName() +"]");
					e.printStackTrace();
				}
			}
			return true;
		} else {
			EZKlogger.debug(this.toString() + ": ERROR: user [" + user.getName() + "] not in list");
			return false;			
		}
	}
	
	public void sendUserListToMembers() {
		
	}
	
	public boolean hasUsers(User user1, User user2) { // for private chats only; tells if a room with the two handed over users already exists
		EZKlogger.debug();
		boolean one = false;
		boolean two = false;
		for(User x : userList) {
			if(user1 == x) one = true;
			if(user2 == x) two = true;
		}
		if(one&two) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		EZKlogger.debug();
		return "ID: " + ID + " name: " + name;
	}
}

