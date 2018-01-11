package Eizikiu_GUI;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistryGUI extends JFrame implements ActionListener, Runnable{
	public static final long serialVersionUID = -11111111;
	
	private JTextField textfieldName = new JTextField();
	private JPasswordField password = new JPasswordField();
	private JPasswordField passwordRepeat = new JPasswordField();
	
	private int height = 25;
	private int width = 500;
	private Dimension d = new Dimension(width, height);
	
	
	
	public RegistryGUI(){
		
		super();
		
		setTitle("Eizikiu Registry");
		this.setSize(300,200);
		this.setLocation(800,300);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.getContentPane().add(initComponents());
		
		
		
		this.setVisible(true);
		
	}
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("REGISTER")){
			if (password.getPassword().equals(passwordRepeat.getPassword())){
			
				/* @Mark: hier sollte dann die Message erzeugt werden und abgeschickt (Login)
				 *
				 **/
			}
		}if(e.getActionCommand().equals("ANMELDEFENSTER")){
			// hier öffnet sich das Anmeldefenster 
			
			Thread t = new Thread(new LogInGUI());
			t.start();
			this.setVisible(false);
			
		}	
	}
	
	private Box initComponents(){
		//JPanel als Container
		Box panel = new Box(BoxLayout.Y_AXIS);
		
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

		
		return panel;
	}
	
	public void run(){
		
	}
	
	/*public static void main(String[] args){
		RegistryGUI Registrierung = new RegistryGUI();
		
	}*/
}


