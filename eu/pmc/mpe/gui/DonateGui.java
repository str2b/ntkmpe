package eu.pmc.mpe.gui;

/**
 * Copied from A7Tool
 * 
 */

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.Window.Type;

import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingConstants;

import eu.pmc.mpe.NetworkUtil;

public class DonateGui {

	private JFrame frame;
	public static void viewOver(JFrame parent) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
					DonateGui window = new DonateGui(parent);
					window.frame.setVisible(true);
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DonateGui(JFrame parent) {
		initialize(parent);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(JFrame parent) {
		frame = new JFrame();
		frame.setType(Type.UTILITY);
		frame.setResizable(false);
		frame.setBounds(100, 100, 360, 216);

		JLabel lblIfYouLike = new JLabel(
				"If you like my work and have some money left...");
		lblIfYouLike.setFont(new Font("Dialog", Font.BOLD, 14));
		lblIfYouLike.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblIfYouLike, BorderLayout.CENTER);
		JButton ppbutton = new JButton(new ImageIcon(DonateGui.class.getResource("/res/pay-pal-donation.png")));

		frame.getContentPane().add(ppbutton, BorderLayout.SOUTH);

		ppbutton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				onImageClick();
				frame.dispose();
			}
		});

		frame.setLocationRelativeTo(parent);
	}

	private void onImageClick() {
		try {
			NetworkUtil.openWebpage(new URL("http://dc.p-mc.eu/donate"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
