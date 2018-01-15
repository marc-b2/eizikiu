package Eizikiu_GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;
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

import Eizikiu_Client.Eizikiu_Client;
import Eizikiu_Tools.EZKlogger;
import Eizikiu_Tools.Message;
import Eizikiu_Tools.Room;
import Eizikiu_Tools.User;


public class Eizikiu_Client_GUI extends KeyAdapter implements ActionListener, ItemListener, Runnable{

	private JFrame frmEizikiuClient;
	private JTextArea chatOutput, chatInput;
	DefaultListModel<Room> rList;
	JList<User> userList; 
	JList<Room> roomList;
	JCheckBoxMenuItem infoChecker, logChecker, debugChecker;
	JTabbedPane listHolder, chatHolder;
	// starten der GUI
	public static void main(String[] args) {
		EZKlogger.debug();
		EventQueue.invokeLater(new Eizikiu_Client_GUI());
	}

	// 
	public Eizikiu_Client_GUI() {
		EZKlogger.debug();
		initialize();
	}

	// Initialisieren der GUI
	private void initialize() {
		EZKlogger.debug();
		frmEizikiuClient = new JFrame();
		frmEizikiuClient.setTitle("Eizikiu");
		frmEizikiuClient.setBounds(100, 100, 570, 500);
		frmEizikiuClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEizikiuClient.getContentPane().setLayout(null);
		
		Chat_Tab chatTab = new Chat_Tab(((Room)roomList.getSelectedValue()).getID());
		chatHolder = new JTabbedPane();
		chatHolder.setBounds(10,42,355,380);
		chatHolder.addTab(rList.getElementAt(0).getName(),chatTab);
		frmEizikiuClient.getContentPane().add(chatHolder);
		
		//ListenVerwaltung 
		listHolder = new JTabbedPane(JTabbedPane.TOP);
		listHolder.setBounds(383, 26, 160, 396);
		frmEizikiuClient.getContentPane().add(listHolder);
		
		JScrollPane scrollUserList = new JScrollPane();
		listHolder.addTab("New tab", null, scrollUserList, null);
		
		DefaultListModel<User> uList = actualizeUserList();
		userList = new JList<User>(uList);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollUserList.setColumnHeaderView(userList);
		
		JScrollPane scrollRoomList = new JScrollPane();
		listHolder.addTab("New tab", null, scrollRoomList, null);
		
		
		rList = actualizeRoomList();
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
		
		JMenuItem menuItem = new JMenuItem("Close");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("CLOSE");
		
		dateiMenu.add(menuItem);
		
		
		JMenu anzeigeMenu = new JMenu("Logging");
		menuBar.add(anzeigeMenu);
		
		infoChecker = new JCheckBoxMenuItem("Activated");
		anzeigeMenu.add(infoChecker);
		
		JMenu userMenu = new JMenu("User");
		menuBar.add(userMenu);
		
		JMenuItem startChat= new JMenuItem("Start private chat");
		startChat.addActionListener(this);
		startChat.setActionCommand("PRIVATE");
		userMenu.add(startChat);
		
		JMenu roomMenu = new JMenu("Room");
		menuBar.add(roomMenu);
		
		JMenuItem join_Room_MenuItem = new JMenuItem("Join");
		join_Room_MenuItem.addActionListener(this);
		join_Room_MenuItem.setActionCommand("JOIN");
		roomMenu.add(join_Room_MenuItem);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		EZKlogger.debug();
		if(e.getActionCommand() == "SENDEN") {
			// hier muss ebenfalls das absenden der Nachricht geschehen
			
			
			chatInput.setText(null);
			chatInput.repaint();
		}
		else if(e.getActionCommand() == "PRIVATE"){
			try {
				Eizikiu_Client.privateChatRequest(((User)userList.getSelectedValue()).getName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else if(e.getActionCommand() == "JOIN"){
			try {
				Eizikiu_Client.publicChatRequest(((Room)roomList.getSelectedValue()).getID());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else if(e.getActionCommand() == "CLOSE"){
			try {
				Eizikiu_Client.shutdown();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		EZKlogger.debug();
		 int key = e.getKeyCode();
	     if (key == KeyEvent.VK_ENTER && chatInput.getText()!= null) {
	    	 // hier muss das Absenden der Nachricht geschehen
			
	    	 chatInput.setText(null);
	    	 chatInput.repaint();
	    	 
		}
	}@Override
	public void itemStateChanged(ItemEvent e) {
		EZKlogger.debug();
		if(((JCheckBoxMenuItem) e.getItem()) == infoChecker) {
			if(infoChecker.getState()== true) {
				EZKlogger.setLoglevel(2);
				EZKlogger.setFileOutput(true);
			}else {
				EZKlogger.setLoglevel(0);
				EZKlogger.setFileOutput(false);
			}
		}	
	}
	@Override
	public void run() {
		EZKlogger.debug();
		try {
			Eizikiu_Client_GUI window = new Eizikiu_Client_GUI();
			window.frmEizikiuClient.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void writeString(String str) {
		EZKlogger.debug();
		chatOutput.append(str + "\n");
	}
	public void writeMessage(Message m) {
		EZKlogger.debug();
		chatOutput.append(m.toString() + "\n");
	}
	
	// aktualisieren die Listen die den JList zu Grunde liegen
	public DefaultListModel<User> actualizeUserList() {
		EZKlogger.debug();
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
		EZKlogger.debug();
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
		EZKlogger.debug();
		this.actualizeUserList();
		this.userList.repaint();
	}
	public void actualizeRoomJList() {
		EZKlogger.debug();
		this.actualizeRoomList();
		this.roomList.repaint();
	}
	
	
	public void newChat(int roomID) {
		EZKlogger.debug();
		this.chatHolder.addTab(((Room)roomList.getSelectedValue()).getName(), new Chat_Tab(((Room)roomList.getSelectedValue()).getID()));
		frmEizikiuClient.repaint();
	}
	public JFrame getFrmEizikiuClient() {
		EZKlogger.debug();
		return frmEizikiuClient;
	}
}
