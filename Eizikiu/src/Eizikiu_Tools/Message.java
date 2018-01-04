package Eizikiu_Tools;

import java.util.Date;
import java.io.*;
public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String message;
	private String senderName;
	private int type; // 0-exit, 1-standard chat, 2-server, 2- benutzername, 3- passwort
	
	// Konstruktor
	public Message(String message, String senderName) {
		
		this.message = message;
		this.senderName = senderName;
		this.type = 1;
		date = new java.util.Date();
	}
	
	public Message(String message, String senderName, int type) {
		
		this.message = message;
		this.senderName = senderName;
		this.type = type;
		date = new java.util.Date();
	}
	
	// Getter
	public String getMessage(){
		return message;
	}
	
	public String getSenderName(){
		return senderName;
	}
	
	public int getType(){
		return type;
	}
	
	// Methoden
	@Override
	public String toString(){
		
		String output = date.toString() + " [" + senderName + "]: " + message; 
		return output;
	}
	
	public void printOwn(){
		EZKlogger.info(date.toString() + " [YOU]: " + message);
	}

}