package Eizikiu_GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInGUI extends JFrame implements ActionListener, Runnable{
	static final long serialVersionUID = -1111111;
	
	private JTextField textfieldName = new JTextField();
	private JPasswordField password = new JPasswordField();
	
	private JButton anmeldeButton = new JButton();
	
	private int height = 25;
	private int width = 500;
	private Dimension d = new Dimension(width, height);
	
	public LogInGUI(){
		
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
		if(e.getActionCommand().equals("ANMELDEN") && textfieldName.getText() != null && password.getPassword() != null){
			// hier kommt wieder die Message generation hin 

		}
		if(e.getActionCommand().equals("ANMELDEFENSTER")){
			// hier öffnet sich das Anmeldefenster 
			Thread t = new Thread(new LogInGUI());
			t.start();
			this.setVisible(false);
		}
	}
	
	@Override 
	public void run(){
		new LogInGUI();
	}
	
	private Box initComponents(){
		Box box = new Box(BoxLayout.Y_AXIS);
		
		JLabel text1 = new JLabel("Geben sie ihren Usernamen ein:");
		JLabel text2 = new JLabel("Geben sie ihr Password ein:");
		
		textfieldName.setMaximumSize(d);
		
		password.setMaximumSize(d);
		
		anmeldeButton.setText("Anmelden");
		anmeldeButton.addActionListener(this);
		anmeldeButton.setActionCommand("ANMELDEN");
		
		
		box.add(text1);
		box.add(textfieldName);
		box.add(text2);
		box.add(password);
		return box;
	}
}
