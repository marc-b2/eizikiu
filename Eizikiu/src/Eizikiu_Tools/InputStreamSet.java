package Eizikiu_Tools;

import java.io.*;
import java.net.*;

public class InputStreamSet {
	
	Socket socket;
	InputStream in;
	ObjectInputStream oin;
	
	// constructor
	public InputStreamSet(Socket socket){
		this.socket = socket;
	}
	
	// functions
	public void setupStreams(){
		try {
			in = socket.getInputStream();
			oin = new ObjectInputStream(in);

			EZKlogger.debug("InputStreamSet.setupStreams() -> inbound streams set up");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//	public Object receive(){
//		Object object = new Object();
//		try {
//			object = oin.readObject();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return object;
//	}
	
	public Message receiveMessage() throws EOFException{
		Message message = new Message("","");
		try {
			message = (Message) oin.readObject();
		} catch (EOFException e) {
			throw e;
		} catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}
	
	public User receiveUser(){
		User user = new User("", "");
		try {
			user = (User) oin.readObject();
			
			EZKlogger.debug("InputStreamSet.receiveUser() -> " + user.getName() + " ---- " + user.getPassword());
		}catch(Exception e){
			e.printStackTrace();
		}
		return user;
	}
	
//	public String receiveString(){
//		String string = new String();
//		try {
//			string = (String) oin.readObject();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return string;
//	}
	
//	public boolean isAvailable(){
//		try{
////			System.out.println("InputStreamSet.isAvailable() -> " + oin.available());
////			if(oin.available() == 1){
//				if(oin.readByte() == 42){
//					return true;
////				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return false;
//	}

	public void closeStreams(){
		try {
			oin.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
