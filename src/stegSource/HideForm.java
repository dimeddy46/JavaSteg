package stegSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

@SuppressWarnings("serial")
public class HideForm extends JFrame {
	String covFileName, msgFileName, defDir = null;
	int xImg, yImg;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
					HideForm frame = new HideForm();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public HideForm() throws IOException {
		setTitle("StegLSB");
		setSize(770,460);
		setMinimumSize(new Dimension(770,460));
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) 
		 {       
		 }


		String[] modes = { "Hecht", "LSB(text)", "Lossless" };
		
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel title = new JLabel("Hide file");
		
		JButton covBtn = new JButton("Select cover");
		JButton msgBtn = new JButton("Select message");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");
		JLabel msgTxt = new JLabel("<html><br/><br/></html>");
			
		JLabel covImg = new JLabel();	
		JLabel msgImg = new JLabel();
		
		JComboBox<String> hideMode = new JComboBox<>(modes);
		JLabel pwdTxt = new JLabel("Password:");
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm");	
 
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);		
		
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.gridwidth = 3;
		title.setFont(new Font("Consolas", Font.BOLD, 22));
		panel.add(title, gbc);
			
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;	
		covBtn.setFont(new Font("Consolas", Font.BOLD, 14));			
		panel.add(covBtn,gbc);		
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		msgBtn.setFont(new Font("Consolas", Font.BOLD, 14));
		panel.add(msgBtn,gbc);
		
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridx = 0;
		gbc.gridy = 2;	
		covTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(covTxt, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;		
		msgTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(msgTxt, gbc);
			
		gbc.insets = new Insets(0,0,0,0);
		
		gbc.weighty = 1;
		gbc.weightx = 1;		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.PAGE_START;
		covImg.setBounds(100, 0, 340, 185);
		covImg.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));	
		xImg = covImg.getWidth();
		yImg = covImg.getHeight();		
		covImg.setPreferredSize(new Dimension(xImg, yImg));
		panel.add(covImg,gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 4;
		msgImg.setBounds(100, 0, 340, 185);		
		msgImg.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
		msgImg.setPreferredSize(new Dimension(xImg, yImg));
		panel.add(msgImg,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		hideMode.setSelectedIndex(0);
		hideMode.setFont(new Font("Consolas", Font.BOLD, 14));		
		panel.add(hideMode, gbc);
		
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 5;		
		gbc.anchor = GridBagConstraints.PAGE_END;
		pwdTxt.setFont(new Font("Consolas", Font.BOLD, 17));
		panel.add(pwdTxt, gbc);
				
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.anchor = GridBagConstraints.PAGE_START;
		pwdInput.setPreferredSize(new Dimension(180,25));
		pwdInput.setFont(new Font("Consolas", Font.BOLD, 13));
		panel.add(pwdInput, gbc);		
		
		gbc.gridwidth = 1;
		gbc.gridx = 2;
		gbc.gridy = 6;		
		gbc.weighty = 3;		
		gbc.anchor = GridBagConstraints.PAGE_START;
		confirmBtn.setFont(new Font("Consolas", Font.BOLD, 15));	
		panel.add(confirmBtn, gbc);
		
		covBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser fc = new JFileChooser();						
				if(defDir != null)
					fc.setCurrentDirectory(new File(defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					covFileName = fc.getSelectedFile().toString();
					covTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+"</font><br/>Size: "
							+fc.getSelectedFile().length()+" bytes</html>");
					try {
						covImg.setIcon(new ImageIcon(ImageIO.read(fc.getSelectedFile()).
							   getScaledInstance(xImg,yImg, Image.SCALE_SMOOTH)));
					} catch (IOException e1) {}	
					
					defDir = fc.getCurrentDirectory().toString();
				}
			}
		});
		
		msgBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();				
				if(defDir != null)
					fc.setCurrentDirectory(new File(defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					msgFileName = fc.getSelectedFile().toString();
					msgTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+"</font><br/>Size: "
							+fc.getSelectedFile().length()+" bytes</html>");
					try {
						msgImg.setIcon(new ImageIcon(ImageIO.read(fc.getSelectedFile()).
							   getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
					} 
					catch (IOException e1) {}
					
					defDir = fc.getCurrentDirectory().toString();
				}
			}
		});
		
		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Mat cov = Highgui.imread(covFileName), msg = Highgui.imread(msgFileName);
				if(pwdInput.getText().length() <= 3)
				{
					System.out.println("pwd scurt");
					return;
				}
				
				Mat rez = OpenCV.hideImgHecht(cov, msg, pwdInput.getText());
				
				if(rez.rows() == 1)
				{
					System.out.println("Cover image must be 3 times bigger than message");
					return;
				}
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(defDir));
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					Highgui.imwrite(fc.getSelectedFile().toString(), rez);
				}		
				title.setText("Image hidden succesfully: "+fc.getSelectedFile().getName());
			}
		});
		getContentPane().add(panel);
	}
}
