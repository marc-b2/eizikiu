package Eizikiu_GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class EizikiuServerGUI extends JFrame implements ActionListener{
	static final long serialVersionUID = -1111111;
	
	private int x, y, width =800, height = 600;
	private Toolkit t;
	
	public EizikiuServerGUI(){
		
		Dimension d = t.getScreenSize();
		x= (int)((d.getWidth()- width)/2);
		y = (int)((d.getHeight() - height)/2);
		
		setBounds(x,y,width,height);
		setTitle("EizikiuServer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e){
		
	}
}
