package Eizikiu_Tools;

import java.util.Date;
import java.io.*;
public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String message;
	//private User sender;
	private String senderName;
	//***private int type (0- normal, 1- exit, 2- benutzername, 3- passwort...)
	
	public Message(String message, String senderName) {
		
		this.message = message;
		this.senderName = senderName;
		date = new java.util.Date();
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getSenderName(){
		return senderName;
	}
	
	@Override
	public String toString(){
		
		String output = date.toString() + " [" + senderName + "]: " + message; 
		return output;
	}
	
	public void printOwn(){
		System.out.println(date.toString() + " [YOU]: " + message);
	}

}