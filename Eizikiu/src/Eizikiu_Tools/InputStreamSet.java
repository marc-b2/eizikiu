package Eizikiu_Tools;

import java.io.*;
import java.net.*;

public class InputStreamSet {
	
	Socket socket;
	InputStream in;
	ObjectInputStream oin;
	
	// constructor
	public InputStreamSet(Socket socket){
		EZKlogger.debug();
		this.socket = socket;
	}
	
	// functions
	public void setupStreams(){
		EZKlogger.debug();
		try {
			in = socket.getInputStream();
			oin = new ObjectInputStream(in);

			EZKlogger.debug("InputStreamSet.setupStreams() -> inbound streams set up");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Message receiveMessage() throws EOFException{
		EZKlogger.debug();
		Message message = new Message("","",0);
		try {
			message = (Message) oin.readObject();
		} catch (EOFException e) {
			throw e;
		} catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}

	public void closeStreams(){
		EZKlogger.debug();
		try {
			oin.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
