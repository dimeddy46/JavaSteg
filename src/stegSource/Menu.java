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
	private static void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Error", JOptionPane.ERROR_MESSAGE);
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
