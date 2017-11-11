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

import net.jeremybrooks.common.gui.WorkerDialog;
import net.jeremybrooks.readsy.BitHelper;
import net.jeremybrooks.readsy.DataAccess;
import net.jeremybrooks.readsy.PropertyManager;
import net.jeremybrooks.readsy.Readsy;
import net.jeremybrooks.readsy.bo.Entry;
import org.apache.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.ResourceBundle;

import static net.jeremybrooks.readsy.Constants.KEY_METADATA_DESCRIPTION;
import static net.jeremybrooks.readsy.Constants.KEY_METADATA_READ;
import static net.jeremybrooks.readsy.Constants.KEY_METADATA_SHORT_DESCRIPTION;
import static net.jeremybrooks.readsy.Constants.KEY_METADATA_YEAR;

/**
 * A panel representing data for a Readsy XML file.
 *
 * @author Jeremy Brooks
 */
public class TabPanel extends javax.swing.JPanel {

  private static final long serialVersionUID = -1711827901008647020L;
  private Logger logger = Logger.getLogger(TabPanel.class);
  /* The current date displayed by this panel. */
  private Date currentDate = null;
  /* The index of this tab in the tab pane. */
  private int index;
  /* The tab pane that contains this tab panel. */
  private JTabbedPane tabbedPane;
  /* The content directory for this tab */
  private String contentDirectory;
  /* The metadata about this content */
  private Properties metadata;
  private BitHelper bitHelper;
  private ResourceBundle bundle = ResourceBundle.getBundle("localization.tab_panel");


  /* Default constructor is private to force use of other constructors. */
  private TabPanel() {
  }

  public TabPanel(String contentDirectory, Properties metadata) {
    this.contentDirectory = contentDirectory;
    this.metadata = metadata;
    this.bitHelper = new BitHelper(metadata.getProperty(KEY_METADATA_READ));
    initComponents();
    updateFontSize(PropertyManager.getInstance().getPropertyAsInt(PropertyManager.PROPERTY_FONT_SIZE));
  }


  /**
   * Get the short description from the root element.
   *
   * @return short description attribute.
   */
  public String getShortDescription() {
    return this.metadata.getProperty(KEY_METADATA_SHORT_DESCRIPTION);
  }

  public String getDescription() {
    return this.metadata.getProperty(KEY_METADATA_DESCRIPTION);
  }


  /**
   * Get the tab title.
   * The tab title is the short description with the unread item count.
   *
   * @return tab title.
   */
  public String getTabTitle() {
    int unreadItemCount = getUnreadItemCount();
    if (unreadItemCount == 0) {
      return this.getShortDescription();
    } else {
      return this.getShortDescription() + " [" + unreadItemCount + "]";
    }
  }

  public int getUnreadItemCount() {
    return bitHelper.getUnreadItemCount(new Date(), metadata.getProperty(KEY_METADATA_YEAR));
  }

  public int getReadItemCount() {
    int count = 0;
    Calendar calendar = new GregorianCalendar();
    String year = metadata.getProperty(KEY_METADATA_YEAR);
    if (year.equals("0")) {
      calendar.set(Calendar.YEAR, 2013);
    } else {
      calendar.set(Calendar.YEAR, Integer.parseInt(year));
    }
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    do {
      if (bitHelper.isRead(calendar.getTime())) {
        count++;
      }
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    } while (calendar.get(Calendar.DAY_OF_YEAR) != 1);
    return count;
  }


  public int getTotalItemCount() {
    Calendar calendar = new GregorianCalendar();
    String year = metadata.getProperty(KEY_METADATA_YEAR);
    // for a "0" year file, set year to a non-leap year. Otherwise, set it to the year in the metadata
    // then return the number of days in the year
    if (year.equals("0")) {
      calendar.set(Calendar.YEAR, 2013);
    } else {
      calendar.set(Calendar.YEAR, Integer.parseInt(year));
    }
    return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
  }


