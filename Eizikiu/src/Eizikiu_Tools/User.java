package Eizikiu_Tools;

import java.io.*;
import java.util.*;

public class User implements Serializable{
	
	private static final long serialVersionUID = -447093848295735838L;
	
	private String name;
	private String password;
	private transient ConnectionToClient connection;
	private boolean status;
	
	public User(String name, String password){
		this.name = name;
		this.password = password;
		status = false;
		connection = new ConnectionToClient();
	}
	
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
		EZKlogger.log(name + ".logIn() -> [" + name + "] logged in");
	}
	
	public void logOut(){
		status = false;
		EZKlogger.log(name + ".logOut() -> [" + name + "] logged out");
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public ConnectionToClient getConnection(){
		return connection;
	}
	
	public void setConnection(ConnectionToClient connection){
		this.connection = connection;
	}
}
