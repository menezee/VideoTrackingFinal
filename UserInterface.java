package UIComponents;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class UserInterface {

	private JFrame frmMedia;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserInterface window = new UserInterface();
					window.frmMedia.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMedia = new JFrame();
		frmMedia.setAlwaysOnTop(true);
		frmMedia.setTitle("Media");
		frmMedia.setBounds(100, 100, 790, 521);
		frmMedia.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMedia.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel original = new JPanel();
		original.setBorder(new LineBorder(new Color(0, 0, 0)));
		frmMedia.getContentPane().add(original);
		
		JLabel img1 = new JLabel("");
		img1.setIcon(new ImageIcon("C:\\Users\\menezeer\\workspace\\OpenCV\\images\\parrot.jpg"));
		original.add(img1);
		
		JPanel edited = new JPanel();
		edited.setBorder(new LineBorder(new Color(0, 0, 0)));
		frmMedia.getContentPane().add(edited);
		
		JLabel img2 = new JLabel("");
		img2.setIcon(new ImageIcon("C:\\Users\\menezeer\\workspace\\OpenCV\\images\\parrot.jpg"));
		edited.add(img2);
	}

}
