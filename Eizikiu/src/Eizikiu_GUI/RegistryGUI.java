package Eizikiu_GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegistryGUI extends JFrame implements ActionListener {
	public RegistryGUI(){
		super();
		
		setTitle("Eizikiu Registry");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel text1 = new JLabel("Geben sie den gewŁnschten Usernamen an:");
		JLabel text2 = new JLabel("Geben sie ihr Password an:");
		JLabel text3 = new JLabel("Wiederholen sie ihr Passwort");
				
		JTextField textfieldName = new JTextField();
		JTextField textfieldPassword = new JTextField();
		JTextField textfieldPasswordRepeat = new JTextField();
		
		JButton register = new JButton();
		
		this.setLayout(new FlowLayout());
		
		this.add(text1);
		this.add(textfieldName);
		this.add(text2);
		this.add(textfieldPassword);
		this.add(text3);
		this.add(textfieldPasswordRepeat);
		this.add(register);
		
		this.pack();
		
	}
	
	public void actionPerformed(){
		
	}
	public static void main(String[] args){
		RegistryGUI Registrierung = new RegistryGUI();
		Registrierung.setSize(300,150);
		Registrierung.setLocation(800,300);
		Registrierung.setVisible(true);
	}
}

}
