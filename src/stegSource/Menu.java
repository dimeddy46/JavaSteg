package stegSource;


import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.opencv.core.Core;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import resources.ResourceLoader;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;

@SuppressWarnings("serial")
public class Menu extends JFrame {

	final static Image noImage = ResourceLoader.loadImage("no-image-selected2.png"),
				 fileImage = ResourceLoader.loadImage("file.png");
	static String defDir;
	static Window owner;
	int[] imgSize = {350, 210};
	float univScale = 0;		
		
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {				
				try {
					 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);	
				     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    } 
				catch (UnsatisfiedLinkError e) {
					infoBox(Core.NATIVE_LIBRARY_NAME+" library has not been found or is invalid.");
					return;
				}
			    catch (UnsupportedLookAndFeelException | ClassNotFoundException | 
			    		InstantiationException | IllegalAccessException e)  {  } 
				
				new Menu().setVisible(true);		
			}
		});
	}
	
	int[] getXY(){
		return imgSize;
	}	
	float getUnivScale(){
		return univScale;
	}
	
	static void infoBox(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Information", JOptionPane.INFORMATION_MESSAGE);           
    }
	
	static String addCommas(long n)	// converts an number to a string with commas after every 3 digits
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
	
	static String getFileExtension(String file)
	{
		if(file == null)
			return "";		
		int x = file.lastIndexOf(".");		
		if(x == -1)
			return "";
		
		return file.toLowerCase().substring(x, file.length());
	}
	
	static boolean checkFileExtension(String file, String[] exts)
	{
		file = getFileExtension(file);		
		for(String s : exts)
			if(file.equals(s))
				return true;
		return false;
	}

	Menu() 
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		univScale = (float) (screenSize.getWidth() > 1440? screenSize.getWidth() / 1440 : 1.0);
		imgSize[0] *= (int)univScale;
		imgSize[1] *= (int)univScale;
		setTitle("StegLSB");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize((int)(400*univScale),(int)(300*univScale));
		setLocationRelativeTo(null);
		
		JLabel title = new JLabel("Menu");
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);			
				
		JButton hideBtn = new JButton("Hide file");
		JButton extBtn = new JButton(" Extract ");
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);
				
		title.setFont(new Font("Consolas", Font.BOLD, (int)(20*univScale)));
		gbc.gridx = 1;
		gbc.gridy = 0;		
		panel.add(title, gbc);
		gbc.weighty = 1;
				
		hideBtn.setFont(new Font("Consolas", Font.BOLD, (int)(17*univScale)));
		gbc.gridx = 0;
		gbc.gridy = 2;		
		panel.add(hideBtn,gbc);
				
		extBtn.setFont(new Font("Consolas", Font.BOLD, (int)(17*univScale)));
		gbc.gridx = 2;
		gbc.gridy = 2;	
		panel.add(extBtn,gbc);

		hideBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{		
				HideForm x = new HideForm();			
				x.setVisible(true);	
				x = null;
			}
		});
		
		extBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				ExtractForm x = new ExtractForm();
				x.setVisible(true);
				x = null;
			}
		});
		addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) 
            {
            	//used to regain focus to Menu
            	owner = javax.swing.FocusManager.getCurrentManager().getActiveWindow();		
            }
        });
	}

}
