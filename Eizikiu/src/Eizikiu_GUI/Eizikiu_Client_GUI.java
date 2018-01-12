package Eizikiu_GUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import Eizikiu_Client.Eizikiu_Client;
import Eizikiu_Server.Eizikiu_Server;
import Eizikiu_Tools.Message;
import Eizikiu_Tools.Room;
import Eizikiu_Tools.User;


public class Eizikiu_Client_GUI extends KeyAdapter implements ActionListener, ItemListener{

	private JFrame frmEizikiuClient;
	private JTextArea chatOutput, chatInput;
	JList<User> userList; 
	JList<Room> roomList;
	JCheckBoxMenuItem infoChecker, logChecker, debugChecker;
	// starten der GUI
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Eizikiu_Client_GUI window = new Eizikiu_Client_GUI();
					window.frmEizikiuClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// 
	public Eizikiu_Client_GUI() {
		initialize();
	}

	// Initialisieren der GUI
	private void initialize() {
		frmEizikiuClient = new JFrame();
		frmEizikiuClient.setTitle("Eizikiu");
		frmEizikiuClient.setBounds(100, 100, 570, 500);
		frmEizikiuClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEizikiuClient.getContentPane().setLayout(null);
		
		JButton sendButton = new JButton("Send");
		sendButton.setBounds(274, 362, 97, 60);
		sendButton.addKeyListener(this);
		sendButton.setActionCommand("SENDEN");
		frmEizikiuClient.getContentPane().add(sendButton);
		
		chatOutput = new JTextArea();
		chatOutput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatOutput.setEditable(false);
		chatOutput.setBounds(10, 30, 360, 320);
		frmEizikiuClient.getContentPane().add(chatOutput);
		
		//ListenVerwaltung 
		JTabbedPane listHolder = new JTabbedPane(JTabbedPane.TOP);
		listHolder.setBounds(383, 26, 160, 396);
		frmEizikiuClient.getContentPane().add(listHolder);
		
		JScrollPane scrollUserList = new JScrollPane();
		listHolder.addTab("New tab", null, scrollUserList, null);
		
		DefaultListModel<User> uList = actualizeUserList();
		userList = new JList<User>(uList);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollUserList.setViewportView(userList);
		
		JScrollPane scrollRoomList = new JScrollPane();
		listHolder.addTab("New tab", null, scrollRoomList, null);
		
		
		DefaultListModel<Room> rList = actualizeRoomList();
		roomList = new JList<Room>(rList);
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollRoomList.setViewportView(roomList);
		
		listHolder.setTitleAt(0, "Users");
		listHolder.setTitleAt(1, "Rooms");
		
		// Menubar erstellen
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 150, 25);
		frmEizikiuClient.getContentPane().add(menuBar);
		
		
		JMenu dateiMenu = new JMenu("File");
		menuBar.add(dateiMenu);
		
		
		JMenu anzeigeMenu = new JMenu("Anzeige");
		menuBar.add(anzeigeMenu);
		
		infoChecker = new JCheckBoxMenuItem("Info");
		anzeigeMenu.add(infoChecker);
		
		logChecker = new JCheckBoxMenuItem("Log");
		anzeigeMenu.add(logChecker);
		
		
		
		chatInput = new JTextArea();
		chatInput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatInput.setBounds(10, 362, 255, 60);
		chatInput.addKeyListener(this);
		frmEizikiuClient.getContentPane().add(chatInput);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "SENDEN") {
			// hier muss ebenfalls das absenden der Nachricht geschehen
			chatInput.setText(null);
			chatInput.repaint();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		 int key = e.getKeyCode();
	     if (key == KeyEvent.VK_ENTER && chatInput.getText()!= null) {
	    	 // hier muss das Absenden der Nachricht geschehen
			
	    	 chatInput.setText(null);
	    	 chatInput.repaint();
	    	 
		}
	}@Override
	public void itemStateChanged(ItemEvent e) {
		if(((JCheckBoxMenuItem) e.getItem()) == infoChecker) {
			if(infoChecker.getState()== true) {
				
			}else {
				
			}
		}else if(((JCheckBoxMenuItem) e.getItem()) == logChecker){
			if(logChecker.getState()== true) {
				
			}else {
				
			}
			
			
		}else if(((JCheckBoxMenuItem) e.getItem()) == debugChecker){
			if(debugChecker.getState()== true) {
				
			}else {
				
			}	
		}
	}
	public void writeString(String str) {
		chatOutput.append(str + "\n");
	}
	public void writeMessage(Message m) {
		chatOutput.append(m.toString() + "\n");
	}
	
	// aktualisieren die Listen die den JList zu Grunde liegen
	public DefaultListModel<User> actualizeUserList() {
		DefaultListModel<User> uList = new DefaultListModel<User>();
		try {
			for(User u : Eizikiu_Client.getGlobalUserList()) {
				uList.addElement(u);
			}return uList;
		}catch(Exception e){
			this.writeString("Es sind keine User angemeldet");
			return uList;
		}
	}
	
	public DefaultListModel<Room> actualizeRoomList(){
		DefaultListModel<Room> rList = new DefaultListModel<Room>();
		try {
			for(Room r : Eizikiu_Client.getPublicRooms()) {
				rList.addElement(r);
			}
			return rList;
		}catch(Exception e) {
			this.writeString("Es sind keine R�ume vorhanden");
			return rList;
		}
	}
	// Methoden die dann zum Aktualisieren der Room/User Listn verwendet werden
	public void actualizeUserJList() {
		this.actualizeUserList();
		this.userList.repaint();
	}
	public void actualizeRoomJList() {
		this.actualizeRoomList();
		this.roomList.repaint();
	}
	
	public JFrame getFrmEizikiuClient() {
		return frmEizikiuClient;
	}
}
