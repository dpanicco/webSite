package it.enel.util.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import it.enel.util.util.CodeHash;
import it.enel.util.util.FileChoose;

public class ComputeHash extends JFrame {

	private JPanel contentPane;
	File fileToHash;
	private JTextField calcHash;
	private JTextField copiedHash;
	private JButton btnStopHash;
	private boolean stopHash = false;
	private final Panel panel = new Panel();

	private class CompHash extends Thread {

		JTextField fileHash;
		JComboBox<Enum<?>> comboBox;
		JProgressBar hashProgrBar;
		
		public CompHash(JTextField fileHash, JComboBox<Enum<?>> comboBox, JProgressBar hashProgrBar) {
			super();
			this.fileHash = fileHash;
			this.comboBox = comboBox;
			this.hashProgrBar = hashProgrBar;
		}

		@Override
		public void run() {

			MessageDigest md = null;

	        try (FileInputStream fis = new FileInputStream(fileHash.getText())) {
	        	
	        	btnStopHash.setEnabled(true);
	        	
	        	long fileSize = fis.getChannel().size();

	        	md = MessageDigest.getInstance(((CodeHash)comboBox.getSelectedItem()).type());

	        	byte[] dataBytes = new byte[1024];

	        	int nread = 0; 
	        	long totread = 0; 
	        	while ((nread = fis.read(dataBytes)) != -1) {

	        		if (stopHash)
	        			break;
	        		
	        		md.update(dataBytes, 0, nread);

	        		totread += nread;
	        		hashProgrBar.setValue((int)(totread * 100 / fileSize));
	        		hashProgrBar.repaint();
	        	};
	        	
	        } catch (NoSuchAlgorithmException e1) {
	        	e1.printStackTrace();
	        } catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				btnStopHash.setEnabled(false);
			}
        	
        	if (stopHash) {
        		this.fileHash.setText("");
        		hashProgrBar.setValue(0);
        		contentPane.repaint();
        		calcHash.setText("");
        		stopHash = false;
        	} else {

		        byte[] mdbytes = md.digest();
		     
		        //convert the byte to hex format method 1
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < mdbytes.length; i++) {
		          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		        }
	
		        calcHash.setText(sb.toString().toUpperCase());
        	}
			
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ComputeHash frame = new ComputeHash();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ComputeHash() {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 729, 357);
		contentPane = new JPanel();
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBorder(new LineBorder(new Color(30, 144, 255), 3, true));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTest = new JLabel("Compute HASH CODE");
		lblTest.setBackground(new Color(30, 144, 255));
		lblTest.setOpaque(true);
		lblTest.setForeground(new Color(139, 69, 19));
		lblTest.setFont(new Font("Arial Black", Font.PLAIN, 26));
		lblTest.setBounds(73, 11, 333, 37);
		contentPane.add(lblTest);
		
		JLabel lblVerify = new JLabel("");
		lblVerify.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblVerify.setBounds(191, 278, 375, 25);
		contentPane.add(lblVerify);
		
		JProgressBar hashProgrBar = new JProgressBar(0, 100);
		hashProgrBar.setForeground(new Color(0, 128, 0));
		hashProgrBar.setStringPainted(true);
		hashProgrBar.setBounds(191, 125, 460, 14);
		contentPane.add(hashProgrBar);
		
		JTextField fileHash = new JTextField("");
		
		JComboBox<Enum<?>> comboBox = new JComboBox<Enum<?>>();
		comboBox.setFont(new Font("Arial", Font.BOLD, 14));
		comboBox.setForeground(new Color(139, 69, 19));
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fileHash != null && fileHash.getText().length() > 0)
				computeHash(fileHash, comboBox, hashProgrBar);
			}
		});
		comboBox.setModel(new DefaultComboBoxModel<Enum<?>>(CodeHash.values()));
		comboBox.setBounds(25, 178, 103, 26);
		contentPane.add(comboBox);

		fileHash.setEditable(false);
		fileHash.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  }
				  public void removeUpdate(DocumentEvent e) {
				  }
				  public void insertUpdate(DocumentEvent e) {
					  computeHash(fileHash, comboBox, hashProgrBar);
				  }
		});


		fileHash.setBackground(SystemColor.control);
		fileHash.setForeground(Color.RED);
		fileHash.setBounds(191, 94, 460, 20);
		fileHash.setDropTarget(new DropTarget() {
		    public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            List<File> droppedFiles = (List<File>)
		                evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            if (droppedFiles.size() > 1)
		            	throw new Exception("You can Drop only 1 file");
//		            for (File file : droppedFiles) {
		                System.out.println(droppedFiles.get(0).getAbsolutePath());
		                fileHash.setText(droppedFiles.get(0).getAbsolutePath());
//		            }
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});		

		contentPane.add(fileHash);
		
		JButton btnVerify = new JButton("VERIFY (Paste)");
		btnVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					copiedHash.setText((String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
//					System.out.println(copiedHash.getText());
//					System.out.println(computedHash.getText());
					if (copiedHash.getText().toUpperCase().equals(calcHash.getText().toUpperCase())) {
						lblVerify.setText("Codes Match!");
						lblVerify.setForeground(new Color(0, 128, 0));
					} else {
						lblVerify.setText("Codes DON'T Match!!!");
						lblVerify.setForeground(Color.RED);
					}
						
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		btnVerify.setForeground(new Color(139, 69, 19));
		btnVerify.setFont(new Font("Arial", Font.BOLD, 14));
		btnVerify.setBounds(25, 236, 156, 26);
		contentPane.add(btnVerify);
		
		JButton btnFile = new JButton("File...");
		btnFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				FileChoose f = new FileChoose();
				f.updateUI();
				f.setVisible(true);
			}
		});
		btnFile.setForeground(new Color(139, 69, 19));
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileChoose fc = new FileChoose();
				int n = fc.showFile();
				if (n == JFileChooser.APPROVE_OPTION) {
					fileToHash = fc.getFile();
					fileHash.setText(fileToHash.getAbsolutePath());
				}
