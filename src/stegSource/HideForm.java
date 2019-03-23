package stegSource;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javafx.stage.Screen;

@SuppressWarnings("serial")
	public class HideForm extends JFrame {

	String covFileName = null, msgFileName = null, defDir = null;
	int xImg = 340, yImg = 185;
		
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
	private boolean checkFileExtension(String str, String[] exts)
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
	
	private static void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
	
	public HideForm() throws IOException {
	//	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//	int xRes = (int)screenSize.getWidth();
	//	int yRes = (int)screenSize.getHeight();				
		setTitle("StegLSB");
		setSize(790, 460);
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) 
		{  }
		
		String[] modes = { "Hecht", "LSB(text)", "Lossless" };
		
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel title = new JLabel("Hide file");
		
		JButton covBtn = new JButton("Select cover");
		JButton msgBtn = new JButton("Select message");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");
		JLabel msgTxt = new JLabel("<html><br/><br/></html>");
			
		JLabel covImg = new JLabel();	
		JLabel msgImg = new JLabel();
		
		JLabel modeTxt = new JLabel("Mode:");
		JLabel pwdTxt = new JLabel("Password:");
		
		JComboBox<String> hideModeCombo = new JComboBox<>(modes);		
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm");	
		
		//---------------- LSB mode ------------
		JTextArea msgInput = new JTextArea();	
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);		
		// title
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.gridwidth = 3;
		title.setFont(new Font("Consolas", Font.BOLD, 22));
		panel.add(title, gbc);
		
		// select cover button	
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;	
		covBtn.setFont(new Font("Consolas", Font.BOLD, 14));			
		panel.add(covBtn,gbc);		
		
		// select message button
		gbc.gridx = 2;
		gbc.gridy = 1;
		msgBtn.setFont(new Font("Consolas", Font.BOLD, 14));
		panel.add(msgBtn,gbc);
		
		// cover label(below cover button)
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridx = 0;
		gbc.gridy = 2;	
		covTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(covTxt, gbc);
		
		// message label(below message button)
		gbc.gridx = 2;
		gbc.gridy = 2;		
		msgTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(msgTxt, gbc);
			
		//gbc.insets = new Insets(0,0,0,0);
		
		// cover image repres.	
		gbc.weighty = 1;
		gbc.weightx = 1;		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.PAGE_START;
		covImg.setBounds(0, 0, xImg, yImg);
		covImg.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));	
		covImg.setPreferredSize(new Dimension(xImg, yImg));
		panel.add(covImg,gbc);
		
		// message text input (text area)	
		gbc.gridx = 2;				// when mode is changed to LSB(text) it becomes visible
		gbc.gridy = 4;		    			    			    		
		msgInput.setPreferredSize(new Dimension(340,185));
		msgInput.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
		msgInput.setLineWrap(true);
		msgInput.setFont(new Font("Consolas", Font.BOLD, 14));
		msgInput.setVisible(false);
		panel.add(msgInput, gbc);
		
		// message image repres.	
		gbc.gridx = 2;				// Hecht mode -> showing message image
		gbc.gridy = 4;
		msgImg.setBounds(0, 0, xImg, yImg);		
		msgImg.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
		msgImg.setPreferredSize(new Dimension(xImg, yImg));
		panel.add(msgImg,gbc);
		
		// hide mode label
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.PAGE_END;
		modeTxt.setFont(new Font("Consolas", Font.BOLD, 17));	
		panel.add(modeTxt,gbc);
		
		// password label
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 5;		
		pwdTxt.setFont(new Font("Consolas", Font.BOLD, 17));
		panel.add(pwdTxt, gbc);
		
		// password input text field
		gbc.gridx = 0;
		gbc.gridy = 6;	
		gbc.anchor = GridBagConstraints.PAGE_START;
		pwdInput.setPreferredSize(new Dimension(180,25));
		pwdInput.setFont(new Font("Consolas", Font.BOLD, 13));
		panel.add(pwdInput, gbc);		
		
		// combobox hide mode	
		gbc.gridwidth = 1;		
		gbc.gridx = 0;
		gbc.gridy = 6;		
		hideModeCombo.setSelectedIndex(0);
		hideModeCombo.setFont(new Font("Consolas", Font.BOLD, 14));		
		panel.add(hideModeCombo, gbc);
			
		// confirm button	
		gbc.gridx = 2;
		gbc.gridy = 6;		
		gbc.weighty = 3;		
		confirmBtn.setFont(new Font("Consolas", Font.BOLD, 15));	
		panel.add(confirmBtn, gbc);
		
		hideModeCombo.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) 
		    {	
		        if(hideModeCombo.getSelectedIndex() == 0)
		        {
		        	msgInput.setVisible(false);
		        	msgImg.setVisible(true);
		        	msgTxt.setText("<html><br/><br/></html>");		        	
		        }
		        else if(hideModeCombo.getSelectedIndex() == 1)
		        {
		        	msgInput.setVisible(true);
		        	msgImg.setVisible(false);	
		    		msgTxt.setText("<html>Select a .txt file, or<br/> enter your message here:</html>");		        			    		
		        }
		        else if(hideModeCombo.getSelectedIndex() == 2)
		        {
		        	msgImg.setVisible(false);
		        }		        
		    }
		});
		covBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				JFileChooser fc = new JFileChooser();						
				if(defDir != null)
					fc.setCurrentDirectory(new File(defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					BufferedImage img;
					int sizeImg;					
					
					if(!checkFileExtension(fc.getSelectedFile().toString(), new String[]{".png",".bmp"}))
					{
						infoBox("Invalid file. Please select an image as your cover.");
						return;
					}
					
					try {
						img = ImageIO.read(fc.getSelectedFile());
					} catch (Exception e1) {
						System.out.println("EXCP");
						return;
					}		
					covFileName = fc.getSelectedFile().toString();
					sizeImg = img.getHeight() * img.getWidth() * 3;
					covImg.setIcon(new ImageIcon(img.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
					
					covTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								  "</font><br/>Size: "+sizeImg+" bytes</html>");	
					
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
					BufferedImage img;
					long sizeFile = 0;
					int index = hideModeCombo.getSelectedIndex();
										
					if(index == 0)
					{
						try {
							img = ImageIO.read(fc.getSelectedFile());
							sizeFile = img.getHeight() * img.getWidth() * 3;
							msgImg.setIcon(new ImageIcon(img.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
						} 
						catch (Exception e1) {
							infoBox("Invalid file. Please select a image file for Hecht steganography.");
							return;
						}
					}
					else if((index == 1 && checkFileExtension(fc.getSelectedFile().toString(), new String[]{".txt"})) || index == 2 )
					{
						sizeFile =	fc.getSelectedFile().length();								
					}
					
					msgFileName = fc.getSelectedFile().toString();					
					msgTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								   "</font><br/>Size: "+sizeFile+" bytes</html>");				
					
					defDir = fc.getCurrentDirectory().toString();
				}
			}
		});
		
		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Mat cov, msg, rez = null;
				String pwd = pwdInput.getText();
				int index = hideModeCombo.getSelectedIndex();
				String strMessage = msgInput.getText();
				
				if( covFileName == null || ((index == 1 && strMessage.length() == 0) && (index == 1 && msgFileName == null)) ) 
				{
					infoBox("You must select both cover and message file.");
					return;
				}
				cov = Highgui.imread(covFileName);
				
				if(pwd.length() <= 3)
				{
					infoBox("Password can't be shorter than 4 characters.");
					return;
				}
				
				if(index == 0)
				{
					msg = Highgui.imread(msgFileName);
					rez = OpenCV.hideImgHecht(cov, msg, pwd);
				}
				else if(index == 1)
				{
					if(checkFileExtension(msgFileName, new String[]{".txt"}))	// if a text file is selected
						strMessage = new String(OpenCV.readFile(msgFileName));		// overwrite the msgInput text
					rez = OpenCV.hideImgText(cov, strMessage, pwd);
				}
				else if(index == 2)
					rez = OpenCV.hideLosslessFile(cov, OpenCV.readFile(msgFileName), pwd);
				
				if(rez.rows() == 1)
				{
					infoBox("The message file is bigger than cover file or an invalid file has been selected.");				
					return;
				}
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(defDir));
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					pwd = fc.getSelectedFile().toString();  // if no extension or an invalid one is provided, assign .png	
					if(!checkFileExtension(pwd, new String[]{".png",".bmp"}))
						pwd += ".png";	
					
					Highgui.imwrite(pwd, rez);	
					title.setText("Image hidden succesfully!");
					
					Desktop dt = Desktop.getDesktop();
				    try{ 				    
					   dt.open(new File(pwd));
					   dt.open(new File(covFileName));
					}
				   catch(IOException ex){}
				}	
				
			}
		});
		getContentPane().add(panel);
	}
}
