package Eizikiu_GUI;

import javax.swing.*;


import Eizikiu_Tools.EZKlogger;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * deprecated
 */
public class RegistryGUI extends JFrame implements ActionListener, Runnable{
	public static final long serialVersionUID = -11111111;
	
	private JTextField textfieldName = new JTextField();
	private JPasswordField password = new JPasswordField();
	private JPasswordField passwordRepeat = new JPasswordField();
	
	private int height = 25;
	private int width = 500;
	private Dimension d = new Dimension(width, height);
	
	private Box panel;
	
	public RegistryGUI(){
		
		super();
		EZKlogger.debug();
		
		setTitle("Eizikiu Registry");
		this.setSize(300,200);
		this.setLocation(800,300);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.getContentPane().add(initComponents());
		
		
		
		this.setVisible(true);
		
	}
	@Override
	public void actionPerformed(ActionEvent e){
		EZKlogger.debug();
		if(e.getActionCommand().equals("REGISTER")){
			if (password.getPassword().equals(passwordRepeat.getPassword())){
//				String name = textfieldName.getText();
//				@SuppressWarnings("deprecation")
//				String pw = password.getText();
				 
			//	if(Eizikiu_Client.register(name, pw, this))
				{
//					new Eizikiu_Client_GUI();
//					Eizikiu_Client.chat();
				}
			}	
		}
			if(e.getActionCommand().equals("ANMELDEFENSTER")){
			// hier öffnet sich das Anmeldefenster 
			
			new LogInGUI();
			this.setVisible(false);
			
		}	
	}
	
	private Box initComponents(){
		EZKlogger.debug();
		//JPanel als Container
		panel = new Box(BoxLayout.Y_AXIS);
		
		// initialisiere die Beschriftung
		JLabel text1 = new JLabel("Geben sie den gewünschten Usernamen an:");
		JLabel text2 = new JLabel("Geben sie ihr Password an:");
		JLabel text3 = new JLabel("Wiederholen sie ihr Passwort");
		
		//initialisiere Textfelder
		textfieldName.setMaximumSize(d);
		
		password.setMaximumSize(d);
		
		passwordRepeat.setMaximumSize(d);
		
		JButton register = new JButton();
		register.setText("Registrieren");
		register.addActionListener(this);
		register.setActionCommand("REGISTER");
		
		JButton anmeldeFenster = new JButton();
		anmeldeFenster.setText("Mit bestehendem Account anmelden");
		anmeldeFenster.addActionListener(this);
		anmeldeFenster.setActionCommand("ANMELDEN");
		
		// dem Container hinzufügen
		panel.add(text1);
		panel.add(textfieldName);
		panel.add(text2);
		panel.add(password);
		panel.add(text3);
		panel.add(passwordRepeat);
		panel.add(register);
		panel.add(anmeldeFenster);

		
		return panel;
	}
	
	public void run(){
		EZKlogger.debug();
	}
	
	public Box getPanel() {
		EZKlogger.debug();
		return panel;
	}
	
	/*public static void main(String[] args){
		RegistryGUI Registrierung = new RegistryGUI();
		
	}*/
}


