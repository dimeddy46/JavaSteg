package stegSource;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class absLayout extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					absLayout frame = new absLayout();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public absLayout() throws IOException {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 470);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblHideFile = new JLabel("Hide file ");
		lblHideFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblHideFile.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblHideFile.setBounds(341, 11, 70, 14);
		contentPane.add(lblHideFile);
		
		JButton btnSelectCover = new JButton("Select cover");
		btnSelectCover.setBounds(144, 71, 116, 23);
		contentPane.add(btnSelectCover);
		
		JButton btnSelectMessage = new JButton("Select message");
		btnSelectMessage.setBounds(489, 71, 134, 23);
		contentPane.add(btnSelectMessage);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(144, 94, 116, 29);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(489, 94, 116, 29);
		contentPane.add(lblNewLabel_1);
		
               	   
		BufferedImage myPicture = ImageIO.read(new File("mere2.png"));		
		JLabel picLabel = new JLabel();
		picLabel.setBounds(32, 134, 332, 208);
		Image dimg = myPicture.getScaledInstance(picLabel.getWidth(), picLabel.getHeight(),
		        Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(dimg);
		picLabel.setIcon(icon);		
		contentPane.add(picLabel);
		
		JLabel label = new JLabel();
		label.setBounds(392, 134, 332, 208);
		myPicture = ImageIO.read(new File("hidden.png"));
		dimg = myPicture.getScaledInstance(label.getWidth(), label.getHeight(),
		        Image.SCALE_SMOOTH);
		icon = new ImageIcon(dimg);
		label.setIcon(icon);		
		contentPane.add(label);
	}
}
