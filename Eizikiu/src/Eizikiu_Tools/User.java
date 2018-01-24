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
		EZKlogger.log(banned ? "user [" + name + "] was banned from Server" : "user [" + name + "] was unbanned from Server");
	}
	
	// functions
	@Override
	public String toString() {
		EZKlogger.debug();
		return name;
	}
	
	public String everythingToString() {
		EZKlogger.debug();
		return "name: " + name + "  password: " + password + "  logged in: " + status + "  is banned: " + banned;
	}
	
	/**
	 * adds 'room' to users room list
	 * @param room
	 * @return true: if successful<br>false: otherwise
	 */
	public boolean addTo(Room room){
		EZKlogger.debug();
		if(rooms.add(room)){
			return true;
		} else {
			EZKlogger.debug(this.toString() + ": ERROR: could not add " + room.toString() + " to room list");
			return false;
		}
	}
	
	/**
	 * removes 'room' from users room list
	 * @param room
	 * @return true: if successful<br>false: otherwise
	 */
	public boolean removeFrom(Room room){
		EZKlogger.debug();
		if(rooms.remove(room)){
			return true;
		} else {
			EZKlogger.debug(this.toString() + ": ERROR: could not remove " + room.toString() + " from room list");
			return false;
		}
	}
	
	/**
	 * sets status = true and creates users room list
	 */
	public void logIn(){ // server only
		EZKlogger.debug();
		status = true;
		rooms = new LinkedList<>();
	}
	
	/**
	 * sets status = false and users room list = null;
	 * removes user from all rooms
	 */
	public void logOut(){ // server only
		EZKlogger.debug();
		status = false;
		rooms = null;
	}
	
	/**
	 * 
	 */
	public void closeAllPrivateChats() {
		Room room = null;
		do	{
			room = null;
			if(!Eizikiu_Server.getPrivateRooms().isEmpty()) {
				for(Room x : Eizikiu_Server.getPrivateRooms()) {
					if(x.getUserList().contains(this)) {
						room = x;
						break;
					}
				}
			} else {
				EZKlogger.debug(name + ": ERROR: The list of private chats is empty!");
			}
			
			if(room != null) {
				for(User x : room.getUserList()) {
					if(!x.equals(this)) {
						try {
							x.getConnection().getNetOutput().sendMessage(new Message("[" + name + "] has left your private chat. You may close this Window now.", "Server---------->", 2, room.getID()));
						} catch (Exception e) {
							EZKlogger.debug(name + ": ERROR: could not send message to user [" + x.name + "] of private chat '" + room.getName() + "'");
							e.printStackTrace();
						}
					}
					x.getRooms().remove(room);
				}
				if(Eizikiu_Server.getPrivateRooms().remove(room)) {
					EZKlogger.log("The room " + room.toString() + "got deleted.");
				} else {
					EZKlogger.debug(": ERROR: the room " + room.toString() + " could not be removed from public room list!");
				}
				Integer i = room.getID();
				if(Room.getIDList().remove(i)) {
					EZKlogger.log("The ID " + i + " got removed from ID list.");
				} else {
					EZKlogger.debug(": ERROR: the ID of room " + room.toString() + " was not in the ID list!");
				}
			}
		} while(room != null);
	}
}