  public Date getFirstUnreadItemDate() {
    Date date = null;
    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
    calendar.set(Calendar.DAY_OF_MONTH, 31);
    do {
      calendar.add(Calendar.DAY_OF_YEAR, -1);
      if (!bitHelper.isRead(calendar.getTime())) {
        date = calendar.getTime();
      }
    } while (calendar.get(Calendar.DAY_OF_YEAR) != 1);

    return date;
  }

  /**
   * Update the title.
   */
  public void updateTabTitle() {
    this.tabbedPane.setTitleAt(this.index, this.getTabTitle());
    this.tabbedPane.setToolTipTextAt(this.index, this.getDescription());
  }


  /**
   * Set the index.
   *
   * @param index the index.
   */
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * Set the tabbed pane that contains this tab panel.
   *
   * @param tabbedPane the JTabbedPane that contains this tab panel.
   */
  public void setTabbedPane(JTabbedPane tabbedPane) {
    this.tabbedPane = tabbedPane;
  }


  public void updateFontSize(float size) {
    txtHeading.setFont(txtHeading.getFont().deriveFont(size));
    txtText.setFont(txtText.getFont().deriveFont(size));
  }


  /**
   * Display data for a specific date.
   *
   * @param date the date to display data for.
   */
  public void displayDataForDate(Date date) {
    this.logger.debug("Trying to find entry for date " + date.toString());
    this.currentDate = date;

    Entry entry = DataAccess.getEntryForDateFromDirectory(date, this.metadata, this.contentDirectory);
    if (entry == null) {
      this.txtHeading.setText(bundle.getString("TabPanel.noEntry"));
      this.txtText.setText("");
      this.cbxRead.setSelected(false);
      this.cbxRead.setEnabled(false);
    } else {
      this.txtHeading.setText(entry.getHeading());
      this.txtText.setText(entry.getText());
      this.cbxRead.setSelected(this.bitHelper.isRead(date));
      this.cbxRead.setEnabled(true);
    }

    // THIS FORCES THE VIEWPORT TO SCROLL TO THE TOP OF THE NEWLY ADDED TEXT
    // setCaretPosition() DID NOT SEEM TO WORK, THIS DOES.
    this.txtText.setSelectionStart(1);
  }


  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    ResourceBundle bundle = this.bundle;
    lblDescription = new JLabel();
    txtHeading = new JTextArea();
    jScrollPane1 = new JScrollPane();
    txtText = new JTextArea();
    cbxRead = new JCheckBox();
    mnuPopup = new JPopupMenu();
    mnuPrevious = new JMenuItem();
    mnuToday = new JMenuItem();
    mnuNext = new JMenuItem();
    jSeparator1 = new JPopupMenu.Separator();
    mnuRead = new JMenuItem();
    jSeparator2 = new JPopupMenu.Separator();
    mnuDefine = new JMenuItem();

    //======== this ========
    setLayout(new GridBagLayout());

    //---- lblDescription ----
    lblDescription.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
    add(lblDescription, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- txtHeading ----
    txtHeading.setEditable(false);
    txtHeading.setLineWrap(true);
    txtHeading.setWrapStyleWord(true);
    txtHeading.setMargin(new Insets(3, 3, 3, 3));
    txtHeading.setFont(new Font("SansSerif", txtHeading.getFont().getStyle(), txtHeading.getFont().getSize()));
    add(txtHeading, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));

