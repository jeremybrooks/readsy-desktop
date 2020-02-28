/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2020  Jeremy Brooks
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

import javax.swing.*;
import net.jeremybrooks.readsy.Readsy;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.ResourceBundle;


/**
 * The about dialog box.
 * It should display the splash screen logo, the version, and a brief message
 * including contact information and license.
 *
 * @author Jeremy Brooks
 */
public class AboutDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 3396224981175261460L;

	private ResourceBundle bundle = ResourceBundle.getBundle("localization.about_dialog");

	/**
	 * Creates new form AboutDialog
	 *
	 * @param parent parent of this dialog.
	 * @param modal  should this be presented modally.
	 */
	public AboutDialog(Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		setIconImage(Readsy.WINDOW_IMAGE);

		// add some data to credits
		StringBuilder sb = new StringBuilder(bundle.getString("versionLabel.text"));
		sb.append(' ').append(Readsy.VERSION).append("\n\n");

		sb.append(this.helpTextArea.getText());
		if (Readsy.getMainWindow().getTabList().size() > 0) {
			for (TabPanel tp : Readsy.getMainWindow().getTabList()) {
				sb.append(tp.getStats()).append('\n');
			}
		} else {
			sb.append(bundle.getString("nothingInstalled"));
		}
		this.helpTextArea.setText(sb.toString());

		// put the cursor at the beginning of the about stuff
		this.licenseTextArea.setCaretPosition(0);
		this.helpTextArea.setCaretPosition(0);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = this.getPreferredSize();
		setLocation(screenSize.width / 2 - (windowSize.width / 2),
				screenSize.height / 2 - (windowSize.height / 2));
	}


	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    ResourceBundle bundle = bundle = this.bundle;;
    panel1 = new JPanel();
    iconLabel = new JLabel();
    jTabbedPane1 = new JTabbedPane();
    jScrollPane3 = new JScrollPane();
    helpTextArea = new JTextArea();
    jScrollPane2 = new JScrollPane();
    licenseTextArea = new JTextArea();
    tabbedPane1 = new JTabbedPane();
    scrollPane1 = new JScrollPane();
    apacheLicenseTextArea = new JTextArea();
    scrollPane2 = new JScrollPane();
    bsdLicenseTextArea = new JTextArea();
    scrollPane4 = new JScrollPane();
    gplLicenseTextArea = new JTextArea();
    scrollPane3 = new JScrollPane();
    mitLicenseTextArea = new JTextArea();
    panel2 = new JPanel();
    homePageButton = new JButton();
    okButton = new JButton();

    //======== this ========
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(bundle.getString("this.title"));
    setAlwaysOnTop(true);
    setModal(true);
    var contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBackground(Color.white);
      panel1.setLayout(new BorderLayout());

      //---- iconLabel ----
      iconLabel.setIcon(new ImageIcon(getClass().getResource("/images/icon500x300.png")));
      iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
      panel1.add(iconLabel, BorderLayout.CENTER);
    }
    contentPane.add(panel1, BorderLayout.NORTH);

    //======== jTabbedPane1 ========
    {

      //======== jScrollPane3 ========
      {

        //---- helpTextArea ----
        helpTextArea.setColumns(20);
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setRows(5);
        helpTextArea.setText(bundle.getString("helpTextArea.text"));
        helpTextArea.setWrapStyleWord(true);
        jScrollPane3.setViewportView(helpTextArea);
      }
      jTabbedPane1.addTab(bundle.getString("jScrollPane3.tab.title"), jScrollPane3);

      //======== jScrollPane2 ========
      {

        //---- licenseTextArea ----
        licenseTextArea.setEditable(false);
        licenseTextArea.setLineWrap(true);
        licenseTextArea.setRows(15);
        licenseTextArea.setText(bundle.getString("licenseTextArea.text"));
        licenseTextArea.setWrapStyleWord(true);
        jScrollPane2.setViewportView(licenseTextArea);
      }
      jTabbedPane1.addTab(bundle.getString("jScrollPane2.tab.title"), jScrollPane2);

      //======== tabbedPane1 ========
      {

        //======== scrollPane1 ========
        {

          //---- apacheLicenseTextArea ----
          apacheLicenseTextArea.setEditable(false);
          apacheLicenseTextArea.setWrapStyleWord(true);
          apacheLicenseTextArea.setText(bundle.getString("apacheLicenseTextArea.text"));
          scrollPane1.setViewportView(apacheLicenseTextArea);
        }
        tabbedPane1.addTab("Apache 2.0 License", scrollPane1);

        //======== scrollPane2 ========
        {

          //---- bsdLicenseTextArea ----
          bsdLicenseTextArea.setWrapStyleWord(true);
          bsdLicenseTextArea.setEditable(false);
          bsdLicenseTextArea.setText(bundle.getString("bsdLicenseTextArea.text"));
          scrollPane2.setViewportView(bsdLicenseTextArea);
        }
        tabbedPane1.addTab("BSD Licenses", scrollPane2);

        //======== scrollPane4 ========
        {

          //---- gplLicenseTextArea ----
          gplLicenseTextArea.setWrapStyleWord(true);
          gplLicenseTextArea.setEditable(false);
          gplLicenseTextArea.setText(bundle.getString("gplLicenseTextArea.text"));
          scrollPane4.setViewportView(gplLicenseTextArea);
        }
        tabbedPane1.addTab("GPLv3 License", scrollPane4);

        //======== scrollPane3 ========
        {

          //---- mitLicenseTextArea ----
          mitLicenseTextArea.setEditable(false);
          mitLicenseTextArea.setWrapStyleWord(true);
          mitLicenseTextArea.setText(bundle.getString("mitLicenseTextArea.text"));
          scrollPane3.setViewportView(mitLicenseTextArea);
        }
        tabbedPane1.addTab("MIT License", scrollPane3);
      }
      jTabbedPane1.addTab("Library Licenses", tabbedPane1);
    }
    contentPane.add(jTabbedPane1, BorderLayout.CENTER);

    //======== panel2 ========
    {
      panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));

      //---- homePageButton ----
      homePageButton.setText(bundle.getString("homePageButton.text"));
      homePageButton.addActionListener(e -> homePageButtonActionPerformed());
      panel2.add(homePageButton);

      //---- okButton ----
      okButton.setText(bundle.getString("okButton.text"));
      okButton.addActionListener(e -> jButton1ActionPerformed());
      panel2.add(okButton);
    }
    contentPane.add(panel2, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(getOwner());
	}// </editor-fold>//GEN-END:initComponents


	private void homePageButtonActionPerformed() {
		Readsy.getMainWindow().openHomePage();
	}


	private void jButton1ActionPerformed() {//GEN-FIRST:event_jButton1ActionPerformed
		this.setVisible(false);
		this.dispose();
	}//GEN-LAST:event_jButton1ActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
  private JPanel panel1;
  private JLabel iconLabel;
  private JTabbedPane jTabbedPane1;
  private JScrollPane jScrollPane3;
  private JTextArea helpTextArea;
  private JScrollPane jScrollPane2;
  private JTextArea licenseTextArea;
  private JTabbedPane tabbedPane1;
  private JScrollPane scrollPane1;
  private JTextArea apacheLicenseTextArea;
  private JScrollPane scrollPane2;
  private JTextArea bsdLicenseTextArea;
  private JScrollPane scrollPane4;
  private JTextArea gplLicenseTextArea;
  private JScrollPane scrollPane3;
  private JTextArea mitLicenseTextArea;
  private JPanel panel2;
  private JButton homePageButton;
  private JButton okButton;
	// End of variables declaration//GEN-END:variables
}
