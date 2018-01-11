package Eizikiu_Tools;

import java.io.*;
import java.util.*;

public class User implements Serializable{
	
	private static final long serialVersionUID = -447093848295735838L;
	
	private String name;
	private String password;
	private transient ConnectionToClient connection;
	private transient LinkedList<Room> rooms;
	private transient boolean status;
	private boolean banned;
	
	// constructor
	public User(String name, String password){
		this.name = name;
		this.password = password;
		connection = new ConnectionToClient();
		rooms = new LinkedList<>();
		status = false;
		banned = false;
	}
	
	// getter
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public ConnectionToClient getConnection() {
		return connection;
	}
	
	public LinkedList<Room> getRooms() {
		return rooms;
	}
	
	public boolean isStatus() {
		return status;
	}

	public boolean isBanned() {
		return banned;
	}
	
	// setter
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setConnection(ConnectionToClient connection){
		this.connection = connection;
	}
	
	public void setRooms(LinkedList<Room> rooms) {
		this.rooms = rooms;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
	
	// functions
	public void addTo(LinkedList<User> userList){
		if(userList.add(this)){
			EZKlogger.log(name + ".addTo() -> new user [" + name + "] added");
		}
	}

	public void removeFrom(LinkedList<User> userList){
		if(userList.remove(this)){
			EZKlogger.log(name + ".removeFrom() -> user [" + name + "] deleted");
		}
	}
	
	public void logIn(){
		status = true;
		rooms = new LinkedList<>();
		EZKlogger.log(name + ".logIn() -> [" + name + "] logged in");
	}
	
	public void logOut(){
		status = false;
		rooms = null;
		EZKlogger.log(name + ".logOut() -> [" + name + "] logged out");
	}
}
