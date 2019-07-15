
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;

@SuppressWarnings("serial")
public class Menu extends JFrame {
	final static Image noImage = getImage("icons/default-image.png"),
					   fileImage = getImage("icons/document-image.png");
	static String defDir = System.getProperty("user.dir")+"/Samples";
	static Window owner;
	int[] imgSize = {350, 210};
	float univScale = 0;		
		
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {				
				try {
					 nu.pattern.OpenCV.loadShared();
				     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    } 
				catch (UnsatisfiedLinkError e) {
					infoBox("Could not load OpenCV shared library. Aborting.");
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
	
	// used to embed icons in final .jar file
	public static Image getImage(final String pathAndFileName) 
	{		
	    final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
	    return Toolkit.getDefaultToolkit().getImage(url);
	}
	
	static void infoBox(String infoMessage)
    {
		String message = "<html><body><div align='center'>"+infoMessage+"</div></body></html>";
		JLabel messageLabel = new JLabel(message);
		messageLabel.setFont(new Font("Consolas",Font.BOLD,14));
		JOptionPane.showConfirmDialog(null, messageLabel, "Information", JOptionPane.DEFAULT_OPTION);     
    }
	
	static String getFileExtension(String file)
	{
		if(file == null)
			return "";		
		int afterDot = file.lastIndexOf(".");
		if(afterDot == -1)
			return "";
		
		return file.toLowerCase().substring(afterDot, file.length());
	}
	
	static boolean checkFileExtension(String file, String exts)
	{
		file = getFileExtension(file);
		if(file.length() != 0 && exts.contains(file))
			return true;
		return false;
	}

	Menu() 
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		univScale = (float) (screenSize.getWidth() > 1440? screenSize.getWidth() / 1440 : 1.0);
		imgSize[0] *= univScale;
		imgSize[1] *= univScale;

		setTitle("JavaSteg");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize((int)(400*univScale),(int)(260*univScale));
		setLocationRelativeTo(null);
		
		Font font = new Font("Consolas",Font.BOLD,(int)(17*univScale));
		JLabel title = new JLabel("Menu");
		JPanel panel = new JPanel(new GridBagLayout());
		getContentPane().add(panel);			
				
		JButton hideBtn = new JButton("Hide data");
		JButton extBtn = new JButton(" Extract ");
		JButton markBtn = new JButton("Watermark");
		JButton extMarkBtn = new JButton("Check mark");
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,0,10,0);
				
		title.setFont(font.deriveFont(20*univScale));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;		
		panel.add(title, gbc);
		
		gbc.weighty = 1;
		
		hideBtn.setFont(font);		
		gbc.gridx = 0;
		gbc.gridy = 2;		
		panel.add(hideBtn,gbc);
				
		extBtn.setFont(font);
		gbc.gridx = 2;
		gbc.gridy = 2;	
		panel.add(extBtn,gbc);
		
		gbc.weighty = 2;
	
		markBtn.setFont(font);
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridx = 0;
		gbc.gridy = 3;	
		panel.add(markBtn,gbc);

		extMarkBtn.setFont(font);
		gbc.gridx = 2;
		gbc.gridy = 3;	
		panel.add(extMarkBtn,gbc);
		
		hideBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{		
				HideForm x = new HideForm(0);			
				x.setVisible(true);	
				x = null;
			}
		});
		
		extBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				ExtractForm x = new ExtractForm(0);
				x.setVisible(true);
				x = null;
			}
		});
		
		markBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{		
				ExtractForm x = new ExtractForm(1);		// no need for another UI just to change 3 functions
				x.setVisible(true);	
				x = null;
			}
		});
		
		extMarkBtn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				HideForm x = new HideForm(1);			// same here
				x.setVisible(true);
				x = null;
			}
		});
		
		addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) 
            {
            	//used to regain focus to Menu(not available on linux)
            	owner = javax.swing.FocusManager.getCurrentManager().getActiveWindow();	
            }
        });
	}

}
