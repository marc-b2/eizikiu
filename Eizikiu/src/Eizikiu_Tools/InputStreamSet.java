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
	public void setupStreams() throws Exception{
		EZKlogger.debug();
		
		in = socket.getInputStream();
		oin = new ObjectInputStream(in);

		EZKlogger.debug("InputStreamSet.setupStreams() -> inbound streams set up");
	}
	
	public Message receiveMessage() throws Exception{
		Message message = new Message("","",0);
		message = (Message) oin.readObject();
		EZKlogger.debug(message.toString());
		return message;
	}

	public void closeStreams() throws Exception{
		EZKlogger.debug();
		
		oin.close();
		in.close();
	}
}
