package stegSource;

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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.awt.GridBagLayout;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExtractForm extends JFrame {
	
	int xImg = 350, yImg = 210;
	String defDir, covFileName;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);	
				     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    } 
				catch (UnsatisfiedLinkError e) {
					System.out.println("DLL");
					return;
				}
			    catch (UnsupportedLookAndFeelException | ClassNotFoundException | 
			    		InstantiationException | IllegalAccessException e)  {  } 
				try{
					new ExtractForm().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ExtractForm() 
	{		
		setTitle("StegLSB");
		setSize(650, 455);
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel title = new JLabel("Extract hidden data");				
		
		JButton covBtn = new JButton("Select cover");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");		
		JLabel covImg = new JLabel();
		
		JLabel pwdTxt = new JLabel("Password:");
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm");	
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);
		
		// title	
		gbc.weighty = 1;			
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		title.setFont(new Font("Consolas", Font.BOLD, 20));
		panel.add(title, gbc);		
		
		// cover button	
		gbc.weighty = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;	
		covBtn.setFont(new Font("Consolas", Font.BOLD, 14));			
		panel.add(covBtn,gbc);
		
		// cover label(below cover button)	
		gbc.weighty = 3;
		gbc.gridx = 0;
		gbc.gridy = 2;	
		gbc.anchor = GridBagConstraints.PAGE_START;
		covTxt.setFont(new Font("Consolas", Font.BOLD, 15));
		panel.add(covTxt, gbc);
		
		gbc.weighty = 50;	
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.PAGE_START;		
		covImg.setIcon(new ImageIcon(Menu.noImage.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
		covImg.setBounds(0, 0, xImg, yImg);
		covImg.setPreferredSize(new Dimension(xImg, yImg));
		panel.add(covImg,gbc);
		getContentPane().add(panel);
		
		gbc.weighty = 100;	
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		pwdTxt.setFont(new Font("Consolas", Font.BOLD, 17));
		panel.add(pwdTxt, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;	
		gbc.anchor = GridBagConstraints.WEST;
		pwdInput.setPreferredSize(new Dimension(180,25));
		pwdInput.setFont(new Font("Consolas", Font.BOLD, 13));
		panel.add(pwdInput, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;	
		gbc.anchor = GridBagConstraints.EAST;		
		confirmBtn.setFont(new Font("Consolas", Font.BOLD, 15));	
		panel.add(confirmBtn, gbc);
		
		covBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				title.setText("Extract hidden data");
				JFileChooser fc = new JFileChooser();						
				if(defDir != null)
					fc.setCurrentDirectory(new File(defDir));
				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					BufferedImage img = null;				

					try {
						img = ImageIO.read(fc.getSelectedFile());				
						covImg.setIcon(new ImageIcon(img.getScaledInstance(xImg, yImg, Image.SCALE_SMOOTH)));
					} 
					catch (Exception e1) { 
						Menu.infoBox("Invalid file. Please select an image as your cover.");
						return;
					}	
					
					covFileName = fc.getSelectedFile().toString();
					covTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								  "</font><br/>Size: "+Menu.toMillions(fc.getSelectedFile().length())+" bytes</html>");	
					
					defDir = fc.getCurrentDirectory().toString();
				}
			}
		});
		

		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				boolean found = false;
				JFileChooser fc = new JFileChooser();
				String key = pwdInput.getText(), msgStr = null, fileName;
				Mat cov = Highgui.imread(covFileName), msgMat;
				byte[] msgByte;
				
				if((msgMat = OpenCV.extImgHecht(cov, key)).cols() != 1)
				{
					Menu.infoBox("A hidden IMAGE has been found.\n\n Please choose a save location.");
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
					Menu.infoBox("A TEXT file has been found. Please choose a save location.");
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
				
				cov = Highgui.imread(covFileName, -1); 
				if(cov.depth() != 0 && (msgByte = OpenCV.extLosslessFile2(cov, key)) != null )
				{
					String fileType = OpenCV.getExt();
					Menu.infoBox("A "+fileType.toUpperCase()+" file has been found. Please choose a save location.");
					if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
					{
						 fileName = fc.getSelectedFile().toString()+fileType;						 				 
						 OpenCV.writeFile(fileName, msgByte);
						 found = true;
					}
					else return;
				}
				
				if(found == true)
					title.setText("<html><font color='green'>The file has been extracted!</font></html>");
				else 
					Menu.infoBox("The password is invalid or no data is hidden inside the file.");
			}
		});
	}	
}
