
import java.awt.Desktop;
import java.awt.Dimension;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;

@SuppressWarnings("serial")
public class ExtractForm extends JFrame {	
	
	private String covFileName;	
	private BufferedImage imgCovRepr = null;				
	private ImageIcon iconCov = null;
	
	//------------------------------- ExtractForm(0) => EXTRACT STEGANOGRAPHIED DATA -------------------------
	//------------------------------- ExtractForm(1) => ADD WATERMARK TO IMAGE -------------------------------	
	ExtractForm(int mode) 
	{			
		Menu m = new Menu();
		int[] imgSize = m.getXY();
		float scale = m.getUnivScale();
		m = null;
		
		setTitle("JavaSteg");
		setSize((int)(600*scale), (int)(455*scale));
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);
		JLabel title = new JLabel(mode == 0? "Extract hidden data": "Watermark images");				
		
		JButton covBtn = new JButton("Select cover");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");		
		JLabel covImg = new JLabel();
		
		JLabel pwdTxt = new JLabel(mode == 0? "Password:": "Insert mark:");
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm"), confirmWatermarkBtn = new JButton("Confirm");	
		
		Font font = new Font("Consolas", Font.BOLD, (int)(14*scale));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);
		
		// title	
		gbc.weighty = 0;			
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		title.setFont(font.deriveFont(22*scale));
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
		covTxt.setFont(font.deriveFont(15*scale));
		panel.add(covTxt, gbc);
		
		gbc.weighty = 20;	
		// cover image repres.			
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.PAGE_START;		
		iconCov = new ImageIcon(Menu.noImage.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
		covImg.setIcon(iconCov);
		panel.add(covImg,gbc);		

		// password label
		gbc.weighty = 50;	
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		pwdTxt.setFont(font.deriveFont(17*scale));
		panel.add(pwdTxt, gbc);
		
		// password text field
		gbc.gridx = 0;
		gbc.gridy = 4;	
		gbc.anchor = GridBagConstraints.WEST;
		pwdInput.setPreferredSize(new Dimension((int)(210*scale),(int)(25*scale)));
		pwdInput.setFont(font);
		panel.add(pwdInput, gbc);
		
		// confirm button for "Extract" window and "Watermark"
		gbc.gridx = 0;
		gbc.gridy = 4;	
		gbc.anchor = GridBagConstraints.EAST;	
		
		if(mode == 0){	
			confirmBtn.setFont(font.deriveFont(15*scale));	
			panel.add(confirmBtn, gbc);
		}
		else {
			confirmWatermarkBtn.setFont(font.deriveFont(15*scale));	
			panel.add(confirmWatermarkBtn, gbc);
		}
		
		covBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				JFileChooser fc = new JFileChooser();						
				fc.setCurrentDirectory(new File(Menu.defDir));				
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					File selectedFile = fc.getSelectedFile();
					
					try {
						imgCovRepr = ImageIO.read(selectedFile);	
						iconCov = new ImageIcon(imgCovRepr.getScaledInstance(imgSize[0], imgSize[1], Image.SCALE_SMOOTH));
						covImg.setIcon(iconCov);				
					} 
					catch (Exception ex) 
					{ 
						Menu.infoBox("Invalid file. Please select an image as your cover.");
						return;
					}	
					finally { 			// garbage collector
						if(iconCov != null)
						{
							iconCov.getImage().flush(); 
							iconCov = null;
						}
						Menu.defDir = selectedFile.getParentFile().toString();
					}
					
					covFileName = fc.getSelectedFile().toString();
					covTxt.setText("<html>File: <font color='red'>"+selectedFile.getName()+
								  "</font><br/>Size: "+NumberFormat.getInstance().format(selectedFile.length())+" bytes</html>");			
				}
				System.gc();
			}			
		});
		
		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				String key = pwdInput.getText(), fileName, type;	 
				byte[] msgByte = null;
				byte found = -1;
				
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
					found = 0;
					type = "IMAGE";
				}
				else if(!Arrays.equals(msgByte = OpenCV.extImgText(cov, key).getBytes(), "\\pwdincorect".getBytes())) 
				{
					found = 1;
					type = "TEXT";
				}
				else 
				{
					// read 16 bit images
					cov = Highgui.imread(covFileName, -1); 	
					if(cov.depth() != 0 && (msgByte = OpenCV.extLosslessFile(cov, key)) != null ) 
					{
						found = 2;
						type = OpenCV.getExt();
					}
					else 
					{
						Menu.infoBox("The password is invalid or no data is hidden inside the file.");
						return;
					}
				}
				
				Menu.infoBox("A hidden "+type.toUpperCase()+" has been found. Please select a save location.");	
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Menu.defDir));
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{							 				
					 fileName = fc.getSelectedFile().toString();
					 
					 if(found == 0)				// found Hecht image hidden inside cover 
					 {      	   						 
						 if(!Menu.checkFileExtension(fileName,".png.bmp.jpg"))
							 fileName += ".png";							 
						 Highgui.imwrite(fileName, msgMat);
					 }
					 else  
					 {
						 if(found == 1)			 // found text message
						 {       							 		
							 if(!Menu.checkFileExtension(fileName,".txt"))
								 fileName += ".txt";
						 }
						 else  		    		// found lossless file type(All Files)					
							 fileName = fc.getSelectedFile().toString()+type;						
						 OpenCV.writeFile(fileName, msgByte);						
					 }
					 Menu.defDir = fc.getCurrentDirectory().toString();
					 					 
					 try{
						 Desktop dt = Desktop.getDesktop();		// open folder
						 dt.open(new File(Menu.defDir));
					 }
					 catch(IOException ex){}
				}				
			}
		});
		
		confirmWatermarkBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{				
				String key = pwdInput.getText();	
				
				if(covFileName == null)
				{
					Menu.infoBox("Please select a cover image.");
					return;
				}
				
				if(key.contains("\\"))
				{
					key.replace("\\", "");
					Menu.infoBox("All '\' characters have been replaced.");
				}
				
				if(key.length() < 2 || key.length() > 20)
				{
					Menu.infoBox("Please choose another marking string.");
					return;
				}
				
				Mat cov = Highgui.imread(covFileName);
				cov = Watermark.hideDCT(cov, key);
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(Menu.defDir));
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{	
					key = fc.getSelectedFile().toString();  // if no extension or an unsupported one is provided, assign .png						
					if(!Menu.checkFileExtension(key, ".png.bmp"))
						key += ".png";	
					
					Highgui.imwrite(key, cov);	
					Menu.infoBox("Image watermarked succesfully!");
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
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	if(Menu.owner != null)
            		Menu.owner.toFront();
            }
        });
	}	
	
}
