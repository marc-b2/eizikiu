package Eizikiu_Tools;

import java.util.Date;
import java.io.*;
public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String message;
	private String senderName;
	private int type;
	private int roomID;
	
	/*	message types:
	 * ###############
	 * 
	 * 		unidirectional			client -> server			server -> client
	 * 		--------------			----------------			----------------
	 * 0 -	exit			10 -	register request	20 -	warning
	 * 1 -	standard		11 -	login request		21 -	
	 * 2 -					12 -	user credentials	22 -	
	 * 3 -					13 -						23 -	
	 * 4 -					14 -						24 -	
	 * 5 -					15 -						25 -	
	 * 6 -					16 -						26 -	
	 * 7 -					17 -						27 -	
	 * 8 -	positive ACK	18 -						28 -	
	 * 9 -	negative ACK	19 -						29 -	
	 */
	
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
		String output = "Error in Message.toString()";
		if (type == 1) {output = date.toString() + " [" + senderName + "]: " + message;}
		else {output = date.toString() + " TYPE = " + type + " [" + senderName + "]: " + message;}
		return output;
	}
	
	public void printOwn(){
		EZKlogger.info(date.toString() + " [YOU]: " + message);
	}

}