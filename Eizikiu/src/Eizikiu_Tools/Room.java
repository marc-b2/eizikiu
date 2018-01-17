package Eizikiu_Tools;

import java.io.Serializable;
import java.util.LinkedList;

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
	public boolean addUser(User user) {
		EZKlogger.debug();
		return userList.add(user);
	}
	
	public boolean removeUser(User user) {
		EZKlogger.debug();
		return userList.remove(user);
	}
	
	public boolean removeUser(String name) {
		EZKlogger.debug();
		User user = null;
		for(User x : userList) {
			if(x.getName().equals(name)) {user = x;}
		}
		if(!(user==null)) {
			return userList.remove(user);
		} else {
			return false;
		}
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

