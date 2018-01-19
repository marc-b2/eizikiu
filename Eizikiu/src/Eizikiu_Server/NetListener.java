package Eizikiu_Server;

import java.net.*;

import Eizikiu_GUI.Eizikiu_Server_GUI;
import Eizikiu_Tools.*;

public class NetListener implements Runnable {
	
	Eizikiu_Server_GUI gui = null;
	
	public NetListener(Eizikiu_Server_GUI gui) {
		this.gui = gui;
		EZKlogger.debug();
		}

	@Override
	public void run() {
		EZKlogger.debug();
		EZKlogger.log("NetListener.run() --> ...succesfully!");

		while(true) {
			
			try {
				ServerSocket listener = new ServerSocket(1234);
				EZKlogger.info("NetListener.run() --> ServerSocket aufgebaut\n");

				Socket socket = listener.accept(); // wait for connection
				
				Thread x = new Thread(new ConnectionToClient(socket, gui));
				x.start();
				
				listener.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
