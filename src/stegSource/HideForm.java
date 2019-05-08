package stegSource;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

@SuppressWarnings("serial")
public class HideForm extends JFrame {
	String covFileName, msgFileName, memoMsgStr = "";
	
	
	HideForm() 
	{		
		Menu m = new Menu();
		int[] imgSize = m.getXY();
		float scale = m.getUnivScale();
		m = null;
		
		setTitle("StegLSB");
		setSize((int)(790*scale), (int)(460*scale));
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);
		JLabel title = new JLabel("Hide data");
				
		JButton covBtn = new JButton("Select cover");
		JButton msgBtn = new JButton("Select message");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");
		JLabel msgTxt = new JLabel("<html><br/><br/></html>");
			
		JLabel covImg = new JLabel();	
		JLabel msgImg = new JLabel();
		
		JLabel modeTxt = new JLabel("Mode:");
		JLabel pwdTxt = new JLabel("Password:");
		
		JComboBox<String> hideModeCombo = new JComboBox<>(new String[]{"Hecht", "LSB(text)", "Lossless"});		
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm");
		
		JTextArea msgInput = new JTextArea();	// LSB TEXT MODE
		
		Font font = new Font("Consolas", Font.BOLD, (int)(14*scale));		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);	
		
		// title
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridwidth = 3;
		title.setFont(font.deriveFont(Font.BOLD, 22*scale));
		panel.add(title, gbc);
		
		// select cover button	
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;	
		covBtn.setFont(font);			
		panel.add(covBtn,gbc);		
		
		// select message button
		gbc.gridx = 2;
		gbc.gridy = 1;
		msgBtn.setFont(font);
		panel.add(msgBtn,gbc);
		
		// cover label(below cover button)		
		gbc.gridx = 0;
		gbc.gridy = 2;	
		covTxt.setFont(font.deriveFont(Font.BOLD, 15*scale));
		panel.add(covTxt, gbc);

		// message label(below message button)
		gbc.gridx = 2;
		gbc.gridy = 2;		
		msgTxt.setFont(font.deriveFont(Font.BOLD, 15*scale));		
		panel.add(msgTxt, gbc);			
		
		// image cover repres.			
		gbc.weighty = 1;
		gbc.weightx = 1;		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.PAGE_START;		
		ImageIcon icon = new ImageIcon(Menu.noImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
		covImg.setIcon(icon);
		panel.add(covImg,gbc);
		
		// text input message (text area from LSB(text) mode)	
		gbc.gridx = 2;				// LSB(text) mode -> showing textArea
		gbc.gridy = 4;		    			    			    		
		msgInput.setPreferredSize(new Dimension(imgSize[0], imgSize[1]));
		msgInput.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
		msgInput.setLineWrap(true);
		msgInput.setFont(font);
		msgInput.setVisible(false);		
		panel.add(msgInput, gbc);
		
		// image message repres.	
		gbc.gridx = 2;				// Hecht mode -> showing message image
		gbc.gridy = 4;	
		msgImg.setIcon(icon);		// declared at image cover
		icon.getImage().flush();	// garbage collector
		icon = null;	
		panel.add(msgImg,gbc);
		
		// label hide mode
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.PAGE_END;
		modeTxt.setFont(font.deriveFont(Font.BOLD, 17*scale));	
		panel.add(modeTxt,gbc);
		
		// password label
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 5;		
		pwdTxt.setFont(font.deriveFont(Font.BOLD, 17*scale));
		panel.add(pwdTxt, gbc);
		
		// password input text field
		gbc.gridx = 0;
		gbc.gridy = 6;	
		gbc.anchor = GridBagConstraints.PAGE_START;
		pwdInput.setFont(font);
		pwdInput.setPreferredSize(new Dimension((int)(210*scale),(int)(25*scale)));	
		panel.add(pwdInput, gbc);		
		
		// combobox hide mode	
		gbc.gridwidth = 1;		
		gbc.gridx = 0;
		gbc.gridy = 6;		
		hideModeCombo.setSelectedIndex(0);
		hideModeCombo.setFont(font);		
		panel.add(hideModeCombo, gbc);
			
		// confirm button	
		gbc.gridx = 2;
		gbc.gridy = 6;	
		gbc.weighty = 3;		
		confirmBtn.setFont(font.deriveFont(Font.BOLD, 15*scale));	
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
		    		msgTxt.setText("<html>Select a .txt file or<br/> enter your message here:</html>");		        			    		
		        }	        
		    }
		});
		
		covBtn.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent e) 
			{	
				title.setText("Hide data");
				JFileChooser fc = new JFileChooser();						
				if(Menu.defDir != null)
					fc.setCurrentDirectory(new File(Menu.defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					BufferedImage img;
					ImageIcon icon = null;
					int sizeImg = 0;					
					
					if(!Menu.checkFileExtension(fc.getSelectedFile().toString(), new String[]{".png",".bmp"}))
					{
						Menu.infoBox("Invalid file. Please select a .PNG or .BMP as your cover.");
						return;
					}					
					try {
						img = ImageIO.read(fc.getSelectedFile());						
						sizeImg = img.getHeight() * img.getWidth() * 3;
						icon = new ImageIcon(img.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						covImg.setIcon(icon);						
					}
					catch (Exception e1) { }
					finally { 					// garbage collector
						icon.getImage().flush(); 
						icon = null;
						img = null;
					}
					
					covTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								  "</font><br/>Size: "+NumberFormat.getInstance().format(sizeImg)+" bytes</html>");	
					
					covFileName = fc.getSelectedFile().toString();
					Menu.defDir = fc.getCurrentDirectory().toString();
				}
				System.gc();
			}
		});
		
		msgBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				title.setText("Hide data");
				JFileChooser fc = new JFileChooser();					
				if(Menu.defDir != null)
					fc.setCurrentDirectory(new File(Menu.defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					BufferedImage img;
					ImageIcon icon = null;
					long sizeFile = 0;
					int index = hideModeCombo.getSelectedIndex();
					
					try {						
    					img = ImageIO.read(fc.getSelectedFile());
						sizeFile = img.getHeight() * img.getWidth() * 3;
						icon = new ImageIcon(img.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						msgImg.setIcon(icon);
					} 
					catch (Exception ex) 
					{
						if(index == 0)
						{
							Menu.infoBox("Invalid file. Please select a image as your message for\nHecht steganography or choose Lossless hiding mode.");
							return;
						}
						else if(index == 1 && !Menu.checkFileExtension(fc.getSelectedFile().toString(), new String[]{".txt"}))
						{
							Menu.infoBox("Invalid file. A text file is required for LSB hiding mode.");
							return;	
						}														
						else if(index == 2)
						{	
							icon = new ImageIcon(Menu.fileImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
							msgImg.setIcon(icon);	
						}						
					}	
					finally { 					// garbage collector
						icon.getImage().flush(); 
						icon = null;
						img = null;
					}
					
					if(index == 1 || index == 2)	
						sizeFile = fc.getSelectedFile().length();

					msgTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								   "</font><br/>Size: "+NumberFormat.getInstance().format(sizeFile)+" bytes</html>");	
					
					msgFileName = fc.getSelectedFile().toString();	
					memoMsgStr = msgTxt.getText();					
					Menu.defDir = fc.getCurrentDirectory().toString();
				}
				System.gc();
			}
		});
		
		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Mat cov, rez = null;
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
								
				if(key.length() < 4)
				{
					Menu.infoBox("Passwords can't be shorter than 4 characters.");
					return;
				}
				
				if( ((index == 0 || index == 2) && (covFileName == null || msgFileName == null)) || 
				     (index == 1 && strMessage.length() == 0) )
				{
					Menu.infoBox("You must select both cover and message file.");
					return;
				}
				cov = Highgui.imread(covFileName);
					
				if(index == 0)
				{	
					if(!Menu.checkFileExtension(msgFileName, new String[]{".bmp",".png",".jpg"}))
					{	
						
						msgFileName = null;
						ImageIcon icon = new ImageIcon(Menu.noImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						msgImg.setIcon(icon);
						icon.getImage().flush();	// gc
						icon = null;	// gc
						msgTxt.setText("");												
						Menu.infoBox("Invalid file. Please select a image file as message for Hecht steganography.");
						return;
					}
					rez = OpenCV.hideImgHecht(cov, Highgui.imread(msgFileName), key);
				}
				else if(index == 1)						
					rez = OpenCV.hideImgText(cov, strMessage, key);			
				else if(index == 2)
					rez = OpenCV.hideLosslessFile(cov, OpenCV.readFile(msgFileName), key, Menu.getFileExtension(msgFileName));
				
				if(rez.rows() == 1)
				{
					Menu.infoBox("The message file is bigger than cover file or an empty message file has been selected.\n"
							+ "For Hecht the required cover must be 3 times bigger than the message.");				
					return;
				}
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Menu.defDir));
				
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					// using [key] as the selected file name
					key = fc.getSelectedFile().toString();  // if no extension or an invalid one is provided, assign .png	
					if(!Menu.checkFileExtension(key, new String[]{".png",".bmp"}))
						key += ".png";	
					
					Highgui.imwrite(key, rez);	
					Menu.infoBox("Image hidden succesfully!");
					
					try{
						Desktop dt = Desktop.getDesktop();	// compare original cover with embedded cover 					
						dt.open(new File(covFileName));
						dt.open(new File(key));
					}
					catch(IOException ex){  }
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	Menu.owner.toFront();            	
            }
        });
	}

}
