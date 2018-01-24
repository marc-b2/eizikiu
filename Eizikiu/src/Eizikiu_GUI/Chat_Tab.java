package Eizikiu_GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Eizikiu_Tools.EZKlogger;
import Eizikiu_Tools.Room;


public class Chat_Tab extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private Room tabroom;
	private JTextArea chatOutput;
	private JScrollBar scrollBar;
	
	
	public Chat_Tab(Room tabroom) {
		EZKlogger.debug();
		this.setLayout(null);
		this.tabroom = tabroom;
		this.setBounds(10,10,355,380);
		
		final JPanel scrollPanel = new JPanel();
		scrollPanel.setBounds(0,0,350,300);
		scrollPanel.setLayout(null);
		
		JScrollPane scrollYourChat = new JScrollPane();
		scrollYourChat.setBounds(0, 0, 350, 280);
		
		scrollBar = scrollYourChat.getVerticalScrollBar();
		
		chatOutput = new JTextArea();
		chatOutput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatOutput.setBounds(0, 0, 350, 300);
		chatOutput.setLineWrap(true);
		chatOutput.setWrapStyleWord(true);
		chatOutput.setEditable(false);
		
		scrollPanel.add(scrollYourChat);
		scrollYourChat.setViewportView(chatOutput);
		this.add(scrollPanel);
		
	}
	
	public JScrollBar getScrollBar() {
		return scrollBar;
	}
	
	public JTextArea getChatOutput() {
		return chatOutput;
	}

	public Room getTabRoom() {
		EZKlogger.debug();
		return tabroom;
	}

	
	
}
