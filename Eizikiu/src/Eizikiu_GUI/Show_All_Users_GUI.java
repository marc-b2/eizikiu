package Eizikiu_GUI;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import Eizikiu_Tools.Room;
import Eizikiu_Tools.User;
import Eizikiu_Server.Eizikiu_Server;

public class Show_All_Users_GUI extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();


	public static void main(String[] args) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public Show_All_Users_GUI() {
		
		this.setTitle("Status of all users");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 430, 217);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 420, 217);
		contentPanel.add(scrollPane);
		
		JTextArea listOutput = new JTextArea();
		listOutput.setEditable(false);
		for(User x : Eizikiu_Server.getGlobalUserList()) {
			listOutput.append(x.everythingToString() + "\n");
		}
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
						Show_All_Users_GUI.this.dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}this.setVisible(true);
	}
	
}
