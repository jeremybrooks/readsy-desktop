/*
 * Copyright (c) 2013, Jeremy Brooks
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package net.jeremybrooks.readsy.gui;

import net.jeremybrooks.readsy.PropertyManager;
import net.jeremybrooks.readsy.Readsy;
import net.jeremybrooks.readsy.gui.workers.DropboxAuthWorker;
import net.jeremybrooks.readsy.gui.workers.DropboxCopyWorker;
import net.jeremybrooks.readsy.gui.workers.DropboxDeleteContentWorker;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Allow the user to set various preferences.
 * Settings will be saved for future use.
 *
 * @author Jeremy Brooks
 */
public class PreferencesDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 2431764287936009269L;
	private ResourceBundle bundle = ResourceBundle.getBundle("localization.preferences");
	private Logger logger = Logger.getLogger(PreferencesDialog.class);

	/**
	 * The initial font size shown in the dialog.
	 */
	private int initialFontSize;

	/**
	 * Creates new form PreferencesDialog.
	 * The state will be set from the currently saved preferences.
	 */
	private void cbxDropboxActionPerformed() {
		if (cbxDropbox.isSelected()) {
			// enable
			int confirm = JOptionPane.showConfirmDialog(this,
					bundle.getString("PreferencesDialog.joption.authorizeConfirm.message"),
					bundle.getString("PreferencesDialog.joption.authorizeConfirm.title"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirm == JOptionPane.YES_OPTION) {
				DropboxAuthWorker daw = new DropboxAuthWorker();
				WorkerDialog wd = new WorkerDialog(this, daw, bundle.getString("worker.authorize.title"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
				daw.setComponent(this);
				wd.executeAndShowDialog();

				// if no errors during authorization, start the copy
				if (daw.getException() == null) {
					DropboxCopyWorker dcw = new DropboxCopyWorker();
					wd = new WorkerDialog(this, dcw, bundle.getString("worker.copying.title"), "");
					wd.executeAndShowDialog();

					String message = bundle.getString("PreferencesDialog.joption.syncEnabled.message1");
					if (dcw.getException() != null) {
						logger.error("Error copying files to dropbox.", dcw.getException());
						message += "\n" + bundle.getString("PreferencesDialog.joption.syncEnabled.message2");
					}
					JOptionPane.showMessageDialog(this,
							message,
							bundle.getString("PreferencesDialog.joption.syncEnabled.title"),
							JOptionPane.INFORMATION_MESSAGE);
					Readsy.getMainWindow().createTabs();
				} else {
					// error during authorization, so uncheck the checkbox
					this.cbxDropbox.setSelected(false);
					logger.error("Error during dropbox authorization.", daw.getException());
					JOptionPane.showMessageDialog(this,
							bundle.getString("PreferencesDialog.joption.authorizeError.message1") +
									daw.getException().getMessage() + bundle.getString("PreferencesDialog.joption.authorizeError.message2"),
							bundle.getString("PreferencesDialog.joption.authorizeError.title"),
							JOptionPane.ERROR_MESSAGE
					);
				}
			}
		} else {
			int confirm = JOptionPane.showConfirmDialog(this,
					bundle.getString("PreferencesDialog.joption.syncDisable.message"),
					bundle.getString("PreferencesDialog.joption.syncDisable.title"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirm == JOptionPane.YES_OPTION) {
				DropboxDeleteContentWorker d = new DropboxDeleteContentWorker();
				WorkerDialog wd = new WorkerDialog(this, d, bundle.getString("worker.deleting"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
				wd.executeAndShowDialog();

				String message = bundle.getString("PreferencesDialog.joption.syncDisabled.message1");
				if (d.getException() != null) {
					logger.error("Error deleting files from dropbox.", d.getException());
					message += "\n" + bundle.getString("PreferencesDialog.joption.syncDisabled.message2");
				}
				JOptionPane.showMessageDialog(this,
						message,
						bundle.getString("PreferencesDialog.joption.syncDisabled.title"),
						JOptionPane.INFORMATION_MESSAGE);
				Readsy.getMainWindow().createTabs();

			} else {
				this.cbxDropbox.setSelected(true);
			}
		}

	}

	public PreferencesDialog(Frame parent, boolean modal) {
		super(parent, modal);

		initComponents();
		this.cbxUpdates.setSelected(PropertyManager.getInstance().getPropertyAsBoolean(PropertyManager.PROPERTY_CHECK_FOR_UPDATES));
		this.initialFontSize = PropertyManager.getInstance().getPropertyAsInt(PropertyManager.PROPERTY_FONT_SIZE);
		this.cmbFont.setSelectedItem(Integer.toString(this.initialFontSize));
		this.cbxDropbox.setSelected(PropertyManager.getInstance().getPropertyAsBoolean(PropertyManager.DROPBOX_ENABLED));

		getRootPane().setDefaultButton(this.btnOk);
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		ResourceBundle bundle = this.bundle;
		panel2 = new JPanel();
		jPanel1 = new JPanel();
		cbxUpdates = new JCheckBox();
		jPanel3 = new JPanel();
		cmbFont = new JComboBox<>();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		panel4 = new JPanel();
		cbxDropbox = new JCheckBox();
		textArea1 = new JTextArea();
		panel1 = new JPanel();
		btnOk = new JButton();
		btnCancel = new JButton();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle(bundle.getString("PreferencesDialog.this.title"));
		setAlwaysOnTop(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== panel2 ========
		{
			panel2.setLayout(new GridLayout(3, 1));

			//======== jPanel1 ========
			{
				jPanel1.setBorder(new TitledBorder(bundle.getString("PreferencesDialog.jPanel1.border")));

				//---- cbxUpdates ----
				cbxUpdates.setText(bundle.getString("PreferencesDialog.cbxUpdates.text"));
				cbxUpdates.setBorder(BorderFactory.createEmptyBorder());
				cbxUpdates.setMargin(new Insets(0, 0, 0, 0));

				GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
				jPanel1.setLayout(jPanel1Layout);
				jPanel1Layout.setHorizontalGroup(
					jPanel1Layout.createParallelGroup()
						.addGroup(jPanel1Layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(cbxUpdates)
							.addContainerGap(310, Short.MAX_VALUE))
				);
				jPanel1Layout.setVerticalGroup(
					jPanel1Layout.createParallelGroup()
						.addGroup(jPanel1Layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(cbxUpdates)
							.addContainerGap(139, Short.MAX_VALUE))
				);
			}
			panel2.add(jPanel1);

			//======== jPanel3 ========
			{
				jPanel3.setBorder(new TitledBorder(bundle.getString("PreferencesDialog.jPanel3.border")));

				//---- cmbFont ----
				cmbFont.setModel(new DefaultComboBoxModel<>(new String[] {
					"8",
					"10",
					"12",
					"14",
					"18",
					"20",
					"24",
					"30",
					"36"
				}));
				cmbFont.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cmbFontActionPerformed();
					}
				});

				//---- jLabel2 ----
				jLabel2.setText(bundle.getString("PreferencesDialog.jLabel2.text"));

				//---- jLabel3 ----
				jLabel3.setText(bundle.getString("PreferencesDialog.jLabel3.text"));

				GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
				jPanel3.setLayout(jPanel3Layout);
				jPanel3Layout.setHorizontalGroup(
					jPanel3Layout.createParallelGroup()
						.addGroup(jPanel3Layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(cmbFont, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(jPanel3Layout.createParallelGroup()
								.addComponent(jLabel3)
								.addComponent(jLabel2))
							.addContainerGap(191, Short.MAX_VALUE))
				);
				jPanel3Layout.setVerticalGroup(
					jPanel3Layout.createParallelGroup()
						.addGroup(jPanel3Layout.createSequentialGroup()
							.addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(cmbFont, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel2))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jLabel3)
							.addContainerGap(114, Short.MAX_VALUE))
				);
			}
			panel2.add(jPanel3);

			//======== panel4 ========
			{
				panel4.setBorder(new TitledBorder(bundle.getString("PreferencesDialog.panel4.border")));
				panel4.setLayout(new BorderLayout());

				//---- cbxDropbox ----
				cbxDropbox.setText(bundle.getString("PreferencesDialog.cbxDropbox.text"));
				cbxDropbox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cbxDropboxActionPerformed();
					}
				});
				panel4.add(cbxDropbox, BorderLayout.NORTH);

				//---- textArea1 ----
				textArea1.setEditable(false);
				textArea1.setWrapStyleWord(true);
				textArea1.setLineWrap(true);
				textArea1.setText(bundle.getString("PreferencesDialog.textArea1.text"));
				textArea1.setBackground(UIManager.getColor("Label.background"));
				panel4.add(textArea1, BorderLayout.CENTER);
			}
			panel2.add(panel4);
		}
		contentPane.add(panel2, BorderLayout.CENTER);

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

			//---- btnOk ----
			btnOk.setText(bundle.getString("PreferencesDialog.btnOk.text"));
			btnOk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnOkActionPerformed();
				}
			});
			panel1.add(btnOk);

			//---- btnCancel ----
			btnCancel.setText(bundle.getString("PreferencesDialog.btnCancel.text"));
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnCancelActionPerformed();
				}
			});
			panel1.add(btnCancel);
		}
		contentPane.add(panel1, BorderLayout.SOUTH);
		setSize(535, 635);
		setLocationRelativeTo(getOwner());
	}// </editor-fold>//GEN-END:initComponents


	/*
	 * Handle clicks on the OK button.
	 * When the user clicks on the OK button, settings will be saved,
	 * and the preferences dialog will be closed.
	 */
	private void btnOkActionPerformed() {//GEN-FIRST:event_btnOkActionPerformed
		if (this.cbxUpdates.isSelected()) {
			PropertyManager.getInstance().setProperty(PropertyManager.PROPERTY_CHECK_FOR_UPDATES, "true");
		} else {
			PropertyManager.getInstance().setProperty(PropertyManager.PROPERTY_CHECK_FOR_UPDATES, "false");
		}

		PropertyManager.getInstance().setProperty(PropertyManager.PROPERTY_FONT_SIZE, this.cmbFont.getSelectedItem().toString());

		this.setVisible(false);
		this.dispose();
	}//GEN-LAST:event_btnOkActionPerformed


	/*
	 * Handle clicks on the Cancel button.
	 * When the user clicks the Cancel button, settings will not be
	 * saved, and the preferences dialog is closed.
	 */
	private void btnCancelActionPerformed() {//GEN-FIRST:event_btnCancelActionPerformed
		// restore the font size
		this.cmbFont.setSelectedItem(this.initialFontSize);
		this.setVisible(false);
		this.dispose();
	}//GEN-LAST:event_btnCancelActionPerformed


	/*
	 * Respond to changes in the font size.
	 * When the user changes the font size, the size in all displayed tabs is updated.
	 */
	private void cmbFontActionPerformed() {//GEN-FIRST:event_cmbFontActionPerformed
		float size = 12f;

		try {
			size = Float.valueOf(this.cmbFont.getSelectedItem().toString());
		} catch (Exception e) {
			// ignore
		}

		List<TabPanel> tabs = ((MainWindow) this.getParent()).getTabList();
		for (TabPanel tab : tabs) {
			tab.updateFontSize(size);
		}
	}//GEN-LAST:event_cmbFontActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JPanel panel2;
	private JPanel jPanel1;
	private JCheckBox cbxUpdates;
	private JPanel jPanel3;
	private JComboBox<String> cmbFont;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JPanel panel4;
	private JCheckBox cbxDropbox;
	private JTextArea textArea1;
	private JPanel panel1;
	private JButton btnOk;
	private JButton btnCancel;
	// End of variables declaration//GEN-END:variables

}