    //======== jScrollPane1 ========
    {
      jScrollPane1.setAutoscrolls(true);

      //---- txtText ----
      txtText.setEditable(false);
      txtText.setLineWrap(true);
      txtText.setWrapStyleWord(true);
      txtText.setMargin(new Insets(3, 3, 3, 3));
      txtText.setFont(new Font("SansSerif", txtText.getFont().getStyle(), txtText.getFont().getSize()));
      txtText.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          txtTextMousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          txtTextMouseReleased(e);
        }
      });
      jScrollPane1.setViewportView(txtText);
    }
    add(jScrollPane1, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- cbxRead ----
    cbxRead.setText(bundle.getString("TabPanel.cbxRead.text"));
    cbxRead.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cbxReadActionPerformed();
      }
    });
    add(cbxRead, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //======== mnuPopup ========
    {
      mnuPopup.setLayout(new GridLayout(0, 1));

      //---- mnuPrevious ----
      mnuPrevious.setIcon(new ImageIcon(getClass().getResource("/images/765-arrow-left_16.png")));
      mnuPrevious.setText(bundle.getString("TabPanel.mnuPrevious.text"));
      mnuPrevious.setToolTipText(bundle.getString("TabPanel.mnuPrevious.toolTipText"));
      mnuPrevious.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mnuPreviousActionPerformed();
        }
      });
      mnuPopup.add(mnuPrevious);

      //---- mnuToday ----
      mnuToday.setIcon(new ImageIcon(getClass().getResource("/images/750-home_16.png")));
      mnuToday.setText(bundle.getString("TabPanel.mnuToday.text"));
      mnuToday.setToolTipText(bundle.getString("TabPanel.mnuToday.toolTipText"));
      mnuToday.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mnuTodayActionPerformed();
        }
      });
      mnuPopup.add(mnuToday);

      //---- mnuNext ----
      mnuNext.setIcon(new ImageIcon(getClass().getResource("/images/766-arrow-right_16.png")));
      mnuNext.setText(bundle.getString("TabPanel.mnuNext.text"));
      mnuNext.setToolTipText(bundle.getString("TabPanel.mnuNext.toolTipText"));
      mnuNext.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mnuNextActionPerformed();
        }
      });
      mnuPopup.add(mnuNext);
      mnuPopup.add(jSeparator1);

      //---- mnuRead ----
      mnuRead.setSelected(true);
      mnuRead.setText(bundle.getString("TabPanel.mnuRead.text"));
      mnuRead.setIcon(new ImageIcon(getClass().getResource("/images/888-checkmark_16.png")));
      mnuRead.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mnuReadActionPerformed();
        }
      });
      mnuPopup.add(mnuRead);
      mnuPopup.add(jSeparator2);

      //---- mnuDefine ----
      mnuDefine.setIcon(new ImageIcon(getClass().getResource("/images/721-bookmarks_16.png")));
      mnuDefine.setText(bundle.getString("TabPanel.mnuDefine.text"));
      mnuDefine.setToolTipText(bundle.getString("TabPanel.mnuDefine.toolTipText"));
      mnuDefine.setName("define");
      mnuDefine.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mnuDefineActionPerformed();
        }
      });
      mnuPopup.add(mnuDefine);
    }
  }// </editor-fold>//GEN-END:initComponents


  /**
   * Handle when a user clicks the "I've read it" checkbox.
   * <p>This action creates a new instance of UpdateReadFlag, executing that
   * in a SwingWorker.</p>
   */
  private void cbxReadActionPerformed() {//GEN-FIRST:event_cbxReadActionPerformed
    WorkerDialog wd = new WorkerDialog(Readsy.getMainWindow(), new UpdateReadFlag(), bundle.getString("worker.saving"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
    wd.executeAndShowDialog();
  }//GEN-LAST:event_cbxReadActionPerformed

  private void txtTextMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTextMousePressed
    if (evt.isPopupTrigger()) {
      this.showPopup(evt);
    } else {
      if (evt.getClickCount() == 2) {
        // nothing to do on a double click for now
      }
    }
  }//GEN-LAST:event_txtTextMousePressed


  public void showPopup(MouseEvent e) {
    if (e.getComponent() instanceof JTextArea) {
      // set the state of the checkbox and define menu
      for (Component c : this.mnuPopup.getComponents()) {
        if (c instanceof JCheckBoxMenuItem) {
          ((JCheckBoxMenuItem) c).setSelected(this.cbxRead.isSelected());
        } else if (c instanceof JMenuItem) {
          JMenuItem menu = (JMenuItem) c;
          String name = menu.getName();
          if (name != null && name.equals("define")) {
            if (this.getWordToDefine() == null) {
              menu.setText(bundle.getString("MainWindow.defineMenu.text"));
              menu.setEnabled(false);
            } else {
              menu.setText(bundle.getString("MainWindow.defineMenu.text") + " \"" + this.getWordToDefine() + "\"");
              menu.setEnabled(true);
            }
          }
        }
      }
      this.mnuPopup.show(e.getComponent(), e.getX(), e.getY());
    }
  }


  /**
   * Get the selected word.
   * <p>The user must have only one word selected. If there are spaces in the
   * selection, it is considered more than one word and will return null.
   * Punctuation and numbers are ignored and will not be returned as part of
   * the word.</p>
   *
   * @return the selected word, or null if a valid selection is not found.
   */
  public String getWordToDefine() {
    String selection = this.txtText.getSelectedText();
    String retString = null;
    if (selection == null || selection.isEmpty()) {
      selection = this.txtHeading.getSelectedText();
    }
    if (selection != null && !selection.isEmpty()) {
      // only continue if there are no spaces in the selection
      if (!selection.contains(" ")) {
        // get rid of all characters that are not letters
        StringBuilder sb = new StringBuilder();
        for (char c : selection.toCharArray()) {
          if (Character.isLetter(c)) {
            sb.append(c);
          }
        }
        // if there are still characters, we have a word to define
        if (sb.length() > 0) {
          retString = sb.toString();
        }
      }
    }

    return retString;
  }


  private void mnuNextActionPerformed() {//GEN-FIRST:event_mnuNextActionPerformed
    Readsy.getMainWindow().next(MainWindow.DateChangeMode.DAY);
  }//GEN-LAST:event_mnuNextActionPerformed

  private void mnuTodayActionPerformed() {//GEN-FIRST:event_mnuTodayActionPerformed
    Readsy.getMainWindow().today();
  }//GEN-LAST:event_mnuTodayActionPerformed

  private void mnuPreviousActionPerformed() {//GEN-FIRST:event_mnuPreviousActionPerformed
    Readsy.getMainWindow().previous(MainWindow.DateChangeMode.DAY);
  }//GEN-LAST:event_mnuPreviousActionPerformed

  private void mnuReadActionPerformed() {//GEN-FIRST:event_mnuReadActionPerformed
    // toggle the state of the checkbox
    this.cbxRead.setSelected(!cbxRead.isSelected());

    // then trigger the action
    this.cbxReadActionPerformed();
  }//GEN-LAST:event_mnuReadActionPerformed

  private void mnuDefineActionPerformed() {//GEN-FIRST:event_mnuDefineActionPerformed
    DefinitionDialog def = new DefinitionDialog(Readsy.getMainWindow(), false);
    def.setVisible(true);
    def.define(this.getWordToDefine());
  }//GEN-LAST:event_mnuDefineActionPerformed

  private void txtTextMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTextMouseReleased
    if (evt.isPopupTrigger()) {
      this.showPopup(evt);
    } else {
      if (evt.getClickCount() == 2) {
        // nothing to do on a double click for now
      }
    }
  }//GEN-LAST:event_txtTextMouseReleased


  public String getStats() {
    StringBuilder sb = new StringBuilder();
    sb.append(metadata.getProperty(KEY_METADATA_DESCRIPTION)).append(": ");
    sb.append(getReadItemCount()).append(" of ").append(getTotalItemCount()).append(" have been read.");
    return sb.toString();
  }

  public void markAllRead() {
    Calendar calendar = new GregorianCalendar();
    if (metadata.getProperty(KEY_METADATA_YEAR).equals("0")) {
      calendar.set(Calendar.YEAR, 2013);
    } else {
      calendar.set(Calendar.YEAR, Integer.parseInt(metadata.getProperty(KEY_METADATA_YEAR)));
    }
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    do {
      bitHelper.setRead(calendar.getTime(), true);
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    } while (calendar.get(Calendar.DAY_OF_YEAR) != 1);
    metadata.setProperty(KEY_METADATA_READ, bitHelper.toString());
    WorkerDialog wd = new WorkerDialog(Readsy.getMainWindow(), new SaveMetadata(), bundle.getString("worker.saving"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
    wd.executeAndShowDialog();
    this.displayDataForDate(this.currentDate);
  }

  public void markAllUnread() {
    Calendar calendar = new GregorianCalendar();
    if (metadata.getProperty(KEY_METADATA_YEAR).equals("0")) {
      calendar.set(Calendar.YEAR, 2013);
    } else {
      calendar.set(Calendar.YEAR, Integer.parseInt(metadata.getProperty(KEY_METADATA_YEAR)));
    }
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    do {
      bitHelper.setRead(calendar.getTime(), false);
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    } while (calendar.get(Calendar.DAY_OF_YEAR) != 1);
    metadata.setProperty(KEY_METADATA_READ, bitHelper.toString());
    WorkerDialog wd = new WorkerDialog(Readsy.getMainWindow(), new SaveMetadata(), bundle.getString("worker.saving"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
    wd.executeAndShowDialog();
    this.displayDataForDate(this.currentDate);
  }

  public void markReadUpToDate() {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(currentDate);
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    Calendar stopAfter = new GregorianCalendar();
    stopAfter.setTime(currentDate);
    do {
      bitHelper.setRead(calendar.getTime(), true);
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    } while (calendar.before(stopAfter) || calendar.equals(stopAfter));
    metadata.setProperty(KEY_METADATA_READ, bitHelper.toString());
    WorkerDialog wd = new WorkerDialog(Readsy.getMainWindow(), new SaveMetadata(), bundle.getString("worker.saving"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
    wd.executeAndShowDialog();
    this.displayDataForDate(this.currentDate);
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JLabel lblDescription;
  private JTextArea txtHeading;
  private JScrollPane jScrollPane1;
  private JTextArea txtText;
  private JCheckBox cbxRead;
  private JPopupMenu mnuPopup;
  private JMenuItem mnuPrevious;
  private JMenuItem mnuToday;
  private JMenuItem mnuNext;
  private JPopupMenu.Separator jSeparator1;
  private JMenuItem mnuRead;
  private JPopupMenu.Separator jSeparator2;
  private JMenuItem mnuDefine;
  // End of variables declaration//GEN-END:variables


  /**
   * This is the work done when a user clicks on the
   * "I've read it" checkbox.  This work is executed by a SwingWorker.
   */
  class UpdateReadFlag extends javax.swing.SwingWorker<Void, Void> {
    @Override
    protected Void doInBackground() throws Exception {
      try {
        bitHelper.setRead(currentDate, cbxRead.isSelected());
        metadata.setProperty(KEY_METADATA_READ, bitHelper.toString());
        DataAccess.saveMetadata(contentDirectory, metadata);
        updateTabTitle();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(Readsy.getMainWindow(),
            bundle.getString("TabPanel.joption.saveError.message"),
            bundle.getString("TabPanel.joption.saveError.title"),
            JOptionPane.INFORMATION_MESSAGE);
      }
      return null;
    }
  }

  /**
   * Class to save the metadata in a background thread.
   */
  class SaveMetadata extends javax.swing.SwingWorker<Void, Void> {
    @Override
    protected Void doInBackground() throws Exception {
      try {
        DataAccess.saveMetadata(contentDirectory, metadata);
        updateTabTitle();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(Readsy.getMainWindow(),
            bundle.getString("TabPanel.joption.saveError.message"),
            bundle.getString("TabPanel.joption.saveError.title"),
            JOptionPane.INFORMATION_MESSAGE);
      }
      return null;
    }
  }

}
