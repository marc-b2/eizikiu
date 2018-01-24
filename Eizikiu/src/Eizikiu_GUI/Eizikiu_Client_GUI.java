package Eizikiu_GUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import Eizikiu_Client.Eizikiu_Client;
import Eizikiu_Tools.EZKlogger;
import Eizikiu_Tools.Message;
import Eizikiu_Tools.Room;
import Eizikiu_Tools.User;
import javax.swing.JLabel;


public class Eizikiu_Client_GUI extends KeyAdapter implements ActionListener, ItemListener, Runnable{

	private JFrame frmEizikiuClient;
	private Chat_Tab chatTab;
	
	private DefaultListModel<User> uList;
	private DefaultListModel<Room> rList;
	
	
	private JList<User> userList; 
	private JList<Room> roomList;
	private JCheckBoxMenuItem infoChecker;
	private JTabbedPane listHolder, chatHolder;
	private JButton sendButton, closeTab;
	private JTextField chatInput;
	private JLabel infoBar;
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
		
		// Anlegen des ChatOut und ChatInput
		chatTab = new Chat_Tab(Eizikiu_Client.getPublicRooms().get(0));
		
		//ListenVerwaltung 
		
		
		listHolder = new JTabbedPane(JTabbedPane.TOP);
		listHolder.setBounds(383, 26, 160, 396);
		frmEizikiuClient.getContentPane().add(listHolder);

		
//Erstellen der Listen und den zugehoerigen Scrollables
		
		
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
		infoChecker.addItemListener(this);
		anzeigeMenu.add(infoChecker);
		
		JMenu userMenu = new JMenu("User");
		menuBar.add(userMenu);
		
		JMenuItem startChat= new JMenuItem("Start private chat");
		startChat.addActionListener(this);
		startChat.setActionCommand("PRIVATE");
		userMenu.add(startChat);
		
		JMenuItem showRoomsMember= new JMenuItem("Show users in room");
		showRoomsMember.addActionListener(this);
		showRoomsMember.setActionCommand("SHOW");
		
		
		JMenu roomMenu = new JMenu("Room");
		roomMenu.add(showRoomsMember);
		menuBar.add(roomMenu);
		
		sendButton = new JButton("Send");
		sendButton.setBounds(260, 340, 100, 50);
		sendButton.addActionListener(this);
		sendButton.setActionCommand("SENDEN");
		
