package Eizikiu_GUI;

import javax.swing.*;
import Eizikiu_Tools.User;
import javafx.scene.layout.Border;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import Eizikiu_Tools.Room;

public class EizikiuClientGUI extends JFrame implements ActionListener,Runnable{
	static final long serialVersionUID = -1111111;
	
	private String selectedRoom;
	private int x = 200, y = 100, width =800, height = 600;
	Dimension d = new Dimension();
	private Toolkit t;
	
	private JPanel panel1;
	private JList<Room> outputRooms;
	private JTextArea toSend, output, outputUsernicknames;
	private List<String> loggedInUsers, rooms;
	private JButton send;
	private JFrame frame;
	
	public EizikiuClientGUI(){
		super();

		
		setBounds(x,y,width,height);
		setTitle("Eizikiu");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.add(initComponents());
//		outputRooms = new JList();
//		
//		
//		toSend = new JTextArea();
//		
//		output = new JTextArea();
//		
//		outputUsernicknames = new JTextArea();
//		outputUsernicknames.setWrapStyleWord(true);
//		outputUsernicknames.setEditable(false);
//		
//		
//	
//		
//		
//		send = new JButton();
//		send.setText("Send");
//		send.addActionListener(this);
//		send.setActionCommand("SENDEN");
//		
//		this.add(output);
//		this.add(toSend);
//		this.add(outputUsernicknames);
//		this.add(outputRooms);
//		this.add(send);
		
		setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent e){
		
	}
	public void run(){
		 
	}
	
	public JPanel initComponents(){
		panel1 = new JPanel();
		BorderLayout borderlayout = new BorderLayout();
		panel1.setLayout(borderlayout);
		
		
		outputRooms = new JList();
		
		
		toSend = new JTextArea();
		
		
		
		output = new JTextArea();
		output.setWrapStyleWord(true);
		output.setEditable(false);
		output.append("Welcome to Eizikiu!");
		
		outputUsernicknames = new JTextArea();
		outputUsernicknames.setWrapStyleWord(true);
		outputUsernicknames.setEditable(false);
		
		
	
		
		
		send = new JButton();
		send.setText("Send");
		send.addActionListener(this);
		send.setActionCommand("SENDEN");
		
		panel1.add(output,BorderLayout.NORTH);
		panel1.add(toSend,BorderLayout.WEST);
		panel1.add(outputUsernicknames,BorderLayout.CENTER);
		panel1.add(outputRooms,BorderLayout.EAST);
		panel1.add(send,BorderLayout.SOUTH);
		
		return panel1;
	}
	
	public void actualizeRooms(){
		
	}
	public static void main(String[] args){
		new EizikiuClientGUI();
	}
}
