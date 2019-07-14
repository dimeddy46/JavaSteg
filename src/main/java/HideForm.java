
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

@SuppressWarnings("serial")
public class HideForm extends JFrame {
	
	private String covFileName, msgFileName, memoMsgStr = "";
	private BufferedImage imgCovRepr = null, imgMsgRepr = null;
	private ImageIcon iconCov = null, iconMsg = null;
	
	//------------------------------- HideForm(0) => EMBED DATA IN IMAGES ----------------------------
	//------------------------------- HideForm(1) => CHECK IMAGE WATERMARK ---------------------------	
	HideForm(int mode) 	
	{		
		Menu m = new Menu();
		int[] imgSize = m.getXY();
		float scale = m.getUnivScale();
		m = null;

		setTitle("JavaSteg");
		setSize((int)(790*scale), (int)((mode == 0?460:440)*scale));
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);
		JLabel title = new JLabel(mode == 0?"Hide data" : "Verify watermarked images");
				
		JButton covBtn = new JButton("Select cover");
		JButton msgBtn = new JButton("Select message");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");
		JLabel msgTxt = new JLabel(mode == 0?"<html><br/><br/></html>":"Output:");
			
		JLabel covImg = new JLabel();	
		JLabel msgImg = new JLabel();
		
		JLabel modeTxt = new JLabel("Mode:");
		JLabel pwdTxt = new JLabel("Password:");
		
		JComboBox<String> hideModeCombo = new JComboBox<>(new String[]{"Hecht(img)", "Text", "All files"});		
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm"), confirmCheckWaterBtn = new JButton("Confirm | Update");
		
		JTextArea msgInput = new JTextArea();	// LSB TEXT MODE
		
