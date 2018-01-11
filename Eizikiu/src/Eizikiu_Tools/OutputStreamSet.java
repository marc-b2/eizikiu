package Eizikiu_Tools;

import java.io.*;
import java.net.*;

public class OutputStreamSet {
	
	Socket socket;
	OutputStream out;
	ObjectOutputStream oout;
	
	// constructor
	public OutputStreamSet(Socket socket){
		this.socket = socket;
	}
	
	// functions
	public void setupStreams(){
		try {
			out = socket.getOutputStream();
			oout = new ObjectOutputStream(out);

			EZKlogger.debug("OutputStreamSet.setupStreams() -> outbound streams set up");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

//	public void send(Object object){
//		try{
//			oout.writeObject(object);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}

	public void sendMessage(Message message){
		try{
			oout.writeObject(message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void sendUser(User user){
		try{
			EZKlogger.debug("OutputStreamSet.sendUser() -> " + user.getName() + " ---- " + user.getPassword());
			oout.writeObject(user);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	public void sendString(String string){
//		try{
//			oout.writeObject(string);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
//	public void sendAvailableByte(){
//		try{
//			oout.writeByte(42);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public void closeStreams(){
		try {
			oout.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
