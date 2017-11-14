/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2017  Jeremy Brooks
 *
 * This file is part of readsy.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jeremybrooks.readsy.gui;

import net.jeremybrooks.readsy.PropertyManager;
import net.jeremybrooks.readsy.Readsy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
	private Logger logger = LogManager.getLogger(PreferencesDialog.class);

	/**
	 * The initial font size shown in the dialog.
	 */
	private int initialFontSize;

	/**
	 * Creates new form PreferencesDialog.
	 * The state will be set from the currently saved preferences.
	 */
//	private void cbxDropboxActionPerformed() {
//		if (cbxDropbox.isSelected()) {
//			// enable
//			int confirm = JOptionPane.showConfirmDialog(this,
//					bundle.getString("PreferencesDialog.joption.authorizeConfirm.message"),
//					bundle.getString("PreferencesDialog.joption.authorizeConfirm.title"),
//					JOptionPane.YES_NO_OPTION,
//					JOptionPane.QUESTION_MESSAGE);
//			if (confirm == JOptionPane.YES_OPTION) {
//				DropboxAuthWorker daw = new DropboxAuthWorker();
//				WorkerDialog wd = new WorkerDialog(this, daw, bundle.getString("worker.authorize.title"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
//				daw.setComponent(this);
//				wd.executeAndShowDialog();
//
//				// if no errors during authorization, see if we should start copying
//				if (daw.getException() == null) {
//					File[] files = Readsy.getContentDir().listFiles();
//					if (files != null && files.length > 0) {
//						int response = JOptionPane.showConfirmDialog(this,
//								bundle.getString("PreferencesDialog.joption.copyData.message"),
//								bundle.getString("PreferencesDialog.joption.copyData.title"),
//								JOptionPane.YES_NO_OPTION,
//								JOptionPane.QUESTION_MESSAGE);
//						if (response == JOptionPane.YES_OPTION) {
//							DropboxCopyWorker dcw = new DropboxCopyWorker();
//							wd = new WorkerDialog(this, dcw, bundle.getString("worker.copying.title"), "");
//							wd.executeAndShowDialog();
//
//							String message = bundle.getString("PreferencesDialog.joption.syncEnabled.message1");
//							if (dcw.getException() != null) {
//								logger.error("Error copying files to dropbox.", dcw.getException());
//								message += "\n" + bundle.getString("PreferencesDialog.joption.syncEnabled.message2");
//							}
//								JOptionPane.showMessageDialog(this,
//										message,
//										bundle.getString("PreferencesDialog.joption.syncEnabled.title"),
//										JOptionPane.INFORMATION_MESSAGE);
//						}
//					} else {
//						JOptionPane.showMessageDialog(this,
//								bundle.getString("PreferencesDialog.joption.syncEnabled.message1"),
//								bundle.getString("PreferencesDialog.joption.syncEnabled.title"),
//								JOptionPane.INFORMATION_MESSAGE);
//					}
//					Readsy.getMainWindow().createTabs();
//				} else {
//					// error during authorization, so uncheck the checkbox
////					this.cbxDropbox.setSelected(false);
//					logger.error("Error during dropbox authorization.", daw.getException());
//					JOptionPane.showMessageDialog(this,
//							bundle.getString("PreferencesDialog.joption.authorizeError.message1") +
//									daw.getException().getMessage() + bundle.getString("PreferencesDialog.joption.authorizeError.message2"),
//							bundle.getString("PreferencesDialog.joption.authorizeError.title"),
//							JOptionPane.ERROR_MESSAGE
//					);
//				}
//			}
//		} else {
//			String[] options = {
//					bundle.getString("PreferencesDialog.joption.syncDisable.optionCancel"),
//					bundle.getString("PreferencesDialog.joption.syncDisable.optionNoDelete"),
//					bundle.getString("PreferencesDialog.joption.syncDisable.optionDelete")
//			};
//			int option = JOptionPane.showOptionDialog(this,
//					bundle.getString("PreferencesDialog.joption.syncDisable.message"),
//					bundle.getString("PreferencesDialog.joption.syncDisable.title"),
//					JOptionPane.DEFAULT_OPTION,
//					JOptionPane.QUESTION_MESSAGE,
//					null,
//					options,
//					options[2]);
//
//			switch (option) {
//				case 0:
////					this.cbxDropbox.setSelected(true);
//					break;
//				case 1:
//					PropertyManager.getInstance().deleteProperty(PropertyManager.DROPBOX_ACCESS_TOKEN);
//					PropertyManager.getInstance().setProperty(PropertyManager.DROPBOX_ENABLED, "false");
//					JOptionPane.showMessageDialog(this,
//							bundle.getString("PreferencesDialog.joption.syncDisable.disableNoDeleteMessage"),
//							bundle.getString("PreferencesDialog.joption.syncDisabled.title"),
//							JOptionPane.INFORMATION_MESSAGE);
//					Readsy.getMainWindow().createTabs();
//					break;
//				case 2:
//					DropboxDeleteContentWorker d = new DropboxDeleteContentWorker();
//					WorkerDialog wd = new WorkerDialog(this, d, bundle.getString("worker.deleting"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
//					wd.executeAndShowDialog();
//
//					String message = bundle.getString("PreferencesDialog.joption.syncDisabled.message1");
//					if (d.getException() != null) {
//						logger.error("Error deleting files from dropbox.", d.getException());
//						message += "\n" + bundle.getString("PreferencesDialog.joption.syncDisabled.message2");
//					}
//					JOptionPane.showMessageDialog(this,
//							message,
//							bundle.getString("PreferencesDialog.joption.syncDisabled.title"),
//							JOptionPane.INFORMATION_MESSAGE);
//					Readsy.getMainWindow().createTabs();
//					break;
//			}
//		}
//	}

  private void button1ActionPerformed() {
	  int choice = JOptionPane.showConfirmDialog(this,
        bundle.getString("PreferencesDialog.disconnect.message"),
        bundle.getString("PreferencesDialog.disconnect.title"),
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
	  if (choice == JOptionPane.YES_OPTION) {
	    PropertyManager.getInstance().deleteProperty(PropertyManager.DROPBOX_ACCESS_TOKEN);
	    System.exit(0);
    }
  }

	public PreferencesDialog(Frame parent, boolean modal) {
		super(parent, modal);

		initComponents();
		this.cbxUpdates.setSelected(PropertyManager.getInstance().getPropertyAsBoolean(PropertyManager.PROPERTY_CHECK_FOR_UPDATES));
		this.initialFontSize = PropertyManager.getInstance().getPropertyAsInt(PropertyManager.PROPERTY_FONT_SIZE);
		this.cmbFont.setSelectedItem(Integer.toString(this.initialFontSize));
		if (PropertyManager.getInstance().getProperty(PropertyManager.DROPBOX_ACCESS_TOKEN) == null) {
		  panel4.setVisible(false);
    } else {
		  panel4.setVisible(true);
    }
		setIconImage(Readsy.WINDOW_IMAGE);

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
    textArea2 = new JTextArea();
    panel3 = new JPanel();
    cmbFont = new JComboBox<>();
    panel4 = new JPanel();
    textArea1 = new JTextArea();
    button1 = new JButton();
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
      panel2.setLayout(new GridBagLayout());
      ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
      ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

      //======== jPanel1 ========
      {
        jPanel1.setBorder(new TitledBorder(bundle.getString("PreferencesDialog.jPanel1.border")));
        jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));

        //---- cbxUpdates ----
        cbxUpdates.setText(bundle.getString("PreferencesDialog.cbxUpdates.text"));
        cbxUpdates.setBorder(BorderFactory.createEmptyBorder());
        cbxUpdates.setMargin(new Insets(0, 0, 0, 0));
        jPanel1.add(cbxUpdates);
      }
      panel2.add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));

      //======== jPanel3 ========
      {
        jPanel3.setBorder(new TitledBorder(bundle.getString("PreferencesDialog.jPanel3.border")));
        jPanel3.setLayout(new BorderLayout());

        //---- textArea2 ----
        textArea2.setBackground(UIManager.getColor("Label.background"));
        textArea2.setEditable(false);
        textArea2.setLineWrap(true);
        textArea2.setWrapStyleWord(true);
        textArea2.setText(bundle.getString("PreferencesDialog.textArea2.text"));
        jPanel3.add(textArea2, BorderLayout.CENTER);

        //======== panel3 ========
        {
          panel3.setLayout(new FlowLayout(FlowLayout.LEFT));

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
          panel3.add(cmbFont);
        }
        jPanel3.add(panel3, BorderLayout.LINE_START);
      }
      panel2.add(jPanel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));

      //======== panel4 ========
      {
        panel4.setBorder(new TitledBorder(bundle.getString("PreferencesDialog.panel4.border")));
        panel4.setLayout(new BorderLayout());

        //---- textArea1 ----
        textArea1.setEditable(false);
        textArea1.setWrapStyleWord(true);
        textArea1.setLineWrap(true);
        textArea1.setText(bundle.getString("PreferencesDialog.textArea1.text"));
        textArea1.setBackground(UIManager.getColor("Label.background"));
        panel4.add(textArea1, BorderLayout.CENTER);

        //---- button1 ----
        button1.setText(bundle.getString("PreferencesDialog.button1.text"));
        button1.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            button1ActionPerformed();
          }
        });
        panel4.add(button1, BorderLayout.SOUTH);
      }
      panel2.add(panel4, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
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
    setSize(440, 370);
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
		if (tabs != null) {
      for (TabPanel tab : tabs) {
        tab.updateFontSize(size);
      }
    }
	}//GEN-LAST:event_cmbFontActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
  private JPanel panel2;
  private JPanel jPanel1;
  private JCheckBox cbxUpdates;
  private JPanel jPanel3;
  private JTextArea textArea2;
  private JPanel panel3;
  private JComboBox<String> cmbFont;
  private JPanel panel4;
  private JTextArea textArea1;
  private JButton button1;
  private JPanel panel1;
  private JButton btnOk;
  private JButton btnCancel;
	// End of variables declaration//GEN-END:variables

}
