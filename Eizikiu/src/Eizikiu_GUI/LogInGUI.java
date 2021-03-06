package Eizikiu_GUI;

import javax.swing.*;

import Eizikiu_Client.Eizikiu_Client;
import Eizikiu_Tools.EZKlogger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInGUI extends JFrame implements ActionListener, Runnable{
	static final long serialVersionUID = -1111111;
	
	private JTextField textfieldName = new JTextField();
	private JPasswordField password = new JPasswordField();
	
	private JButton anmeldeButton = new JButton();
	private JButton registrierungsButton = new JButton();
	
	private int height = 25;
	private int width = 500;
	private Dimension d = new Dimension(width, height);
	
	private Box box;
	
	public LogInGUI(){
		
		super();
		EZKlogger.debug();
		
		setTitle("Log in to Eizikiu");
		this.setSize(300,200);
		this.setLocation(800,300);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.getContentPane().add(initComponents());
		   this.setVisible(true);
	}
	
	@Override 
	public void actionPerformed(ActionEvent e){
		EZKlogger.debug();
		if(e.getActionCommand().equals("ANMELDEN") && textfieldName.getText() != null && password.getPassword() != null){
			 String name = textfieldName.getText();
			 @SuppressWarnings("deprecation")
			 String pw = password.getText();
			 
			 if(Eizikiu_Client.login(name, pw, this)){
				 LogInGUI.this.dispose();
			 }else {
				 this.textfieldName.setText("");
				 this.password.setText("");
			 }

		}
		if(e.getActionCommand().equals("REGISTRIEREN")){
			EZKlogger.debug();
			new Registry_GUI();
			LogInGUI.this.dispose();
		}
	}
	
	@Override 
	public void run(){
		EZKlogger.debug();
	}
	
	private Box initComponents(){
		EZKlogger.debug();
		box = new Box(BoxLayout.Y_AXIS);
		
		JLabel text1 = new JLabel("Geben sie ihren Usernamen ein:");
		JLabel text2 = new JLabel("Geben sie ihr Password ein:");
		
		textfieldName.setMaximumSize(d);
		textfieldName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				String name = textfieldName.getText();
				 @SuppressWarnings("deprecation")
				 String pw = password.getText();
				 
				 if(Eizikiu_Client.login(name, pw, LogInGUI.this)){
					 LogInGUI.this.dispose();
				 }else {
					 LogInGUI.this.textfieldName.setText("");
					 LogInGUI.this.password.setText("");
				 }
			}
		});
		password.setMaximumSize(d);
		password.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				String name = textfieldName.getText();
				 @SuppressWarnings("deprecation")
				 String pw = password.getText();
				 
				 if(Eizikiu_Client.login(name, pw, LogInGUI.this)){
					 LogInGUI.this.dispose();
				 }else {
					 LogInGUI.this.textfieldName.setText("");
					 LogInGUI.this.password.setText("");
				 }
			}
		});
		anmeldeButton.setText("Anmelden");
		anmeldeButton.addActionListener(this);
		anmeldeButton.setActionCommand("ANMELDEN");
		
		registrierungsButton.setText("Registrieren");
		registrierungsButton.addActionListener(this);
		registrierungsButton.setActionCommand("REGISTRIEREN");
		box.add(text1);
		box.add(textfieldName);
		box.add(text2);
		box.add(password);
		box.add(anmeldeButton);
		box.add(registrierungsButton);
		return box;
	}
	
	public Box getBox() {
		EZKlogger.debug();
		return box;
	}

public static void main(String[] args){
	EZKlogger.debug();
	new LogInGUI();
}
}