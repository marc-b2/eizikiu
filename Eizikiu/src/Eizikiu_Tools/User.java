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
		EZKlogger.debug();
		this.name = name;
		this.password = password;
		connection = new ConnectionToClient();
		rooms = new LinkedList<>();
		status = false;
		banned = false;
	}
	
	// getter
	public String getName() {
		EZKlogger.debug();
		return name;
	}
	
	public String getPassword() {
		EZKlogger.debug();
		return password;
	}
	
	public ConnectionToClient getConnection() {
		EZKlogger.debug();
		return connection;
	}
	
	public LinkedList<Room> getRooms() {
		EZKlogger.debug();
		return rooms;
	}
	
	public boolean isStatus() {
		EZKlogger.debug();
		return status;
	}

	public boolean isBanned() {
		EZKlogger.debug();
		return banned;
	}
	
	// setter
	public void setName(String name) {
		EZKlogger.debug();
		this.name = name;
	}
	
	public void setPassword(String password) {
		EZKlogger.debug();
		this.password = password;
	}

	public void setConnection(ConnectionToClient connection){
		EZKlogger.debug();
		this.connection = connection;
	}
	
	public void setRooms(LinkedList<Room> rooms) {
		EZKlogger.debug();
		this.rooms = rooms;
	}
	
	public void setStatus(boolean status) {
		EZKlogger.debug();
		this.status = status;
	}
	
	public void setBanned(boolean banned) {
		EZKlogger.debug();
		this.banned = banned;
	}
	
	// functions
	@Override
	public String toString() {
		EZKlogger.debug();
		return "name: " + name + "  password: " + password + "  logged in: " + status + "  is banned: " + banned;
	}
	
	public void addTo(LinkedList<User> userList){
		EZKlogger.debug();
		if(userList.add(this)){
			EZKlogger.log(name + ".addTo() -> new user [" + name + "] added");
		}
	}

	public void removeFrom(LinkedList<User> userList){
		EZKlogger.debug();
		if(userList.remove(this)){
			EZKlogger.log(name + ".removeFrom() -> user [" + name + "] deleted");
		}
	}
	
	public void logIn(){
		EZKlogger.debug();
		status = true;
		rooms = new LinkedList<>();
		EZKlogger.log(name + ".logIn() -> [" + name + "] logged in");
	}
	
	public void logOut(){
		EZKlogger.debug();
		status = false;
		rooms = null;
		EZKlogger.log(name + ".logOut() -> [" + name + "] logged out");
	}
}
