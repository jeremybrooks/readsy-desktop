/*
 * readsy - read something new every day
 *
 *     Copyright (C) 2013  Jeremy Brooks
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the programs author at jeremyb@whirljack.net
 */

package net.jeremybrooks.readsy.gui;

import net.jeremybrooks.readsy.FileUtil;
import net.jeremybrooks.readsy.Readsy;
import net.jeremybrooks.readsy.bo.ReadsyDataFile;
import org.apache.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * The window shown when a user starts the editor.
 * This window allows the user to either create a new data file,
 * or open an existing one.  Once the user has done one of those things,
 * the resulting ReadsyDataFile object is passed to an instance of
 * EditorWindow, where the user can actually edit the file.
 * <p/>
 * If the editor closes this window, we go back to the main window.
 *
 * @author Jeremy Brooks
 */
public class EditorStartWindow extends JFrame {

	private static final long serialVersionUID = 6602945962112905201L;
	private Logger logger = Logger.getLogger(EditorStartWindow.class);

	/**
	 * Creates new form EditorStartWindow.
	 * The location is set relative to where the main window was.
	 */
	public EditorStartWindow() {
		initComponents();
		this.setLocation(Readsy.getMainWindow().getX(), Readsy.getMainWindow().getY());
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		ResourceBundle bundle = ResourceBundle.getBundle("localization.editor_start_window");
		label1 = new JLabel();
		jPanel1 = new JPanel();
		jLabel2 = new JLabel();
		txtFilename = new JTextField();
		jLabel3 = new JLabel();
		txtDescription = new JTextField();
		jLabel4 = new JLabel();
		txtShortDescription = new JTextField();
		jLabel5 = new JLabel();
		txtYear = new JTextField();
		btnNew = new JButton();
		jPanel2 = new JPanel();
		jLabel1 = new JLabel();
		btnBrowse = new JButton();
		jPanel3 = new JPanel();
		btnCancel = new JButton();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle(bundle.getString("this.title"));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing();
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

		//---- label1 ----
		label1.setIcon(new ImageIcon(getClass().getResource("/images/icon64.png")));
		contentPane.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== jPanel1 ========
		{
			jPanel1.setBorder(new TitledBorder(bundle.getString("jPanel1.border")));
			jPanel1.setLayout(new GridBagLayout());

			//---- jLabel2 ----
			jLabel2.setText(bundle.getString("jLabel2.text"));
			jPanel1.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- txtFilename ----
			txtFilename.setToolTipText("Filename to create.  This is relative to your home directory.");
			jPanel1.add(txtFilename, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- jLabel3 ----
			jLabel3.setText(bundle.getString("jLabel3.text"));
			jPanel1.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- txtDescription ----
			txtDescription.setToolTipText("The description attribute for the data file.");
			jPanel1.add(txtDescription, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- jLabel4 ----
			jLabel4.setText(bundle.getString("jLabel4.text"));
			jPanel1.add(jLabel4, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- txtShortDescription ----
			txtShortDescription.setToolTipText("The short description attribute.  This is shown on the tab.");
			jPanel1.add(txtShortDescription, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- jLabel5 ----
			jLabel5.setText(bundle.getString("jLabel5.text"));
			jPanel1.add(jLabel5, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- txtYear ----
			txtYear.setColumns(6);
			txtYear.setToolTipText(bundle.getString("txtYear.toolTipText"));
			jPanel1.add(txtYear, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- btnNew ----
			btnNew.setText(bundle.getString("btnNew.text"));
			btnNew.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnNewActionPerformed();
				}
			});
			jPanel1.add(btnNew, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));
		}
		contentPane.add(jPanel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== jPanel2 ========
		{
			jPanel2.setBorder(new TitledBorder(bundle.getString("jPanel2.border")));
			jPanel2.setLayout(new GridBagLayout());

			//---- jLabel1 ----
			jLabel1.setText(bundle.getString("jLabel1.text"));
			jPanel2.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));

			//---- btnBrowse ----
			btnBrowse.setText(bundle.getString("btnBrowse.text"));
			btnBrowse.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnBrowseActionPerformed();
				}
			});
			jPanel2.add(btnBrowse, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(4, 4, 4, 4), 0, 0));
		}
		contentPane.add(jPanel2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== jPanel3 ========
		{
			jPanel3.setLayout(new FlowLayout(FlowLayout.RIGHT));

			//---- btnCancel ----
			btnCancel.setText(bundle.getString("btnCancel.text"));
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnCancelActionPerformed();
				}
			});
			jPanel3.add(btnCancel);
		}
		contentPane.add(jPanel3, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		setSize(500, 370);
		setLocationRelativeTo(getOwner());
	}
	// </editor-fold>//GEN-END:initComponents

	/**
	 * Respond to user clicks on the "Cancel" button.
	 * This does the same thing as the user closing the form.
	 */
	private void btnCancelActionPerformed() {
		closeEditorWindow();
	}

	/**
	 * Allow the user to open an existing readsy file.
	 * The file will be turned into an instance of ReadsyDataFile,
	 * and passed to an EditorWindow.
	 * <p/>
	 * If the file cannot be parsed, an error is displayed, and the user
	 * goes back to this window (EditorStartWindow) to try again.
	 * <p/>
	 * The file chooser defaults to the user home, and filters all files
	 * that end with ".xml".  Only one file may be selected.
	 */
	private void btnBrowseActionPerformed() {

		JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(new SwingXMLFileFilter());
		jfc.setFileHidingEnabled(false);

		if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = jfc.getSelectedFile();

			try {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				ReadsyDataFile dataFile = new ReadsyDataFile(f);

				// if the version is 0, this is an old data file, so convert as follows:
				//    save a backup
				//    set the version
				//    write the data file
				if (dataFile.getReadsyRootElement().getVersion() == 0) {
					logger.info("Converting old data file to new format...");
					File dest = new File(f.getParent(), f.getName().substring(0, f.getName().indexOf('.')) + "_old.xml");
					FileUtil.copy(f, dest);
					dataFile.getReadsyRootElement().setVersion(1);
					dataFile.write();
				}
				openEditorWindow(dataFile);

			} catch (Exception e) {
				logger.error("ERROR CREATING ReadsyDataFile OBJECT FROM FILE " + f.getAbsolutePath(), e);

				JOptionPane.showMessageDialog(this,
						"Could not parse the selected file.",
						"Parse Error",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				this.setCursor(Cursor.getDefaultCursor());
			}

		}
	}

	/**
	 * If the user closes this window, dispose of it, and show the
	 * main window again.
	 */
	private void formWindowClosing() {
		closeEditorWindow();
	}

	/**
	 * Make the main window visible, and get rid of the editor window.
	 */
	private void closeEditorWindow() {
		Readsy.getMainWindow().setVisible(true, false);
		this.setVisible(false);
		this.dispose();
	}

	/**
	 * If the user has entered valid data, create a new ReadsyDataFile
	 * object with empty entries and pass it to a new EditorWindow.
	 * <p/>
	 * If any errors occur while creating the new file, display an error message
	 * and let the user try again.
	 */
	private void btnNewActionPerformed() {
		if (validateEntry()) {
			int year = Integer.parseInt(this.txtYear.getText());
			String filename = this.txtFilename.getText();
			if (!filename.endsWith(".xml")) {
				filename += ".xml";
			}
			File file = new File(System.getProperty("user.home"), filename);

			try {
				ReadsyDataFile dataFile = new ReadsyDataFile(
						year,
						file,
						this.txtDescription.getText(),
						this.txtShortDescription.getText(),
						false
				);

				openEditorWindow(dataFile);

			} catch (IOException ioe) {
				logger.error("ERROR CREATING FILE " + file.getAbsolutePath(), ioe);
				JOptionPane.showMessageDialog(this,
						"There was an error while trying to create the file\n" + file.getAbsolutePath(),
						"File Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Check the data entered by a user for a new file.
	 * An error message will be displayed if the data fails validation.
	 *
	 * @return true if the data is okay, false otherwise.
	 */
	private boolean validateEntry() {
		boolean valid = true;
		StringBuilder err = new StringBuilder();
		if (this.txtDescription.getText().trim().length() == 0) {
			err.append("  * You must enter a Description.\n");
			valid = false;
		}
		if (this.txtShortDescription.getText().trim().length() == 0) {
			err.append("  * You must enter a Short Description.\n");
			valid = false;
		}

		if (!valid) {
			JOptionPane.showMessageDialog(this,
					"Please correct the following errors:\n" + err.toString(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			// LOOKS OKAY, SO CHECK THE FILENAME AND YEAR
			if (this.txtFilename.getText().trim().length() == 0) {
				this.txtFilename.setText(this.txtShortDescription.getText());
			}
			if (this.txtYear.getText() == null || this.txtYear.getText().trim().length() == 0) {
				this.txtYear.setText("0");
			}
		}

		return valid;
	}

	/**
	 * Open an EditorWindow.
	 * The dataFile is passed to the EditorWindow, and this window is hidden
	 * and disposed of.
	 *
	 * @param dataFile the file representing the Readsy XML.
	 */
	private void openEditorWindow(ReadsyDataFile dataFile) {
		new EditorWindow(dataFile).setVisible(true);
		this.setVisible(false);
		this.dispose();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JLabel label1;
	private JPanel jPanel1;
	private JLabel jLabel2;
	private JTextField txtFilename;
	private JLabel jLabel3;
	private JTextField txtDescription;
	private JLabel jLabel4;
	private JTextField txtShortDescription;
	private JLabel jLabel5;
	private JTextField txtYear;
	private JButton btnNew;
	private JPanel jPanel2;
	private JLabel jLabel1;
	private JButton btnBrowse;
	private JPanel jPanel3;
	private JButton btnCancel;
	// End of variables declaration//GEN-END:variables
}
