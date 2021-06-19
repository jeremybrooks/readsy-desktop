/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2021  Jeremy Brooks
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

/*
 * Created by JFormDesigner on Sat Nov 16 01:26:17 PST 2013
 */

package net.jeremybrooks.readsy.gui;

import net.jeremybrooks.readsy.PropertyManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import static net.jeremybrooks.readsy.Constants.WINDOW_IMAGE;

/**
 * @author Jeremy Brooks
 */
public class WelcomeDialog extends JDialog {

	public WelcomeDialog() {
		super();
		initComponents();
		setIconImage(WINDOW_IMAGE);
		this.getRootPane().setDefaultButton(this.buttonYes);
	}

	private void buttonYesActionPerformed() {
    JFileChooser jfc = new JFileChooser();
    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    jfc.setMultiSelectionEnabled(false);

    int selection = jfc.showOpenDialog(this);
    if (selection == JFileChooser.APPROVE_OPTION) {
      PropertyManager propertyManager = PropertyManager.getInstance();
      propertyManager.setProperty(PropertyManager.READSY_FILE_DIRECTORY,
          jfc.getSelectedFile().getAbsolutePath());
      this.setVisible(false);
      this.dispose();
      SwingUtilities.invokeLater(() -> MainWindow.instance.setVisible(true, true));
    }
	}

  private void buttonCancelActionPerformed() {
	  System.exit(0);
  }

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    ResourceBundle bundle = ResourceBundle.getBundle("localization.welcome_dialog");
    dialogPane = new JPanel();
    contentPanel = new JPanel();
    logoLabel = new JLabel();
    scrollPane1 = new JScrollPane();
    textPane1 = new JTextPane();
    buttonBar = new JPanel();
    buttonCancel = new JButton();
    buttonYes = new JButton();

    //======== this ========
    var contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

        //---- logoLabel ----
        logoLabel.setIcon(new ImageIcon(getClass().getResource("/images/icon64.png")));
        logoLabel.setVerticalAlignment(SwingConstants.TOP);
        logoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        contentPanel.add(logoLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));

        //======== scrollPane1 ========
        {

          //---- textPane1 ----
          textPane1.setText(bundle.getString("WelcomeDialog.textPane1.text"));
          textPane1.setEditable(false);
          scrollPane1.setViewportView(textPane1);
        }
        contentPanel.add(scrollPane1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(5, 5, 5, 5), 0, 0));
      }
      dialogPane.add(contentPanel, BorderLayout.CENTER);

      //======== buttonBar ========
      {
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        buttonBar.setLayout(new GridBagLayout());
        ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 80};
        ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

        //---- buttonCancel ----
        buttonCancel.setText(bundle.getString("WelcomeDialog.buttonCancel.text"));
        buttonCancel.addActionListener(e -> buttonCancelActionPerformed());
        buttonBar.add(buttonCancel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- buttonYes ----
        buttonYes.setText(bundle.getString("WelcomeDialog.buttonYes.text"));
        buttonYes.addActionListener(e -> buttonYesActionPerformed());
        buttonBar.add(buttonYes, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      dialogPane.add(buttonBar, BorderLayout.SOUTH);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    setSize(405, 300);
    setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}


	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JPanel dialogPane;
  private JPanel contentPanel;
  private JLabel logoLabel;
  private JScrollPane scrollPane1;
  private JTextPane textPane1;
  private JPanel buttonBar;
  private JButton buttonCancel;
  private JButton buttonYes;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
