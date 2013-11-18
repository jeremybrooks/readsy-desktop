/*
 * readsy - read something new every day
 *
 *     Copyright (C) 2103  Jeremy Brooks
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
 *     You may contact the program's author at jeremyb@whirljack.net
 */

/*
 * Created by JFormDesigner on Sat Nov 16 01:26:17 PST 2013
 */

package net.jeremybrooks.readsy.gui;

import net.jeremybrooks.readsy.Readsy;
import net.jeremybrooks.readsy.gui.workers.DropboxAuthWorker;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

/**
 * @author Jeremy Brooks
 */
public class WelcomeDialog extends JDialog {

	public WelcomeDialog() {
		super();
		initComponents();
		this.getRootPane().setDefaultButton(this.buttonYes);
	}

	private void buttonYesActionPerformed() {
		DropboxAuthWorker daw = new DropboxAuthWorker();
		WorkerDialog wd = new WorkerDialog(null, daw, "Authorizing Dropbox", "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
		wd.executeAndShowDialog();
		this.setVisible(false);
		this.dispose();
		Readsy.getMainWindow().setVisible(true, true);
	}

	private void buttonNoActionPerformed() {
		this.setVisible(false);
		this.dispose();
		Readsy.getMainWindow().setVisible(true, true);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("localization.welcome_dialog");
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		logoLabel = new JLabel();
		textLabel = new JLabel();
		buttonBar = new JPanel();
		buttonNo = new JButton();
		buttonYes = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new GridBagLayout());
				((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
				((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
				((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
				((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

				//---- logoLabel ----
				logoLabel.setIcon(new ImageIcon(getClass().getResource("/images/Icon64.png")));
				logoLabel.setVerticalAlignment(SwingConstants.TOP);
				logoLabel.setHorizontalAlignment(SwingConstants.LEFT);
				contentPanel.add(logoLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));

				//---- textLabel ----
				textLabel.setIcon(null);
				textLabel.setText(bundle.getString("WelcomeDialog.textLabel.text"));
				textLabel.setVerticalAlignment(SwingConstants.TOP);
				contentPanel.add(textLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5), 0, 0));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- buttonNo ----
				buttonNo.setText(bundle.getString("WelcomeDialog.buttonNo.text"));
				buttonNo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonNoActionPerformed();
					}
				});
				buttonBar.add(buttonNo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

				//---- buttonYes ----
				buttonYes.setText(bundle.getString("WelcomeDialog.buttonYes.text"));
				buttonYes.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonYesActionPerformed();
					}
				});
				buttonBar.add(buttonYes, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(400, 300);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}


	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel logoLabel;
	private JLabel textLabel;
	private JPanel buttonBar;
	private JButton buttonNo;
	private JButton buttonYes;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
