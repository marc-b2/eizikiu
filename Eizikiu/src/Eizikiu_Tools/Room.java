package Eizikiu_Tools;

import java.util.LinkedList;

// Kommentar
public class Room {

	LinkedList<User> userList;
	
	// Konstruktor
	Room(){
		userList = new LinkedList<User>();
	}
	
	// Getter
	public LinkedList<User> getUserList(){
		return userList;
	}
	
	// Setter
	public void setUserList(LinkedList<User> userList){
		this.userList = userList;
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

