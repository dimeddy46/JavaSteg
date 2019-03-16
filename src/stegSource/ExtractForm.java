package stegSource;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ExtractForm extends JFrame {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExtractForm frame = new ExtractForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ExtractForm() 
	{
		setTitle("Extract data from file");
		setSize(600,400);
		setMinimumSize(new Dimension(600,400));
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);		
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		JLabel title = new JLabel("Extract data");
		title.setFont(new Font("Consolas", Font.BOLD, 20));
		gbc.gridx = 1;
		gbc.gridy = 0;		
		panel.add(title, gbc);
		
		gbc.weighty = 1;
		gbc.insets = new Insets(10,5,5,5);
		
		JLabel covTxt = new JLabel("Select cover:");
		covTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridx = 1;
		gbc.gridy = 1;		
		panel.add(covTxt, gbc);
	}
}
