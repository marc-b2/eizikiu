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

	public void sendMessage(Message message) throws Exception{
		EZKlogger.debug();
		oout.writeObject(message);
	}
		
	public void closeStreams() throws Exception{
		EZKlogger.debug();
		oout.close();
		out.close();	
	}
}
