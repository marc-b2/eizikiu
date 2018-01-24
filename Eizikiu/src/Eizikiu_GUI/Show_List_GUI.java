package Eizikiu_GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import Eizikiu_Server.Eizikiu_Server;
import Eizikiu_Tools.Room;
import Eizikiu_Tools.User;

public class Show_List_GUI extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextArea listOutput;
	
	public Show_List_GUI() {
		this.setTitle("Status of all users");
		this.initComp();
		
		for(User x : Eizikiu_Server.getGlobalUserList()) {
			listOutput.append(x.everythingToString() + "\n");
		}
		this.setVisible(true);
	}
	
	public Show_List_GUI(User user) {
		this.setTitle(user.getName() + " is in rooms:");
		this.initComp();
		
		for(Room x : user.getRooms()) {
			listOutput.append(x.toString() + "\n");
		}
		this.setVisible(true);
	}
	
	public Show_List_GUI(Room room) {
		this.setTitle(room.getName() + " holds these users:");
		this.initComp();
		
		for(User x : room.getUserList()) {
			listOutput.append(x.toString() + "\n");
		}
		this.setVisible(true);
	}
	
	public Show_List_GUI(LinkedList<Room> roomList) {
		this.setTitle("All private conversations");
		this.initComp();
		
		for(Room x : roomList) {
			listOutput.append(x.toString() + "\n");
		}
		this.setVisible(true);
	}
		

	
	
	private void initComp() {
		
		this.setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 430, 217);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 420, 217);
		this.contentPanel.add(scrollPane);
		
		listOutput = new JTextArea();
		listOutput.setEditable(false);
		
		scrollPane.setViewportView(listOutput);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 218, 432, 35);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Show_List_GUI.this.dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}