//				contentPane.add(fc);
//				fc.setVisible(true);
//				fc.repaint();
//				contentPane.repaint();
			}
		});
		btnFile.setFont(new Font("Arial", Font.BOLD, 14));
		btnFile.setBounds(25, 94, 115, 26);
		contentPane.add(btnFile);
		
		calcHash = new JTextField();
		calcHash.setEditable(false);
		calcHash.setFont(new Font("Tahoma", Font.BOLD, 11));
		calcHash.setBounds(191, 181, 460, 20);
		contentPane.add(calcHash);
		calcHash.setColumns(10);
//----------------------------------------
/*
	    final TransferHandler handler = new TransferHandler() {
	        public boolean canImport(TransferHandler.TransferSupport support) {
	            System.out.println("canImport");
	            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	                return false;
	            }

//	            if (copyItem.isSelected()) {
	                boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;

	                if (!copySupported) {
	                    return false;
	                }

	                support.setDropAction(COPY);
//	            }

	                importData(support);
	            return true;
	        }

	

        public boolean importData(TransferHandler.TransferSupport support) {
            System.out.println("ImportData");
//        	if (!canImport(support)) {
//                return false;
//            }
            
            Transferable t = support.getTransferable();

            try {
                java.util.List<File> l =
                    (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
                
                if (l.size() > 0)
                	throw new IOException("Unsupport hash cechsum on List of Files!");

                lblFileName.setText(l.get(0).getAbsolutePath());
                
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }
    };
    
    DropTargetListener dtl = new DropTargetListener() {
		
		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			System.out.println("DropTargetDragEvent");

		}
		
		@Override
		public void drop(DropTargetDropEvent dtde) {
			// TODO Auto-generated method stub
			System.out.println("DropTargetDropEvent");
			
		}
		
		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void dragExit(DropTargetEvent dte) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			// TODO Auto-generated method stub
			
		}
	};
*/
	//------------------------------------------		
		copiedHash = new JTextField();
//		copiedHash.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				setTransferHandler(handler);
//			}
//		});
		copiedHash.setFont(new Font("Tahoma", Font.BOLD, 11));
		copiedHash.setColumns(10);
		copiedHash.setBounds(191, 236, 460, 20);
//		copiedHash.getTransferHandler();
//		System.out.println(SystemFlavorMap.getDefaultFlavorMap());
//        copiedHash.setDropTarget(new DropTarget(this, dtl));
//        copiedHash.setDropMode(DropMode.INSERT);
//        contentPane.setTransferHandler(handler);
		
		contentPane.add(copiedHash);
		
		JLabel lblCalcHash = new JLabel("Current File Checksum Value");
		lblCalcHash.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCalcHash.setBounds(191, 159, 191, 14);
		contentPane.add(lblCalcHash);
		
		JLabel lblOriginalFileChecksum = new JLabel("Original File Checksum Value");
		lblOriginalFileChecksum.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOriginalFileChecksum.setBounds(191, 212, 203, 14);
		contentPane.add(lblOriginalFileChecksum);
		
		JLabel lblNewLabel = new JLabel("Select a File to compute checksum");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setBounds(191, 69, 287, 14);
		contentPane.add(lblNewLabel);
		panel.setBackground(new Color(30, 144, 255));
		panel.setBounds(0, 0, 783, 63);
		contentPane.add(panel);
		
		ImageIcon stop = new ImageIcon(ComputeHash.class.getResource("/images/Stop26.png"));
		btnStopHash = new JButton(stop);
		btnStopHash.setEnabled(false);
		btnStopHash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(arg0.getActionCommand());
				stopHash = true;
			}
			
		});
		btnStopHash.setBounds(661, 93, 26, 26);
		btnStopHash.setBorder(BorderFactory.createEmptyBorder());
		btnStopHash.setContentAreaFilled(false);
		contentPane.add(btnStopHash);

//		contentPane.getTransferHandler();
	
}

	protected void computeHash(JTextField fileHash, JComboBox<Enum<?>> comboBox, JProgressBar hashProgrBar) {

		(new CompHash(fileHash, comboBox, hashProgrBar)).start();
		
	}	
}
