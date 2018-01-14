package Eizikiu_Tools;

import java.io.*;
import java.net.*;

public class OutputStreamSet {
	
	Socket socket;
	OutputStream out;
	ObjectOutputStream oout;
	
	// constructor
	public OutputStreamSet(Socket socket){
		EZKlogger.debug();
		this.socket = socket;
	}
	
	// functions
	public void setupStreams(){
		EZKlogger.debug();
		try {
			out = socket.getOutputStream();
			oout = new ObjectOutputStream(out);

			EZKlogger.debug("OutputStreamSet.setupStreams() -> outbound streams set up");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(Message message){
		EZKlogger.debug();
		try{
			oout.writeObject(message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
		
	public void closeStreams(){
		EZKlogger.debug();
		try {
			oout.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
