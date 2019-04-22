package stegSource;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import java.awt.GridBagLayout;
import java.awt.Image;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class ExtractForm extends JFrame {	
	String covFileName;	
	
	ExtractForm() 
	{			
		Menu m = new Menu();
		int[] imgSize = m.getXY();
		float scale = m.getUnivScale();
		m = null;
		
		setTitle("StegLSB");
		setSize((int)(650*scale), (int)(455*scale));
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);
		JLabel title = new JLabel("Extract hidden data");				
		
		JButton covBtn = new JButton("Select cover");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");		
		JLabel covImg = new JLabel();
		
		JLabel pwdTxt = new JLabel("Password:");
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm");	
		
		Font font = new Font("Consolas", Font.BOLD, (int)(14*scale));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);
		
		// title	
		gbc.weighty = 0;			
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		title.setFont(font.deriveFont(Font.BOLD, 22*scale));
		panel.add(title, gbc);		
		
		// cover button	
		gbc.gridx = 0;
		gbc.gridy = 1;	
		covBtn.setFont(font);			
		panel.add(covBtn,gbc);
		
		// cover label(below cover button)	
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;	
		gbc.anchor = GridBagConstraints.PAGE_START;
		covTxt.setFont(font.deriveFont(Font.BOLD, 15*scale));
		panel.add(covTxt, gbc);
		
		// cover image repres.	
		
		gbc.weighty = 20;	
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.PAGE_START;		
		ImageIcon icon = new ImageIcon(Menu.noImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
		covImg.setIcon(icon);
		icon.getImage().flush();	// garbage collector
		icon = null;	
		panel.add(covImg,gbc);		

		// password label
		gbc.weighty = 50;	
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		pwdTxt.setFont(font.deriveFont(Font.BOLD, 17*scale));
		panel.add(pwdTxt, gbc);
		
		// password text field
		gbc.gridx = 0;
		gbc.gridy = 4;	
		gbc.anchor = GridBagConstraints.WEST;
		pwdInput.setPreferredSize(new Dimension((int)(210*scale),(int)(25*scale)));
		pwdInput.setFont(font);
		panel.add(pwdInput, gbc);
		
		// confirm button
		gbc.gridx = 0;
		gbc.gridy = 4;	
		gbc.anchor = GridBagConstraints.EAST;		
		confirmBtn.setFont(font.deriveFont(Font.BOLD, 15*scale));	
		panel.add(confirmBtn, gbc);
		
		covBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				JFileChooser fc = new JFileChooser();						
				if(Menu.defDir != null)
					fc.setCurrentDirectory(new File(Menu.defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					BufferedImage img = null;				
					ImageIcon icon = null;
					try {
						img = ImageIO.read(fc.getSelectedFile());	
						icon = new ImageIcon(img.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						covImg.setIcon(icon);				
					} 
					catch (Exception e1) { 
						Menu.infoBox("Invalid file. Please select an image as your cover.");
						return;
					}	
					finally { 					// garbage collector
						icon.getImage().flush(); 
						icon = null;
						img = null;
					}
					
					covFileName = fc.getSelectedFile().toString();
					covTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								  "</font><br/>Size: "+Menu.addCommas(fc.getSelectedFile().length())+" bytes</html>");	
					
					Menu.defDir = fc.getCurrentDirectory().toString();
				}
				System.gc();
			}			
		});
		
		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				boolean found = false;
				JFileChooser fc = new JFileChooser();
				String key = pwdInput.getText(), msgStr = null, fileName = null;			
				byte[] msgByte;
				
				if(covFileName == null)
				{
					Menu.infoBox("Please select a cover image.");
					return;
				}
				
				if(key.length() < 4)
				{
					Menu.infoBox("Passwords can't be shorter than 4 characters.");
					return;
				}	
				
				Mat cov = Highgui.imread(covFileName), msgMat;

				if((msgMat = OpenCV.extImgHecht(cov, key)).cols() != 1)
				{
					Menu.infoBox("A hidden IMAGE has been found. Please select a save location.");
					if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
					{
						 fileName = fc.getSelectedFile().toString();
						 if(!Menu.checkFileExtension(fileName, new String[]{".png",".bmp",".jpg"}))
							 fileName += ".png";						 
						 Highgui.imwrite(fileName, msgMat);
						 found = true;
					}
					else return;
				}
				else if((msgStr = OpenCV.extImgText(cov, key)) != "\\pwdincorect")
				{
					Menu.infoBox("A hidden TEXT file has been found. Please select a save location.");
					if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
					{
						 fileName = fc.getSelectedFile().toString();
						 if(!Menu.checkFileExtension(fileName, new String[]{".txt"}))
							 fileName += ".txt";	
						 
						 OpenCV.writeFile(fileName, msgStr.getBytes());
						 found = true;
					}
					else return;
				}
			
				cov = Highgui.imread(covFileName, -1); 	// read 16 bit images(can't use convertTo, hidden data would be lost)
				if(cov.depth() != 0 && (msgByte = OpenCV.extLosslessFile(cov, key)) != null )
				{
					String fileType = OpenCV.getExt();
					Menu.infoBox("A hidden "+fileType.toUpperCase()+" file has been found. Please select a save location.");
					if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
					{
						 fileName = fc.getSelectedFile().toString()+fileType;						 				 
						 OpenCV.writeFile(fileName, msgByte);
						 found = true;
					}
					else return;
				}
				
				if(found == true)
					Menu.infoBox("The file has been extracted succesfully!");				
				else 
					Menu.infoBox("The password is invalid or no data is hidden inside the file.");
			}
		});
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	Menu.owner.toFront();
            }
        });
	}	
	
}
