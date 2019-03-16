package stegSource;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class HideForm extends JFrame {
	int x;
	String p = "salut";
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HideForm frame = new HideForm();
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
	public HideForm() {
		setTitle("Hide binary file");
		setSize(600,400);
		setMinimumSize(new Dimension(600,400));
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		//gbc.insets = new Insets(5,5,5,5);
		JLabel title = new JLabel("Hide file");
		title.setFont(new Font("Consolas", Font.BOLD, 20));
		gbc.gridx = 1;
		gbc.gridy = 0;		
		//gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.PAGE_START;
		panel.add(title, gbc);
		
		gbc.insets = new Insets(20,5,5,5);
		gbc.weighty = 1;
		
		JLabel covTxt = new JLabel("Select cover");
		gbc.gridx = 0;
		gbc.gridy = 1;
		covTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(covTxt, gbc);
		
		JLabel msgTxt = new JLabel("Select message");
		gbc.gridx = 2;
		gbc.gridy = 1;
		msgTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(msgTxt, gbc);
	}
}