		closeTab = new JButton("Leave Conversation");
		closeTab.setBounds(0,100,20,20);
		closeTab.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e){
				try {
					if (((Chat_Tab) chatHolder.getSelectedComponent()).getTabRoom().getID()!=1) {
						Eizikiu_Client.chatLeave(((Chat_Tab) chatHolder.getSelectedComponent()).getTabRoom().getID());
						chatHolder.removeTabAt(chatHolder.getSelectedIndex());
					}
					
				}catch(Exception exception){
					Eizikiu_Client_GUI.this.writeString("Leaving this room is currently not possible!\n");
				}
					
		}
		});
		
		
		chatInput = new JTextField();
		chatInput.setBorder(new LineBorder(new Color(0, 0, 0)));
		chatInput.setBounds(10, 340, 250, 50);
		chatInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chatInput.getText()!=null) {
					String temp = chatInput.getText();
					try {
					Eizikiu_Client.sendMessage(temp, ((Chat_Tab) chatHolder.getSelectedComponent()).getTabRoom().getID());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				
					chatInput.setText(null);
					chatInput.repaint();
				}
			}
		});
		
		chatHolder = new JTabbedPane();
		chatHolder.setBounds(10,30,350,300);
		chatHolder.addTab(rList.getElementAt(0).getName(),chatTab);
		frmEizikiuClient.getContentPane().add(chatHolder);
		
		JMenuItem join_Room_MenuItem = new JMenuItem("Join");
		join_Room_MenuItem.addActionListener(this);
		join_Room_MenuItem.setActionCommand("JOIN");
		roomMenu.add(join_Room_MenuItem);
		
		frmEizikiuClient.getContentPane().add(closeTab);
		frmEizikiuClient.getContentPane().add(sendButton);
		frmEizikiuClient.getContentPane().add(chatInput);
		
		infoBar = new JLabel("");
		infoBar.setBounds(10, 424, 250, 16);
		frmEizikiuClient.getContentPane().add(infoBar);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		EZKlogger.debug();
		if(e.getActionCommand() == "SENDEN") {
			if(chatInput.getText()!=null) {
				String temp = chatInput.getText();
				try {
					Eizikiu_Client.sendMessage(temp, ((Chat_Tab) chatHolder.getSelectedComponent()).getTabRoom().getID());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				chatInput.setText(null);
				chatInput.repaint();
			}
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
		}else if(e.getActionCommand() == "SHOW"){
			if(roomList.getSelectedValue().getUserList() != null && roomList.getSelectedValue().getID() != 1)	{
				new Show_List_GUI(roomList.getSelectedValue());
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		EZKlogger.debug();
		 int key = e.getKeyCode();
	     if (key == KeyEvent.VK_ENTER) {
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
			frmEizikiuClient.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void writeString(String str) {
		EZKlogger.debug();
		chatTab.getChatOutput().append(str + "\n");
	}
	public void writeMessage(Message m) {
		EZKlogger.debug("" + chatHolder.getTabCount());
		for(int i=0; i<chatHolder.getTabCount();i++) {
			EZKlogger.debug("for-loop " + ((Chat_Tab) chatHolder.getComponentAt(i)).getTabRoom().getID());
			int temp = ((Chat_Tab) chatHolder.getComponentAt(i)).getTabRoom().getID();
			if(temp == m.getRoomID()){
				EZKlogger.debug();
				((Chat_Tab)chatHolder.getComponentAt(i)).getChatOutput().append("["+ m.getSenderName()+"]: " + m.getMessage() + "\n");
				((Chat_Tab)chatHolder.getComponentAt(i)).getScrollBar().setValue(((Chat_Tab)chatHolder.getComponentAt(i)).getScrollBar().getMaximum());
				break;
			}
		}
		
	}
	
	/**
	 * aktualisiert die rootListe der userJList
	 * @return
	 */
	public DefaultListModel<User> actualizeUserList() {
		EZKlogger.debug();
		uList = new DefaultListModel<User>();
		try {
			for(User u : Eizikiu_Client.getGlobalUserList()) {
				uList.addElement(u);
			}return uList;
		}catch(Exception e){
			this.writeString("Es sind keine User angemeldet" + "\n");
			return uList;
		}
	}
	/**
	 * aktualisiert die rootList der roomJList
	 * @return
	 */
	public DefaultListModel<Room> actualizeRoomList(){
		EZKlogger.debug();

		rList = new DefaultListModel<Room>();

		this.rList = new DefaultListModel<Room>();

		try {
			for(Room r : Eizikiu_Client.getPublicRooms()) {
				rList.addElement(r);
			}
			return rList;
		}catch(Exception e) {
			this.writeString("Es sind keine Raeume vorhanden" + "\n");
			return rList;
		}
	}
	/**
	 * aktualisiert die userJList
	 */
	// lösche alle Elemente aus der Liste und füge alle danach wieder hinzu
	public void actualizeUserJList() {
		((DefaultListModel<User>) userList.getModel()).removeAllElements();
		for(User x : Eizikiu_Client.getGlobalUserList()) {
			((DefaultListModel<User>) userList.getModel()).addElement(x);
		}
	}
	/**
	 * aktualisiert die roomJList
	 */
	public void actualizeRoomJList() {
		
		((DefaultListModel<Room>) roomList.getModel()).removeAllElements();
		for(Room x : Eizikiu_Client.getPublicRooms()) {
			
			((DefaultListModel<Room>) roomList.getModel()).addElement(x);
		}
	}
	
	/**
	 * Öffnet einen neuen Tab mit einer Unterhaltung zu einem public room
	 * @param roomID
	 */
	public void newChat(Room room) {
		EZKlogger.debug();
		this.chatHolder.addTab(((Room)roomList.getSelectedValue()).getName(), new Chat_Tab((Room)roomList.getSelectedValue()));
		frmEizikiuClient.repaint();
	}
	/** 
	 * Öffnet einen neuen Tab mit einer Unterhaltung zu einem pivate room
	 * @param nameChatPartner
	 * @param roomID
	 */
	public void newChat(String nameChatPartner, Room room) {
		EZKlogger.debug("new Chat gestartet");
		this.chatHolder.addTab("Private: " + nameChatPartner, new Chat_Tab(room));
		frmEizikiuClient.repaint();
	}
	
	
	public JFrame getFrmEizikiuClient() {
		EZKlogger.debug();
		return frmEizikiuClient;
	}	
	
	
	public JTabbedPane getChatHolder() {
		return chatHolder;
	}
	
	public void showServerInfo(String str) {
		EZKlogger.debug();
		this.infoBar.setText(str);
	}
}
