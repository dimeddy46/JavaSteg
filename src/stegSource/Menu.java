package stegSource;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.border.EtchedBorder;

public class Menu extends JFrame {
	static final long serialVersionUID = 1;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
				HideForm m;
				try {
					m = new HideForm();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
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
				ExtractForm m = new ExtractForm();
				m.setVisible(true);
			}
		});
		panel.add(extBtn,gbc);
	}
}
