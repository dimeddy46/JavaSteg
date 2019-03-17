package stegSource;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class HideForm extends JFrame {
	private String covFileName,msgFileName;
	
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

	public HideForm() throws IOException {
		setTitle("Hide binary file");
		setSize(750,410);
		setMinimumSize(new Dimension(750,410));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel covTxt = new JLabel("Select cover");
		JLabel msgTxt = new JLabel("Select message");
		getContentPane().add(panel);	
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(3,3,3,3);
		JLabel title = new JLabel("Hide file");
		title.setFont(new Font("Consolas", Font.BOLD, 20));
		gbc.gridx = 1;
		gbc.gridy = 0;	
		gbc.anchor = GridBagConstraints.PAGE_START;
		panel.add(title, gbc);
		
		
		
		JButton covBtn = new JButton("Browse");
		covBtn.setPreferredSize(new Dimension(120,25));
		covBtn.setFont(new Font("Consolas", Font.BOLD, 15));
		gbc.gridx = 0;
		gbc.gridy = 1;		
		covBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser fc = new JFileChooser();				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					covFileName = fc.getSelectedFile().getName();
					covTxt.setText("<html>"+covFileName+" <br/>Size:"+fc.getSelectedFile().length()+" bytes</html>");
				}
			}
		});
		panel.add(covBtn,gbc);
		
		JButton msgBtn = new JButton("Browse");
		msgBtn.setFont(new Font("Consolas", Font.BOLD, 15));
		msgBtn.setPreferredSize(new Dimension(120,25));
		gbc.gridx = 2;
		gbc.gridy = 1;
		msgBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser fc = new JFileChooser();				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					msgFileName = fc.getSelectedFile().getName();
					msgTxt.setText("<html>"+msgFileName+" <br/>Size: "+fc.getSelectedFile().length()+" bytes</html>");
				}
			}
		});
		panel.add(msgBtn,gbc);
				
		
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 2;
		covTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(covTxt, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		msgTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(msgTxt, gbc);
		
		gbc.weighty = 1;
		gbc.insets = new Insets(0,0,0,0);
		JLabel covImg = new JLabel();		
		covImg.setBounds(100, 0, 300, 180);		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.PAGE_START;
		BufferedImage myPicture = ImageIO.read(new File("mere2.png"));
		Image dimg = myPicture.getScaledInstance(covImg.getWidth(), covImg.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(dimg);
		covImg.setIcon(icon);		
		panel.add(covImg,gbc);

		JLabel msgImg = new JLabel();		
		msgImg.setBounds(100, 0, 300, 180);		
		gbc.gridx = 2;
		gbc.gridy = 4;
		myPicture = ImageIO.read(new File("hidden.png"));
		dimg = myPicture.getScaledInstance(msgImg.getWidth(), msgImg.getHeight(), Image.SCALE_SMOOTH);
		icon = new ImageIcon(dimg);
		msgImg.setIcon(icon);			
		panel.add(msgImg,gbc);
		
	}
}
