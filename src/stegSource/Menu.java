package stegSource;


import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.opencv.core.Core;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import resources.ResourceLoader;
import java.awt.Font;
import java.awt.Insets;

public class Menu extends JFrame {
	static final long serialVersionUID = 1;
	public static Image noImage = ResourceLoader.loadImage("no-image-selected2.png"),
					    fileImage = ResourceLoader.loadImage("file.png");
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {				
				try {
					 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);	
				     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    } 
				catch (UnsatisfiedLinkError e) {
					infoBox("opencv_java2413.dll not found.");
					return;
				}
			    catch (UnsupportedLookAndFeelException | ClassNotFoundException | 
			    		InstantiationException | IllegalAccessException e)  {  } 
				
				new Menu().setVisible(true);						
			}
		});
	}
	
	public static void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
	
	public static String toMillions(long n)	// converts an number to a string with commas after every 3 digits
	{
		StringBuilder str = new StringBuilder(Long.toString(n));
		String rez = "";
		int i, ct = 3, len = str.length();
		
		for(i = 0;i < len / 3; i++)
		{
			rez = ","+str.substring(len-ct, len-ct+3) + rez;
			ct += 3;
		}
		
		rez = str.substring(0, len % 3) + rez;		
		if(rez.charAt(0) == ',')
			return rez.substring(1,rez.length());
		return rez;
	}
	
	public static boolean checkFileExtension(String str, String[] exts)
	{
		if(str == null)
			return false;
		
		int x = str.lastIndexOf(".");		
		if(x == -1)
			return false;
		
		str = str.substring(x, str.length());
		
		for(String s : exts)
			if(str.equals(s))
				return true;
		return false;
	}
	public Menu() 
	{
		setTitle("StegLSB");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,300);
		setMinimumSize(new Dimension(400,300));
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);		
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);
		JLabel title = new JLabel("StegLSB");
		title.setFont(new Font("Consolas", Font.BOLD, 20));
		gbc.gridx = 1;
		gbc.gridy = 0;		
		panel.add(title, gbc);
		gbc.weighty = 1;
		
		JButton hideBtn = new JButton("Hide file");
		hideBtn.setFont(new Font("Consolas", Font.BOLD, 17));
		gbc.gridx = 0;
		gbc.gridy = 2;
		hideBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				 new HideForm().setVisible(true);									
			}
		});
		panel.add(hideBtn,gbc);
		
		JButton extBtn = new JButton(" Extract ");
		extBtn.setFont(new Font("Consolas", Font.BOLD, 17));
		gbc.gridx = 2;
		gbc.gridy = 2;		
		extBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				new ExtractForm().setVisible(true);
			}
		});
		panel.add(extBtn,gbc);
	}
}
