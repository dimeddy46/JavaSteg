package stegSource;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Interf extends JFrame {

	private JPanel contentPane;
	static final long serialVersionUID = 1;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interf frame = new Interf();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Interf() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnTest = new JButton("test1");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTest.setText("TERMINAT!");
			}
		});
		btnTest.setBounds(156, 118, 89, 23);
		contentPane.add(btnTest);
	}
}
