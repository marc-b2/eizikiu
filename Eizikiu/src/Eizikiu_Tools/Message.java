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
	 * 		unidirectional				client -> server				server -> client
	 * 		--------------				----------------				----------------
	 * 0 -	exit				10 -	register request		20 -	service message
	 * 1 -	standard public		11 -	login request			21 -	
	 * 2 -						12 -	user credentials		22 -	
	 * 3 -						13 -	join priv chat request	23 -	priv chat ACK
	 * 4 -						14 -	leave priv chat notif.	24 -	priv chat NAK
	 * 5 -						15 -	join room request		25 -	join room ACK
	 * 6 -						16 -	leave room notification	26 -	join room NAK
	 * 7 -						17 -	room list request		27 -	room list	
	 * 8 -	ACK					18 -	user list request		28 -	user list
	 * 9 -	NAK					19 -							29 -	warning
	 */
	
	// constructor
	public Message(String message, String senderName) { // deprecated
		
		this.message = message;
		this.senderName = senderName;
		this.type = 1;
		date = new java.util.Date();
	}
	
	public Message(String message, String senderName, int roomID) {
		
		this.message = message;
		this.senderName = senderName;
		this.type = 1;
		this.roomID = roomID;
		date = new java.util.Date();
	}
	
	public Message(String message, String senderName, int type, int roomID) {
		
		this.message = message;
		this.senderName = senderName;
		this.type = type;
		this.roomID = roomID;
		date = new java.util.Date();
	}
	
	// getter
	public String getMessage() {
		return message;
	}
	
	public String getSenderName() {
		return senderName;
	}
	
	public int getType() {
		return type;
	}
	
	public int getRoomID() {
		return roomID;
	}
	
	// functions
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