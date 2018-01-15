package Eizikiu_GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.Box;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import Eizikiu_Client.Eizikiu_Client;

import java.awt.Component;
import javax.swing.JLabel;

public class Registry_GUI {

	private JFrame frmRegister;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Registry_GUI window = new Registry_GUI();
					window.frmRegister.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Registry_GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRegister = new JFrame();
		frmRegister.setTitle("Register ");
		frmRegister.setBounds(100, 100, 300, 300);
		frmRegister.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRegister.getContentPane().setLayout(new BoxLayout(frmRegister.getContentPane(), BoxLayout.X_AXIS));
		
		Box verticalBox = Box.createVerticalBox();
		frmRegister.getContentPane().add(verticalBox);
		
		JLabel label1 = new JLabel("Username:");
		verticalBox.add(label1);
		
		textField = new JTextField();
		verticalBox.add(textField);
		textField.setColumns(10);
		
		JLabel label2 = new JLabel("Password:");
		verticalBox.add(label2);
		
		textField_1 = new JTextField();
		verticalBox.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel label3 = new JLabel("Repeat Password:");
		verticalBox.add(label3);
		
		textField_2 = new JTextField();
		verticalBox.add(textField_2);
		textField_2.setColumns(10);
		
		
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (textField_1.getText().equals(textField_2.getText())){
					String name = textField.getText();
					String pw = textField_1.getText();
					
					//if(Eizikiu_Client.register(name, pw, Registry_GUI.this)){
//					//	new Eizikiu_Client_GUI();
					//	Eizikiu_Client.chat();
					//}
			
				}
			}
		});
		verticalBox.add(registerButton);
		
		JButton loginButton = new JButton("Already registered?");
		loginButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new LogInGUI();
				Registry_GUI.this.frmRegister.dispose();
			}
		});
		verticalBox.add(loginButton);
		frmRegister.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textField, textField_1, textField_2, registerButton}));
		
		
	}

}
