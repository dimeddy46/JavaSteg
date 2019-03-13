package stegSource;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
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

public class Interf extends JFrame {
	static final long serialVersionUID = 1;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interf frame = new Interf();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Interf() {
		setResizable(false);
		setPreferredSize(new Dimension(200, 200));
		setMinimumSize(new Dimension(100, 150));
		setTitle("StegLSB");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 442, 310);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Menu");
		lblNewLabel.setFont(new Font("Consolas", Font.BOLD, 17));
		getContentPane().add(lblNewLabel);
		
		Component verticalStrut = Box.createVerticalStrut(200);
		getContentPane().add(verticalStrut);
		
		Component horizontalStrut = Box.createHorizontalStrut(500);
		getContentPane().add(horizontalStrut);
		
		JButton btnNewButton = new JButton("Hide");
		btnNewButton.setFont(new Font("Consolas", Font.BOLD, 15));
		getContentPane().add(btnNewButton);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(40);
		getContentPane().add(horizontalStrut_1);
		
		JButton btnNewButton_1 = new JButton("Extract");
		btnNewButton_1.setFont(new Font("Consolas", Font.BOLD, 15));
		getContentPane().add(btnNewButton_1);
	}
}
