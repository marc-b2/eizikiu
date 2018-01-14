package Eizikiu_GUI;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import Eizikiu_Server.Eizikiu_Server;
import Eizikiu_Tools.EZKlogger;
import Eizikiu_Tools.Message;
import Eizikiu_Tools.Room;
import Eizikiu_Tools.User;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.JToolBar;
import javax.swing.JToggleButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;


//@Marc: Um die Dialogfenster zu erstellen braucht man nur die Methode JOptionPane.showMessageDialog(Component c (das Fenster in dem sich die Nachricht öffnen soll),
//Object nachricht, String titel, int nachrichtentyp(ERROR_MESSAGE,INFORMATION_MESSAGE,WARNING_MESSAGE,QUESTION_MESSAGE,PLAIN_MESSAGE))
public class Eizikiu_Server_GUI implements ItemListener, ActionListener, Runnable{

	private JFrame frmEizikiuServer;
	JTextArea chatOutput;
	JCheckBoxMenuItem infoChecker, logChecker, debugChecker, safeLogToChecker;
	
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
		DefaultListModel<User> uList = new DefaultListModel<User>();
		
		JList<User> userList = new JList<User>(uList);
		// nimmt die UserListe vom Server und fügt sie in die JList ein
		// *********hier gab es eine NullPointerException, weil die Liste nicht existiert, wenn man die main() dieser GUI hier startet
		if(Eizikiu_Server.getGlobalUserList() != null) {
			for(User u : Eizikiu_Server.getGlobalUserList()) {
				uList.addElement(u);
			}
		}
		scrollUserList.setViewportView(userList);
		
		JScrollPane scrollRoomList = new JScrollPane();
		listHolder.addTab("New tab", null, scrollRoomList, null);
		
		DefaultListModel<Room> rList = new DefaultListModel<Room>();
		// ******** hier das gleiche Problem...
		if(Eizikiu_Server.getPublicRooms() != null) {
			for(Room r : Eizikiu_Server.getPublicRooms()) {
				rList.addElement(r);
			}
		}
		if(Eizikiu_Server.getPrivateRooms() != null) {
			for(Room r : Eizikiu_Server.getPrivateRooms()) {
				rList.addElement(r);
			}
		}
		JList<Room> roomList = new JList<Room>(rList);
		scrollRoomList.setViewportView(roomList);
		
		
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
		menuBar.add(userMenu);
		
		JMenuItem options_User_MenuItem= new JMenuItem("Options");
		options_User_MenuItem.addActionListener(this);
		options_User_MenuItem.setActionCommand("USERVERWALTUNG");
		userMenu.add(options_User_MenuItem);
		
		//RoomMenu mit der Möglichkeit die Rooms zu editieren
		JMenu roomMenu = new JMenu("Rooms");
		menuBar.add(roomMenu);
		
		JMenuItem edit_Room_MenuItem = new JMenuItem("Edit");
		edit_Room_MenuItem.addActionListener(this);
		edit_Room_MenuItem.setActionCommand("EDITROOMS");
		roomMenu.add(edit_Room_MenuItem);
		
		JMenuItem delete_Rooms_MenuItem = new JMenuItem("Delete");
		roomMenu.add(delete_Rooms_MenuItem);
		
		// Label das als Überschrift für die Logausgabe gilt
		JLabel lblLog = new JLabel("Log:");
		lblLog.setBounds(12, 52, 56, 16);
		frmEizikiuServer.getContentPane().add(lblLog);
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		EZKlogger.debug();
		if(e.getActionCommand() == "EDITROOMS") {
			
		}else if(e.getActionCommand()=="USERVERWALTUNG") {
			
		}else if(e.getActionCommand()=="CLOSE") {
			
		}else if(e.getActionCommand()=="") {
			
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
			Eizikiu_Server_GUI window = new Eizikiu_Server_GUI();
			window.frmEizikiuServer.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Diese Methoden sollen in der Mainfunktion die System.out.print-Aufrufe ersetzen
	public void writeMessage(Message m) {
		EZKlogger.debug();
		chatOutput.append(m.toString());
	}
	
	public void writeLogger(String message) {
		EZKlogger.debug();
		chatOutput.append(message);
	}
}
