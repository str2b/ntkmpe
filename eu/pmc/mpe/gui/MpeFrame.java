package eu.pmc.mpe.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

import eu.pmc.mpe.MovieParamEditor;
import eu.pmc.mpe.MediaTableModel;
import eu.pmc.mpe.MovieParam;

public class MpeFrame {

	private String version = "1.2";
	private String appName = "NtkMPE";

	private MovieParamEditor mpe;

	private JFrame frame;
	private JLabel jpInfo;

	public MpeFrame() {
		frame = new JFrame();
		frame.setResizable(false);
		JMenuBar jb = new JMenuBar();
		JMenu jmFile = new JMenu("File");
		JMenuItem jmiLoad = new JMenuItem("Open Ntk firmware");
		jmiLoad.addActionListener(new OpenListener());
		JMenu jmApply = new JMenu("Apply modifications");
		jmApply.addMouseListener(new ApplyListener());
		JMenu jmQ = new JMenu("About");
		JMenuItem jmDonate = new JMenuItem("Donate");
		jmDonate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DonateGui.viewOver(frame);
			}
		});
		JMenuItem jmAbout = new JMenuItem("Version");
		jmAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,
						String.format("<html><h2><b>%s %s</b></h2>"
								+ "<section>Thanks to nutsey for initial discovery of movie parameter struct</section><br/><br/>"
								+ "<p><b>Released for:</b><ul><li>DASHCAMTALK</li><li>GoPrawn</li></ul></p>"
								+ "<p>Author: Tobi@s</p></html>", appName, version),
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(appName + " " + version + " by Tobi@s");

		frame.setJMenuBar(jb);
		jb.add(jmFile);
		jmFile.add(jmiLoad);
		jb.add(jmApply);
		jb.add(jmQ);
		jmQ.add(jmDonate);
		jmQ.add(jmAbout);
		frame.getContentPane().add(jpInfo = new JLabel("Please open a firmware file..."), BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}

	private JPanel jpTable;

	private MediaTableModel mtm;

	private void addMovieParamTable(List<MovieParam> l, String fileName) {
		if (jpTable != null) {
			frame.remove(jpTable);
		}
		jpTable = new JPanel();
		jpTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fileName));
		JTable jtMovieParams = new JTable(mtm = new MediaTableModel(l));
		jtMovieParams.setAutoCreateRowSorter(true);
		jtMovieParams.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jtMovieParams.setPreferredScrollableViewportSize(jtMovieParams.getPreferredSize());
		jtMovieParams.getColumnModel().getColumn(5).setPreferredWidth(50);
		JScrollPane jspTable = new JScrollPane(jtMovieParams);
		jspTable.setPreferredSize(new Dimension(375, 330));
		jpTable.add(jspTable);
		frame.getContentPane().add(jpTable, BorderLayout.CENTER);
		frame.pack();
		addActionCommandListener(jtMovieParams);
	}

	private void addActionCommandListener(JTable table) {
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Copy")) {
					doCopy(table);
				} else if (event.getActionCommand().equals("Paste")) {
					doPaste(table);
				}
			}
		};

		final KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		table.registerKeyboardAction(listener, "Copy", copy, JComponent.WHEN_FOCUSED);
		final KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		table.registerKeyboardAction(listener, "Paste", paste, JComponent.WHEN_FOCUSED);
	}

	public static void main(String args[]) {
		new MpeFrame();
	}

	class OpenListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			int retVal = chooser.showDialog(null, "Load uncompressed Ntk firmware");
			File fwFile;

			if (retVal == JFileChooser.APPROVE_OPTION) {
				fwFile = chooser.getSelectedFile();
				mpe = new MovieParamEditor(fwFile);
				try {
					mpe.open();
					SwingWorker<Integer, Object> sw = new SwingWorker<Integer, Object>() {

						@Override
						protected Integer doInBackground() throws Exception {
							return mpe.analyze();
						}

						@Override
						protected void done() {
							try {
								int count = this.get();
								addMovieParamTable(mpe.getParams(), fwFile.getAbsolutePath());
								if (count <= 0) {
									JOptionPane.showMessageDialog(frame,
											"File does not contain any valid movie parameters", "An error occured:",
											JOptionPane.ERROR_MESSAGE);
								}
								jpInfo.setText("Loaded " + count + " movie parameters");
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					};
					sw.execute();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	class ApplyListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (mpe != null) {
				try {
					SwingWorker<Integer, Object> sw = new SwingWorker<Integer, Object>() {
						@Override
						protected Integer doInBackground() throws IOException {
							return mpe.apply();
						}

						@Override
						protected void done() {
							try {
								jpInfo.setText("Modified " + this.get() + " movie parameter(s)!");
							} catch (Exception e) {
								jpInfo.setText("Error: " + e.getMessage());
								e.printStackTrace();
							}
						}
					};
					sw.execute();

				} catch (Exception e1) {
					jpInfo.setText("Error!");
					JOptionPane.showMessageDialog(frame, e1.getMessage(), "An error occured:",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	private void doCopy(JTable table) {
		int col = table.getSelectedColumn();
		int row = table.getSelectedRow();
		if (col != -1 && row != -1) {
			Object value = table.getValueAt(row, col);
			String data;
			if (value == null) {
				data = "";
			} else {
				data = value.toString();
			}

			final StringSelection selection = new StringSelection(data);

			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
		}
	}

	private void doPaste(JTable table) {
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final Transferable content = clipboard.getContents(this);
		if (content != null) {
			final Object paste;
			try {
				if (table.getSelectedColumn() == 4) {
					paste = Short.parseShort((String) content.getTransferData(DataFlavor.stringFlavor));
				} else {
					paste = Integer.parseInt((String) content.getTransferData(DataFlavor.stringFlavor));
				}

				final int col = table.getSelectedColumn();
				final int row = table.getSelectedRow();
				if (table.isCellEditable(row, col)) {
					table.setValueAt(paste, row, col);
				}
				mtm.fireTableCellUpdated(row, col);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
