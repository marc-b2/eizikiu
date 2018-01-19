package Eizikiu_GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;

import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Eizikiu_Client.Eizikiu_Client;
import Eizikiu_Server.Eizikiu_Server;
import Eizikiu_Tools.EZKlogger;
import Eizikiu_Tools.Message;
import Eizikiu_Tools.Room;
import Eizikiu_Tools.User;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;


//@Marc: Um die Dialogfenster zu erstellen braucht man nur die Methode JOptionPane.showMessageDialog(Component c (das Fenster in dem sich die Nachricht öffnen soll),
//Object nachricht, String titel, int nachrichtentyp(ERROR_MESSAGE,INFORMATION_MESSAGE,WARNING_MESSAGE,QUESTION_MESSAGE,PLAIN_MESSAGE))
public class Eizikiu_Server_GUI implements ItemListener, ActionListener, Runnable{

	private JFrame frmEizikiuServer;
	private JTextArea chatOutput;
	private JCheckBoxMenuItem infoChecker, logChecker, debugChecker, safeLogToChecker;
	private JList<Room> roomList;
	private JList<User> userList;
	private DefaultListModel<Room> rList;
	private DefaultListModel<User>uList;
	
	
	public static void main(String[] args) {
		EZKlogger.debug();
		EventQueue.invokeLater(new Eizikiu_Server_GUI());
	}

	
	public Eizikiu_Server_GUI() {
		EZKlogger.debug();
		initialize();
	}

	
	private void initialize() {
		EZKlogger.debug();
		frmEizikiuServer = new JFrame();
		frmEizikiuServer.setTitle("Eizikiu Server");
		frmEizikiuServer.setBounds(100, 100, 600, 500);
		frmEizikiuServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEizikiuServer.setLayout(null);
		frmEizikiuServer.getContentPane().setLayout(null);
		
		chatOutput = new JTextArea();
		chatOutput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatOutput.setEditable(false);
		chatOutput.setBounds(10, 70, 360, 370);
		frmEizikiuServer.getContentPane().add(chatOutput);
		
		JTabbedPane listHolder = new JTabbedPane(JTabbedPane.TOP);
		listHolder.setBounds(380, 40, 190, 400);
		
		frmEizikiuServer.getContentPane().add(listHolder);
		
		
		JScrollPane scrollUserList = new JScrollPane();
		listHolder.addTab("New tab", null, scrollUserList, null);
		
//Einrichten der Listen:
		
//Erstellt ListModel auf das dann durch JList zugegriffen wird
		uList = actualizeUserList();
		userList = new JList<User>(uList);
		
		rList =actualizeRoomList();
		roomList = new JList<Room>(rList);
		
// Als Scrollable		
		JScrollPane scrollRoomList = new JScrollPane();
		scrollRoomList.setViewportView(roomList);
		scrollUserList.setViewportView(userList);
		listHolder.addTab("New tab", null, scrollRoomList, null);
		
		
		
		
		listHolder.setTitleAt(0, "Users");
		listHolder.setTitleAt(1, "Rooms");
		
		
// Initialisieren der Menubar und des jeweiligen Button/CheckBox
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 880, 25);
		frmEizikiuServer.getContentPane().add(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem close_File_MenuItem = new JMenuItem("Close");
		close_File_MenuItem.addActionListener(this);
		close_File_MenuItem.setActionCommand("CLOSE");
		fileMenu.add(close_File_MenuItem);
		
		
//AnzeigeMenu zum Einstellen der Logbuchmitschriebe
		JMenu anzeigeMenu = new JMenu("Window");
		menuBar.add(anzeigeMenu);
		
		infoChecker = new JCheckBoxMenuItem("Info");
		infoChecker.addItemListener(this);
		anzeigeMenu.add(infoChecker);
		
		
		logChecker = new JCheckBoxMenuItem("Log");
		logChecker.addItemListener(this);
		anzeigeMenu.add(logChecker);
		
		debugChecker = new JCheckBoxMenuItem("Debug");
		debugChecker.addItemListener(this);
		anzeigeMenu.add(debugChecker);
		
		safeLogToChecker = new JCheckBoxMenuItem("Safe Log to File");
		safeLogToChecker.addItemListener(this);
		anzeigeMenu.add(safeLogToChecker);
		
		
//UserMenu zum verwalten der User
		JMenu userMenu = new JMenu("User");
		
		
		JMenuItem warn_User_MenuItem= new JMenuItem("Warn");
		warn_User_MenuItem.addActionListener(this);
		warn_User_MenuItem.setActionCommand("WARN");
		
		JMenuItem kick_User_MenuItem= new JMenuItem("Kick");
		kick_User_MenuItem.addActionListener(this);
		kick_User_MenuItem.setActionCommand("KICK");
		
		JMenuItem bann_User_MenuItem= new JMenuItem("Bann");
		bann_User_MenuItem.addActionListener(this);
		bann_User_MenuItem.setActionCommand("BANN");
		
		
//RoomMenu mit der Möglichkeit die Rooms zu editieren
		JMenu roomMenu = new JMenu("Rooms");
		menuBar.add(roomMenu);
		
		JMenuItem create_Room_MenuItem = new JMenuItem("Create new room");
		create_Room_MenuItem.addActionListener(this);
		create_Room_MenuItem.setActionCommand("CREATEROOMS");
		
		JMenuItem edit_Room_MenuItem = new JMenuItem("Edit");
		edit_Room_MenuItem.addActionListener(this);
		edit_Room_MenuItem.setActionCommand("EDITROOMS");
		
		JMenuItem show_UserList_MenuItem = new JMenuItem("Edit Userlist");
		show_UserList_MenuItem.addActionListener(this);
		show_UserList_MenuItem.setActionCommand("EDITUSERLIST");
		
		JMenuItem delete_Rooms_MenuItem = new JMenuItem("Delete");
		delete_Rooms_MenuItem.addActionListener(this);
		delete_Rooms_MenuItem.setActionCommand("DELETE");
		
		menuBar.add(userMenu);
		userMenu.add(warn_User_MenuItem);
		userMenu.add(kick_User_MenuItem);
		userMenu.add(bann_User_MenuItem);
		
		roomMenu.add(create_Room_MenuItem);
		roomMenu.add(edit_Room_MenuItem);
		roomMenu.add(show_UserList_MenuItem);
		roomMenu.add(delete_Rooms_MenuItem);
// Label das als Überschrift für die Logausgabe gilt
		JLabel lblLog = new JLabel("Log:");
		lblLog.setBounds(12, 52, 56, 16);
		
		frmEizikiuServer.getContentPane().add(lblLog);
		
	}
	
//Steuerungsblock für alle Buttons und MenuItems
	@Override
	public void actionPerformed(ActionEvent e) {
		EZKlogger.debug();
		if(e.getActionCommand() == "EDITROOMS") {
			
			String newName = JOptionPane.showInputDialog(frmEizikiuServer,"Select new name for the room:");
			Eizikiu_Server.editRoom(Eizikiu_Server_GUI.this.roomList.getSelectedValue(), newName);
			this.actualizeRoomJList();
			
		}else if(e.getActionCommand()=="CREATEROOMS") {
			String newName = JOptionPane.showInputDialog(frmEizikiuServer,"Select new name for the room:");
			Eizikiu_Server.createRoom(newName);
			this.actualizeRoomList();
			
		}else if(e.getActionCommand()=="EDITUSERLIST") {
			
			Eizikiu_Server_GUI.this.roomList.getSelectedValue().getUserList();
			
		}else if(e.getActionCommand()=="CLOSE") {
			
			System.exit(0);
			
		}else if(e.getActionCommand()=="DELETE") {
			Eizikiu_Server.deleteRoom(Eizikiu_Server_GUI.this.roomList.getSelectedValue());
			this.actualizeRoomJList();
			
		}else if(e.getActionCommand()=="WARN") {
			String newWarning= JOptionPane.showInputDialog(frmEizikiuServer,"Warn User:");
			Eizikiu_Server.warnUser(userList.getSelectedValue(), newWarning);
			
		}else if(e.getActionCommand()=="KICK") {
			Eizikiu_Server.warnUser(userList.getSelectedValue(), "You have been kicked by server! \n");
			try {
				userList.getSelectedValue().getConnection().shutdown();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}else if(e.getActionCommand()=="BANN") {
			Eizikiu_Server.warnUser(userList.getSelectedValue(), "You have been permanently banned! \n");
			try {
				userList.getSelectedValue().getConnection().shutdown();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			userList.getSelectedValue().setBanned(true);
		}
	}
	
	// ItemState überprüfung die die jeweiligen Checker bedient (und die Logs mitschreibt oder eben nicht)
	@Override
	public void itemStateChanged(ItemEvent e) {
		EZKlogger.debug();
		if(((JCheckBoxMenuItem) e.getItem()) == infoChecker) {
			if(infoChecker.getState()== true) {
				EZKlogger.setLoglevel(0);
				logChecker.setState(false);
				debugChecker.setState(false);
			
		}else if(((JCheckBoxMenuItem) e.getItem()) == logChecker){
			if(logChecker.getState()== true) {
				EZKlogger.setLoglevel(1);
				infoChecker.setState(false);
				debugChecker.setState(false);
		
			
		}else if(((JCheckBoxMenuItem) e.getItem()) == debugChecker){
			if(debugChecker.getState()== true) {
				EZKlogger.setLoglevel(2);
				infoChecker.setState(false);
				logChecker.setState(false);
			}
		}	
		}else if(((JCheckBoxMenuItem) e.getItem()) == safeLogToChecker){
			if(safeLogToChecker.getState()== true) {
				EZKlogger.setFileOutput(true);
			}else {
				EZKlogger.setFileOutput(false);
			}
		}
		}	
	}
	@Override
	public void run() {
		EZKlogger.debug();
		try {
			frmEizikiuServer.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeString(String str) {
		this.chatOutput.append(str);
		this.frmEizikiuServer.repaint();
	}
	/**
	 * Schreibt den Inhalt String eines Message-Objekts in die Ausgabe des Servers
	 * @param m
	 */
	public void writeMessage(Message m) {
		EZKlogger.debug();
	
		chatOutput.append(m.toString());
	}
	/** Ermöglicht das Schreiben eines Strings in die Ausgabe des Servers.
	 * 	 * @param str
	 */
	public void writeLogger(String message) {
		EZKlogger.debug();
		chatOutput.append(message);
	}
	/**
	 * aktualisiert das DefaultListModel der UserJList
	 * @return
	 * @deprecated
	 */
	public DefaultListModel<User> actualizeUserList() {
		EZKlogger.debug();
		this.uList = new DefaultListModel<User>();
		try {
			for(User u : Eizikiu_Server.getGlobalUserList()) {
				uList.addElement(u);
			}return uList;
		}catch(Exception e){
			this.writeLogger("Es sind keine User angemeldet" + "\n");
			return uList;
		}
	}
	/**
	 * aktualisiert das DefaultListModel der RoomJList
	 * @return
	 * @deprecated
	 */
	public DefaultListModel<Room> actualizeRoomList(){
		EZKlogger.debug();
		this.rList = new DefaultListModel<Room>();
		try {
			for(Room r : Eizikiu_Server.getPublicRooms()) {
				rList.addElement(r);
			}
			return rList;
		}catch(Exception e) {
			this.writeLogger("Es sind keine Räume vorhanden" + "\n");
			return rList;
		}
	}
	// Methoden die dann zum Aktualisieren der Room/User Listn verwendet werden
	/**
	 * aktualisiert die UserJList
	 */
	public void actualizeUserJList() {
		for(int i = 0; i < this.userList.getModel().getSize(); i++ ) {
			boolean exist = false;
			User temp = this.userList.getModel().getElementAt(i);
			for(int j = 0; j < Eizikiu_Server.getGlobalUserList().size(); j++) {
				if(this.userList.getModel().getElementAt(i).getName() == Eizikiu_Server.getGlobalUserList().get(j).getName()) {
					exist = true;
				}
			}
			if(!exist) {
				((DefaultListModel<User>)userList.getModel()).removeElementAt(((DefaultListModel<User>)userList.getModel()).indexOf(temp));
			}
		}for(int i = 0; i < Eizikiu_Server.getGlobalUserList().size(); i++ ) {
			boolean exist = false;
			User temp = Eizikiu_Server.getGlobalUserList().get(i);
			for(int j = 0; j < this.userList.getModel().getSize(); j++) {
				if(this.userList.getModel().getElementAt(i).getName() == Eizikiu_Server.getGlobalUserList().get(j).getName()) {
					exist = true;
				}
			}if(!exist) {
				((DefaultListModel<User>)this.userList.getModel()).addElement(temp);
			}
		}
	}
	/**
	 * aktualisiert die RoomJList
	 */
	public void actualizeRoomJList() {
		for(int i = 0; i < this.roomList.getModel().getSize(); i++ ) {
			boolean exist = false;
			Room temp = this.roomList.getModel().getElementAt(i);
			for(int j = 0; j < Eizikiu_Server.getPublicRooms().size(); j++) {
				if(this.roomList.getModel().getElementAt(i).getName() == Eizikiu_Server.getPublicRooms().get(j).getName()) {
					exist = true;
				}
			}
			if(!exist) {
				((DefaultListModel<Room>)roomList.getModel()).removeElementAt(((DefaultListModel<Room>)roomList.getModel()).indexOf(temp));
			}
		}for(int i = 0; i < Eizikiu_Server.getGlobalUserList().size(); i++ ) {
			boolean exist = false;
			Room temp = Eizikiu_Server.getPublicRooms().get(i);
			for(int j = 0; j < this.roomList.getModel().getSize(); j++) {
				if(this.roomList.getModel().getElementAt(i).getName() == Eizikiu_Server.getGlobalUserList().get(j).getName()) {
					exist = true;
				}
			}if(!exist) {
				((DefaultListModel<Room>)this.roomList.getModel()).addElement(temp);
			}
		}
	}
}
