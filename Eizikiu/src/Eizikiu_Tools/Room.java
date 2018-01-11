package Eizikiu_Tools;

import java.io.Serializable;
import java.util.LinkedList;

public class Room implements Serializable{

	private static final long serialVersionUID = 636461425992742159L;
	
	private static int IDcount = 2; // 0 - reserved for no/all rooms; 1 - reserved for default room
	
	private String name;
	private int ID;
	private transient LinkedList<User> userList;
	
	// constructor
	public Room(String name) {
		this.name = name;
		userList = new LinkedList<User>();
		if(name.equals("default")) {
			ID = 1;
		} else {
			ID = IDcount;
			IDcount++;
		}
	}
	
	public Room(String name, int ID) { // for user's room list on client side
		this.name = name;
		this.ID = ID;
		userList = null;
	}
		
	// getter
	public String getName() {
		return name;
	}
	
	public LinkedList<User> getUserList() {
		return userList;
	}
	
	public int getID() {
		return ID;
	}
	
	// setter
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUserList(LinkedList<User> userList){
		this.userList = userList;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	// functions
	public boolean addUser(User user) {
		return userList.add(user);
	}
	
	public boolean removeUser(User user) {
		return userList.remove(user);
	}
	
	public boolean removeUser(String name) {
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
}

