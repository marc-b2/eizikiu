package Eizikiu_Tools;

import java.io.*;
import java.util.*;

import Eizikiu_Server.Eizikiu_Server;

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
	
	public void logIn(){ // server only
		EZKlogger.debug();
		status = true;
		rooms = new LinkedList<>();
		for(Room x : Eizikiu_Server.getPublicRooms()) { // add user to default room and send new room list to all members
			if(x.getID() == 1) {
				if(x.addUser(this)) Eizikiu_Server.sendUserListToAllMembersOf(x);
				break;
			}
		}
		EZKlogger.log(name + ".logIn() -> [" + name + "] logged in");
	}
	
	public void logOut(){ // server only
		EZKlogger.debug();
		status = false;
		rooms = null;
		// remove user from all rooms; send 'user left' message and new user list to all members
		for(Room x : Eizikiu_Server.getPublicRooms()) {
			if(x.getUserList().contains(this)) {
				if(x.getUserList().remove(this)) {
					for(User y : x.getUserList()) {
						try {
							y.getConnection().getNetOutput().sendMessage(new Message("[" + name + "] left this room", "Server---------->", 1, x.getID()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Eizikiu_Server.sendUserListToAllMembersOf(x);
				}
			}
		}
		
		Room room = null;
		do	{
			room = null;
			for(Room x : Eizikiu_Server.getPrivateRooms()) {
				if(x.getUserList().contains(this)) {
					room = x;
					break;
				}
			}
			
			if(room != null) {
				for(User x : room.getUserList()) {
					if(!x.equals(this)) {
						try {
							x.getConnection().getNetOutput().sendMessage(new Message("[" + name + "] has left your private chat. You may close this Window now.", "Server---------->", 2, room.getID()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						x.getRooms().remove(room);
					}
				}
				Eizikiu_Server.getPrivateRooms().remove(room);
			}
		} while(room != null);
		
		EZKlogger.log(name + ".logOut() -> [" + name + "] logged out");
	}
}
