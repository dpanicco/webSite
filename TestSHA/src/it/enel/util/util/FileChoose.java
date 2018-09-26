package it.enel.util.util;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.webkit.ContextMenu.ShowContext;

import java.io.File;

import javax.swing.JFileChooser;

public class FileChoose extends JPanel {

	JFileChooser fileChooser; 
	/**
	 * Create the panel.
	 */
	public FileChoose() {
		
		setLayout(null);
		
		fileChooser = new JFileChooser();
		fileChooser.setBounds(0, 0, 569, 397);
//		fileChooser.updateUI();
		add(fileChooser);

	}
	
	public int showFile() {
		return fileChooser.showOpenDialog(this);
	}
	
	public File getFile() {
		return fileChooser.getSelectedFile();
	}

}
