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
import javax.swing.border.BevelBorder;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javafx.stage.Screen;
import resources.ResourceLoader;

@SuppressWarnings("serial")
public class HideForm extends JFrame {

	String covFileName, msgFileName, defDir, memoMsgStr = "";
	int xImg = 340, yImg = 200;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {				
				try {
					 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);	
				     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				     new HideForm().setVisible(true);
			    } 
				catch (UnsatisfiedLinkError e) {
					System.out.println("DLL");
					return;
				}
			    catch (UnsupportedLookAndFeelException | ClassNotFoundException | 
			    		InstantiationException | IllegalAccessException e)  {  } 
				
			}
		});
	}
		
	public HideForm() {
	//	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//	int xRes = (int)screenSize.getWidth();
	//	int yRes = (int)screenSize.getHeight();				
		setTitle("StegLSB");
		setSize(790, 460);
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
		gbc.anchor = GridBagConstraints.PAGE_START;
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
		gbc.gridx = 0;
		gbc.gridy = 2;	
		covTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(covTxt, gbc);
		
		// message label(below message button)
		gbc.gridx = 2;
		gbc.gridy = 2;		
		msgTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(msgTxt, gbc);			
		
		// image cover repres.	
		gbc.weighty = 1;
		gbc.weightx = 1;		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.PAGE_START;		
		covImg.setIcon(new ImageIcon(Menu.noImage.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
		covImg.setBounds(0, 0, xImg, yImg);
		covImg.setPreferredSize(new Dimension(xImg, yImg));
		panel.add(covImg,gbc);
		
		// text input message (text area)	
		gbc.gridx = 2;				// when mode is changed to LSB(text) it becomes visible
		gbc.gridy = 4;		    			    			    		
		msgInput.setPreferredSize(new Dimension(xImg, yImg));
		msgInput.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
		msgInput.setLineWrap(true);
		msgInput.setFont(new Font("Consolas", Font.BOLD, 14));
		msgInput.setVisible(false);
		panel.add(msgInput, gbc);
		
		// image message repres.	
		gbc.gridx = 2;				// Hecht mode -> showing message image
		gbc.gridy = 4;
		msgImg.setBounds(0, 0, xImg, yImg);		
		msgImg.setIcon(new ImageIcon(Menu.noImage.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
		msgImg.setPreferredSize(new Dimension(xImg, yImg));
		panel.add(msgImg,gbc);
		
		// label hide mode
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
		    	int index = hideModeCombo.getSelectedIndex();
		        if(index == 0 || index == 2)
		        {
		        	msgInput.setVisible(false);
		        	msgImg.setVisible(true);
		        	msgTxt.setText(memoMsgStr);
		        }
		        else if(index == 1)
		        {		        	
		        	msgInput.setVisible(true);
		        	msgImg.setVisible(false);	
		        	memoMsgStr = msgTxt.getText();
		    		msgTxt.setText("<html>Select a .txt file, or<br/> enter your message here:</html>");		        			    		
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
					BufferedImage img = null;
					int sizeImg = 0;					
					
					if(!Menu.checkFileExtension(fc.getSelectedFile().toString(), new String[]{".png",".bmp"}))
					{
						Menu.infoBox("Invalid file. Please select an image as your cover.");
						return;
					}
					
					try {
						img = ImageIO.read(fc.getSelectedFile());						
						sizeImg = img.getHeight() * img.getWidth() * 3;
						covImg.setIcon(new ImageIcon(img.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
					} 
					catch (Exception e1) { }		
					
					
					covTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								  "</font><br/>Size: "+Menu.toMillions(sizeImg)+" bytes</html>");	
					
					covFileName = fc.getSelectedFile().toString();
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
					try {
						img = ImageIO.read(fc.getSelectedFile());
						sizeFile = img.getHeight() * img.getWidth() * 3;
						msgImg.setIcon(new ImageIcon(img.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
					} 
					catch (Exception e1) 
					{
						if(index == 0)
						{
							Menu.infoBox("Invalid file. Please select a image file as message for Hecht steganography.");
							return;
						}
						else if(index == 1 && !Menu.checkFileExtension(fc.getSelectedFile().toString(), new String[]{".txt"}))
						{
							Menu.infoBox("Invalid file. A text file is required for LSB hiding mode.");
							return;	
						}														
						else if(index == 2)						
							msgImg.setIcon(new ImageIcon(Menu.fileImage.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
						
					}				
					if(index == 1)	
						sizeFile = fc.getSelectedFile().length();	

					msgTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								   "</font><br/>Size: "+Menu.toMillions(sizeFile)+" bytes</html>");	
					
					msgFileName = fc.getSelectedFile().toString();	
					memoMsgStr = msgTxt.getText();					
					defDir = fc.getCurrentDirectory().toString();
				}
			}
		});
		
		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Mat cov, msg, rez = null;
				String key = pwdInput.getText(), strMessage = msgInput.getText();
				int index = hideModeCombo.getSelectedIndex();
				
				// if a text file is selected and mode = LSB(text), overwrite the msgInput text
				if(index == 1 && Menu.checkFileExtension(msgFileName, new String[]{".txt"}) )	
					strMessage = new String(OpenCV.readFile(msgFileName));	
				
				if(strMessage.indexOf("\\") != -1)
				{
					Menu.infoBox("Character \\ is not permited. Please change your message.");
					return;
				}
				
				if((index == 0 && (covFileName == null || msgFileName == null)) ||
					(index == 1 && strMessage.length() == 0) )
				{
					Menu.infoBox("You must select both cover and message file.");
					return;
				}
				cov = Highgui.imread(covFileName);
				
				if(key.length() <= 3)
				{
					Menu.infoBox("Password can't be shorter than 4 characters.");
					return;
				}
				
				if(index == 0)
				{	
					if(!Menu.checkFileExtension(covFileName, new String[]{".bmp,",".png",".jpg"}))
					{
						msgImg.setIcon(new ImageIcon(Menu.fileImage.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));	
						msgFileName = null;
						Menu.infoBox("Invalid file. Please select a image file as message for Hecht steganography.");
						return;
					}
					msg = Highgui.imread(msgFileName);
					rez = OpenCV.hideImgHecht(cov, msg, key);
				}
				else if(index == 1)						
					rez = OpenCV.hideImgText(cov, strMessage, key);			
				else if(index == 2)
					rez = OpenCV.hideLosslessFile(cov, OpenCV.readFile(msgFileName), key);
				
				if(rez.rows() == 1)
				{
					Menu.infoBox("The message file is bigger than cover file or an empty message file has been selected.\n"
							+ "For Hecht the required cover must be 3 times bigger than the message.");				
					return;
				}
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(defDir));
				
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					key = fc.getSelectedFile().toString();  // if no extension or an invalid one is provided, assign .png	
					if(!Menu.checkFileExtension(key, new String[]{".png",".bmp"}))
						key += ".png";	
					
					Highgui.imwrite(key, rez);	
					title.setText("Image hidden succesfully!");
					
					Desktop dt = Desktop.getDesktop();	// compare original cover with embedded cover
				    try{ 
				       dt.open(new File(covFileName));
					   dt.open(new File(key));					  
					}
				   catch(IOException ex){}
				}	
				
			}
		});
		getContentPane().add(panel);
	}
}
