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
	
	private static void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
	
	
	private static String toMillions(long n)	// converts an number to a string with commas after every 3 digits
	{
		StringBuilder str = new StringBuilder(Long.toString(n));
		String rez = "";
		int i, ct = 3, len = str.length();
		
		for(i = 0;i < len / 3; i++)
		{
			rez = ","+str.substring(len-ct, len-ct+3) + rez;
			ct += 3;
		}
		
		rez = str.substring(0, len % 3) + rez;		
		if(rez.charAt(0) == ',')
			return rez.substring(1,rez.length());
		return rez;
	}
	
	public ExtractForm() 
	{		
		setTitle("StegLSB");
		setSize(650, 455);
		setResizable(false);	
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel title = new JLabel("Extract data from image");				
		
		JButton covBtn = new JButton("Select cover");
		JLabel covTxt = new JLabel("<html><br/><br/></html>");		
		JLabel covImg = new JLabel();
		
		JLabel pwdTxt = new JLabel("Password:");
		JTextField pwdInput = new JTextField(); 
		JButton confirmBtn = new JButton("Confirm");	
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3,3,3,3);
		
		gbc.weighty = 1;			
		gbc.gridx = 0;
		gbc.gridy = 0;	
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.PAGE_START;
		title.setFont(new Font("Consolas", Font.BOLD, 20));
		panel.add(title, gbc);		
		
		gbc.weighty = 2;
		gbc.gridwidth = 1;
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
						infoBox("Invalid file. Please select an image as your cover.");
						return;
					}	
					
					covFileName = fc.getSelectedFile().toString();
					covTxt.setText("<html>File: <font color='red'>"+fc.getSelectedFile().getName()+
								  "</font><br/>Size: "+toMillions(fc.getSelectedFile().length())+" bytes</html>");	
					
					defDir = fc.getCurrentDirectory().toString();
				}
			}
		});
		

		confirmBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				String key = pwdInput.getText(), msg;
				Mat cov = Highgui.imread(covFileName),msg2;
				byte[] msg3;
				
				if((msg = OpenCV.extImgText(cov, key)) != "\\pwdincorect")
				{
					System.out.println("GASIT ASCUNS TEXT:"+msg);
					return;
				}
				if((msg2 = OpenCV.extImgHecht(cov, key)).cols() != 1)
				{
					System.out.println("GASIT");
					Highgui.imwrite("hecht1.png", msg2);
					return;
				}
			 
				cov = Highgui.imread(covFileName, -1 ); // read 16 bits images

				if(cov.depth() != 0 && (msg3 = OpenCV.extLosslessFile(cov, key)) != null)
				{
					OpenCV.writeFile("test.exe", msg3);
					return;
				}
				System.out.println("The password is invalid or no data is hidden inside the file.");
			}
		});
	}	
}
