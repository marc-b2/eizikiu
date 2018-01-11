package Eizikiu_Tools;

import java.io.Serializable;
import java.util.LinkedList;

// Kommentar
public class Room implements Serializable{

	private static final long serialVersionUID = 636461425992742159L;
	private static int IDcount = 0;
	
	private int ID;
	private LinkedList<User> userList;
	
	// Konstruktor
	Room() {
		ID = IDcount;
		IDcount++;
		userList = new LinkedList<User>();
	}
	
	// Getter
	public LinkedList<User> getUserList() {
		return userList;
	}
	
	public int getID() {
		return ID;
	}
	
	// Setter
	public void setUserList(LinkedList<User> userList){
		this.userList = userList;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	// Methoden
	public boolean addUser(User user){
		return userList.add(user);
	}
	
	public boolean removeUser(User user){
		return userList.remove(user);
	}
	
	public boolean removeUser(String name){
		User user = null;
		for(User x : userList){
			if(x.getName().equals(name)) {user = x;}
		}
		if(!(user==null)){
			return userList.remove(user);
		}else{
			return false;
		}
	}
}

