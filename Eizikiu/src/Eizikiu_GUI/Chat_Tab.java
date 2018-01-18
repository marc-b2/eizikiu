package Eizikiu_GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Eizikiu_Tools.EZKlogger;
import Eizikiu_Tools.Room;


public class Chat_Tab extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private Room tabroom;
	private JTextArea chatOutput;
	
	
	
	public Chat_Tab(Room tabroom) {
		EZKlogger.debug();
		this.setLayout(null);
		this.tabroom = tabroom;
		this.setBounds(10,10,355,380);
		
		chatOutput = new JTextArea();
		chatOutput.setBounds(0, 0, 350, 300);
		chatOutput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatOutput.setEditable(false);
		
		
		this.add(chatOutput);
		
	}
	
	
	
	public JTextArea getChatOutput() {
		return chatOutput;
	}

	public Room getTabRoom() {
		EZKlogger.debug();
		return tabroom;
	}

	
	
}
