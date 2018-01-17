package Eizikiu_GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Eizikiu_Tools.EZKlogger;


public class Chat_Tab extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private int tabID;
	private JButton sendButton;
	private JTextArea chatOutput,chatInput;
	
	public Chat_Tab(int roomID) {
		EZKlogger.debug();
	
		this.tabID = roomID;
		this.setBounds(10,10,355,380);
		
		
		
		
		sendButton = new JButton();
		sendButton.setBounds(274, 362, 97, 60);
		sendButton.addActionListener(this);
		sendButton.setActionCommand("SENDEN");
		
		
		chatInput = new JTextArea();
		chatInput.setLineWrap(true);
		chatInput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatInput.setBounds(10, 362, 255, 60);
		chatInput.setWrapStyleWord(true);
		
		
		chatOutput = new JTextArea();
		chatOutput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatOutput.setEditable(false);
		chatOutput.setBounds(10, 70, 360, 370);
		
		this.add(chatOutput);
		this.add(chatInput);
		this.add(sendButton);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		EZKlogger.debug();
		if(e.getActionCommand() == "SENDEN") {
			// hier muss ebenfalls das absenden der Nachricht geschehen
			
			
			chatInput.setText(null);
			chatInput.repaint();
		}
	}
	
	public JTextArea getChatInput() {
		return chatInput;
	}
	public JTextArea getChatOutput() {
		return chatOutput;
	}

	public int getTabID() {
		EZKlogger.debug();
		return tabID;
	}

	public void setTabID(int tabID) {
		EZKlogger.debug();
		this.tabID = tabID;
	}
	
	
}