		Font font = new Font("Consolas", Font.BOLD, (int)(14*scale));		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);	
		
		// title
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridwidth = 3;
		title.setFont(font.deriveFont(22*scale));
		panel.add(title, gbc);
		
		gbc.gridwidth = 1;
			
		// select cover button	
		if(mode == 0){
			gbc.gridx = 0;
			gbc.gridy = 1;
		}
		else {
			gbc.gridx = 0;
			gbc.gridy = 5;	
			gbc.weighty = 5;
		}			
		covBtn.setFont(font);			
		panel.add(covBtn,gbc);	
			
		// select message button | confirm button from watermark
		if(mode == 0){
			gbc.gridx = 2;
			gbc.gridy = 1;
			msgBtn.setFont(font);
			panel.add(msgBtn,gbc);
		}
		else {
			gbc.gridx = 2;
			gbc.gridy = 5;	
			confirmCheckWaterBtn.setFont(font);
			panel.add(confirmCheckWaterBtn,gbc);
		}

		gbc.weighty = mode == 0? 0 : 1;
		gbc.anchor = GridBagConstraints.PAGE_END;
		
		// cover label(below cover button)	
		gbc.gridx = 0;
		gbc.gridy = 2;	
		covTxt.setFont(font.deriveFont(15*scale));
		panel.add(covTxt, gbc);

		// message label(below message button)
		gbc.gridx = 2;
		gbc.gridy = 2;		
		msgTxt.setFont(font.deriveFont(15*scale));		
		panel.add(msgTxt, gbc);			
					
		// image cover repres.	
		gbc.weighty = 1;
		gbc.weightx = 1;		
		gbc.gridx = 0;
		gbc.gridy = 4;	
		gbc.anchor = GridBagConstraints.PAGE_START;	
		iconCov = new ImageIcon(Menu.noImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
		covImg.setIcon(iconCov);
		panel.add(covImg,gbc);
		
		// message input (text area from LSB(text) mode)	
		gbc.gridx = 2;				// LSB(text) mode -> showing textArea
		gbc.gridy = 4;		    			    			    				
		msgInput.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
		msgInput.setLineWrap(true);
		msgInput.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane (msgInput);
		scroll.setPreferredSize(new Dimension(imgSize[0], imgSize[1]));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);		
		panel.add(scroll,gbc);
				
		if(mode == 0)				// set parameters for "Hide data" window
		{
			msgInput.setFont(font);
			msgInput.setVisible(false);	
			scroll.setVisible(false);
			
			// image message repres.	
			gbc.gridx = 2;				// Hecht mode -> showing message image
			gbc.gridy = 4;	
			msgImg.setIcon(iconCov);		// initialised at covImg
			iconCov.getImage().flush();	// garbage collector
			iconCov = null;	
			panel.add(msgImg,gbc);
		
			// label hide mode
			gbc.gridx = 0;
			gbc.gridy = 5;
			gbc.anchor = GridBagConstraints.PAGE_END;
			modeTxt.setFont(font.deriveFont(17*scale));	
			panel.add(modeTxt,gbc);
			
			// password label
			gbc.gridwidth = 3;
			gbc.gridx = 0;
			gbc.gridy = 5;		
			pwdTxt.setFont(font.deriveFont(17*scale));
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
			confirmBtn.setFont(font.deriveFont(15*scale));	
			panel.add(confirmBtn, gbc);
		}
		else				// set parameters for "Check watermark" window
		{
			msgInput.setFont(font.deriveFont(12*scale));
			msgInput.setEditable(false);
			msgInput.setVisible(true);
		}
		
		hideModeCombo.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) 
		    {	
		    	int index = hideModeCombo.getSelectedIndex(), canHide = 0;
		        if(index == 0 || index == 2)
		        {
		        	scroll.setVisible(false);
		        	msgInput.setVisible(false);		        	
		        	msgImg.setVisible(true);
		        	msgTxt.setText(memoMsgStr);
		        	
		        	if(imgCovRepr != null)
		        		canHide = index == 0? 
		        				imgCovRepr.getHeight() * imgCovRepr.getWidth() :	
		        				imgCovRepr.getHeight() * imgCovRepr.getWidth() *3;		        		
		        }
		        else if(index == 1)
		        {		        	
		        	scroll.setVisible(true);
		        	msgInput.setVisible(true);
		        	msgImg.setVisible(false);	
		        	memoMsgStr = msgTxt.getText();
		    		msgTxt.setText("<html>Select a .txt file or<br/> enter your message here:</html>");		    
		        	if(imgCovRepr != null)
		        		canHide = (imgCovRepr.getHeight() * imgCovRepr.getWidth() * 3) / 8;			
		        }
		        if(imgCovRepr != null)
			        covTxt.setText("<html>File: <font color='red'>"+
			        		covFileName.substring( covFileName.lastIndexOf("\\")+1, covFileName.length())+ 
			        		"</font><br/>Hides: "+NumberFormat.getInstance().format(canHide)+" bytes</html>");	
		    }
		});
		
		covBtn.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent e) 
			{					
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Menu.defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					int canHide = 0, index;					
					File selectedFile = fc.getSelectedFile();
					String covForWhat = "";
					if(!Menu.checkFileExtension(selectedFile.toString(), ".jpg.jpeg.png.bmp"))
					{
						Menu.infoBox("Please select a image as your cover.");
						return;
					}					
					try {
						imgCovRepr = ImageIO.read(selectedFile);	
						iconCov = new ImageIcon(imgCovRepr.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						covImg.setIcon(iconCov);	
						
						if(mode == 0)
						{
							covForWhat = "Hides";
							index = hideModeCombo.getSelectedIndex();
							if(index == 0)
								canHide = imgCovRepr.getHeight() * imgCovRepr.getWidth();
							else if(index == 1)
								canHide = (imgCovRepr.getHeight() * imgCovRepr.getWidth() * 3) / 8;	
							else
								canHide = imgCovRepr.getHeight() * imgCovRepr.getWidth() * 3;					
						}
						else {
							canHide = (int)selectedFile.length();
							covForWhat = "Size";
						}
					}
					catch (Exception ex) 
					{
						Menu.infoBox("This file is invalid.");
						return;
					}
					finally { 	
						if(iconCov != null)		// gc
						{
							iconCov.getImage().flush();
							iconCov = null;
						}
						Menu.defDir = selectedFile.getParentFile().toString();
					}
					
					covTxt.setText("<html>File: <font color='red'>"+selectedFile.getName()+
								  "</font><br/>"+covForWhat+": "+NumberFormat.getInstance().format(canHide)+" bytes</html>");						
					covFileName = selectedFile.toString();					
				}
				System.gc();
			}
		});
		
		msgBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();					
				fc.setCurrentDirectory(new File(Menu.defDir));				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					int imgBytes = 0;
					File selectedFile = fc.getSelectedFile();
					
					try {						
    					imgMsgRepr = ImageIO.read(selectedFile);
    					imgBytes = imgMsgRepr.getHeight() * imgMsgRepr.getWidth() * 3;
    					iconMsg = new ImageIcon(imgMsgRepr.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						msgImg.setIcon(iconMsg);
						
						if(hideModeCombo.getSelectedIndex() != 2)
							hideModeCombo.setSelectedIndex(0);
					}
					catch (NullPointerException ex) 
					{	
						if(selectedFile.length() == 0)
						{
							Menu.infoBox("This file is invalid.");
							return;
						}
						
						if(Menu.checkFileExtension(selectedFile.toString(), ".txt"))
							hideModeCombo.setSelectedIndex(1);
						else 
							hideModeCombo.setSelectedIndex(2);	
						
						iconMsg = new ImageIcon(Menu.fileImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						msgImg.setIcon(iconMsg);	
						imgBytes = (int)selectedFile.length();
					}	
					catch (ArrayIndexOutOfBoundsException ex2)
					{
						Menu.infoBox("This file is invalid.");
						return;
					}
					catch(Exception ex3){ }
					finally 
					{ 					// garbage collector
						if(iconMsg != null)
						{
							iconMsg.getImage().flush(); 
							iconMsg = null;
						}
						Menu.defDir = selectedFile.getParentFile().toString();
					}

					msgTxt.setText("<html>File: <font color='red'>"+selectedFile.getName()+
								   "</font><br>Size: "+NumberFormat.getInstance().format(imgBytes)+" bytes</html>");						
					msgFileName = selectedFile.toString();	
					memoMsgStr = msgTxt.getText();					
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
				if(index == 1 && Menu.checkFileExtension(msgFileName, ".txt") )	
					strMessage = new String(OpenCV.readFile(msgFileName));	

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
				
				if(strMessage.contains("\\"))
				{
					strMessage.replace("\\", "");
					Menu.infoBox("All '\' characters have been replaced.");
				}		
				
				cov = Highgui.imread(covFileName);
					
				if(index == 0)
				{	
					if(!Menu.checkFileExtension(msgFileName, ".bmp.png.jpg"))
					{	
						Menu.infoBox("Invalid file. Please select a image file as message for Hecht mode.");					
						msgFileName = null;
						msgTxt.setText("");	
						
						iconMsg = new ImageIcon(Menu.noImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						msgImg.setIcon(iconMsg);
						iconMsg.getImage().flush();	// gc
						iconMsg = null;	
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
					Menu.infoBox("The message file is bigger than cover file or an empty message file has been selected.<br>"
							   + "Hecht requires a 3 times bigger cover than message.");				
					return;
				}
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Menu.defDir));
				
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					key = fc.getSelectedFile().toString();  // if no extension or an unsupported one is provided, assign .png	
					if(index == 2 && Menu.checkFileExtension(key, ".bmp"))	// can't write 16 bit images on BMP
						key = key.replace(".bmp", ".png");
					
					if(!Menu.checkFileExtension(key, ".png.bmp"))
						key += ".png";	
					
					Highgui.imwrite(key, rez);	
					Menu.infoBox("Image hidden succesfully!");
					Menu.defDir = fc.getCurrentDirectory().toString();
					
					try{
						Desktop dt = Desktop.getDesktop();	// compare original cover with embedded cover 					
						dt.open(new File(covFileName));
						dt.open(new File(key));
					}
					catch(IOException ex){  }
				}
			}
		});
		
		confirmCheckWaterBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{			
				if(covFileName == null)
				{
					Menu.infoBox("Please select a cover image.");
					return;
				}
				
				File selectedFile = new File(covFileName);
				int imgBytes;
				
				try {						
					imgCovRepr = ImageIO.read(selectedFile);		// update image representation and size
					iconCov = new ImageIcon(imgCovRepr.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
					covImg.setIcon(iconCov);
					imgBytes = (int)selectedFile.length();
					covTxt.setText("<html>File: <font color='red'>"+selectedFile.getName()+
							  "</font><br/>Size: "+NumberFormat.getInstance().format(imgBytes)+" bytes</html>");	
				}
				catch(Exception ex)
				{					
					iconCov = new ImageIcon(Menu.noImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));	
					covImg.setIcon(iconCov);
					covTxt.setText("<html><br/><br/></html>");
					Menu.infoBox("The selected file is not available anymore.");
					return;
				}
				finally
				{					
					if(iconCov != null)		// GC
					{
						iconCov.getImage().flush(); 
						iconCov = null;
					}
				}
				
				Mat cov = Highgui.imread(covFileName);	
				
		/*		int numThr = Runtime.getRuntime().availableProcessors();
				numThr = numThr >= 4? 4 : 2;				
				int cpt = 0;
				Thread[] th = new Thread[numThr];	// start threads for faster computation
				for(int i = 0;i<numThr;i++,cpt= cpt + 8 / numThr)
				{
					th[i] = new Watermark(cov, cpt, cpt + 8 / numThr);
					th[i].start();
				}
				try{							
					for(i = 0;i<numThr;i++)
						th[i].join();
				}
				catch(InterruptedException ex){}*/
				
				Watermark.getStatistics(Watermark.extDCT(cov,0,0));
				msgInput.setText(Watermark.stats);
				Watermark.stats = "";
				Watermark.probability = -1;
			}
		});
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	Menu.owner.toFront();            	
            }
        });
	}

}
