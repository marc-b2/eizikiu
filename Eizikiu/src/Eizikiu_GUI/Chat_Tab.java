package Eizikiu_GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;


public class Chat_Tab extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private int tabID;
	private JButton sendButton;
	private JTextArea chatOutput,chatInput;
	
	public Chat_Tab(int roomID) {
	
		this.tabID = roomID;
		this.setBounds(10,10,355,380);
		
		this.add(sendButton);
		this.add(chatInput);
		this.add(chatOutput);
		
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
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "SENDEN") {
			// hier muss ebenfalls das absenden der Nachricht geschehen
			
			
			chatInput.setText(null);
			chatInput.repaint();
		}
	}
	
	

	public int getTabID() {
		return tabID;
	}

	public void setTabID(int tabID) {
		this.tabID = tabID;
	}
	
	
}
