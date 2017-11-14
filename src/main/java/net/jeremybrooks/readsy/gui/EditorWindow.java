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

import net.jeremybrooks.readsy.Constants;
import net.jeremybrooks.readsy.Readsy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * This window allows a user to edit the data XML file.
 * <p>Changes are saved whenever the user naviagtes to another day, or when the
 * user closes this window.</p>
 * <p>When this window is closed, the user is taken back to the main window.</p>
 *
 * @author Jeremy Brooks
 */
public class EditorWindow extends javax.swing.JFrame {

  private Logger logger = LogManager.getLogger(EditorWindow.class);
  private DateFormat prettyDateFormat = new SimpleDateFormat("MMMM dd");
  private DateFormat monthDateFormat = new SimpleDateFormat("MMdd");
  private Calendar calendar;
  private File directory;
  private Properties metadata;

  /**
   * Creates new form EditorWindow.
   * The location is set based on the location of the main window, and the
   * display is updated.  This should result in the first element of the
   * data file being shown, ready for editing.
   *
   * //	 * @param dataFile the object model to work with.
   */
  public EditorWindow(File directory, Properties metadata) {
    this.directory = directory;
    this.metadata = metadata;
    this.calendar = new GregorianCalendar();
    this.calendar.set(Calendar.MONTH, Calendar.JANUARY);
    this.calendar.set(Calendar.DAY_OF_MONTH, 1);
    if (this.metadata.getProperty("year").equals("0")) {
      this.calendar.set(Calendar.YEAR, 2013);
    } else {
      this.calendar.set(Calendar.YEAR, Integer.parseInt(metadata.getProperty("year")));
    }
    setCalendarToFirstEmptyEntry();
    initComponents();
    setIconImage(Readsy.WINDOW_IMAGE);
    this.lblFilename.setText(directory.getAbsolutePath());
    if (!metadata.getProperty("year").equals("0")) {
      this.lblYearValue.setText(metadata.getProperty("year"));
    }
    this.setLocation(Readsy.getMainWindow().getX(), Readsy.getMainWindow().getY());
    updateDisplay();
  }

  private void setCalendarToFirstEmptyEntry() {
    // find the first empty date and start there
    try {
      File[] files = directory.listFiles();
      if (files != null) {
        Arrays.sort(files, Comparator.comparing(File::getName));
        for (File f : files) {
          try (BufferedReader in = new BufferedReader(
              new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
              sb.append(line.trim());
            }
            if (sb.length() == 0) {
              // parse filename to determine month/day
              // month is zero-based, so subtract one
              this.calendar.set(Calendar.MONTH, Integer.parseInt(f.getName().substring(0, 2)) - 1);
              this.calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(f.getName().substring(2)));
              break;
            }
          }
        }
      }
    } catch (Exception e) {
      this.calendar.set(Calendar.MONTH, Calendar.JANUARY);
      this.calendar.set(Calendar.DAY_OF_MONTH, 1);
    }
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    ResourceBundle bundle = ResourceBundle.getBundle("localization.editor_window");
    label1 = new JLabel();
    jLabel1 = new JLabel();
    jLabel2 = new JLabel();
    txtDescription = new JTextField();
    txtShortDescription = new JTextField();
    lblDate = new JLabel();
    buttonPrevious = new JButton();
    buttonNext = new JButton();
    jLabel5 = new JLabel();
    txtHeading = new JTextField();
    jLabel6 = new JLabel();
    jScrollPane1 = new JScrollPane();
    txtText = new JTextArea();
    jLabel3 = new JLabel();
    lblFilename = new JLabel();
    lblYear = new JLabel();
    lblYearValue = new JLabel();
    doneButton = new JButton();

    //======== this ========
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle(bundle.getString("EditorWindow.this.title"));
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        formWindowClosing();
      }
    });
    Container contentPane = getContentPane();
    contentPane.setLayout(new GridBagLayout());

    //---- label1 ----
    label1.setIcon(new ImageIcon(getClass().getResource("/images/icon64.png")));
    contentPane.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));

    //---- jLabel1 ----
    jLabel1.setText(bundle.getString("EditorWindow.jLabel1.text"));
    contentPane.add(jLabel1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- jLabel2 ----
    jLabel2.setText(bundle.getString("EditorWindow.jLabel2.text"));
    contentPane.add(jLabel2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- txtDescription ----
    txtDescription.setColumns(40);
    txtDescription.setToolTipText(bundle.getString("EditorWindow.txtDescription.toolTipText"));
    this.txtDescription.setText(this.metadata.getProperty("description"));
    contentPane.add(txtDescription, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- txtShortDescription ----
    txtShortDescription.setColumns(40);
    txtShortDescription.setToolTipText(bundle.getString("EditorWindow.txtShortDescription.toolTipText"));
    this.txtShortDescription.setText(this.metadata.getProperty("shortDescription"));
    contentPane.add(txtShortDescription, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- lblDate ----
    lblDate.setText(bundle.getString("EditorWindow.lblDate.text"));
    contentPane.add(lblDate, new GridBagConstraints(2, 5, 1, 1, 1.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- buttonPrevious ----
    buttonPrevious.setIcon(new ImageIcon(getClass().getResource("/images/765-arrow-left_16.png")));
    buttonPrevious.setToolTipText(bundle.getString("EditorWindow.buttonPrevious.toolTipText"));
    buttonPrevious.addActionListener(e -> buttonPreviousActionPerformed());
    contentPane.add(buttonPrevious, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- buttonNext ----
    buttonNext.setIcon(new ImageIcon(getClass().getResource("/images/766-arrow-right_16.png")));
    buttonNext.setToolTipText(bundle.getString("EditorWindow.buttonNext.toolTipText"));
    buttonNext.addActionListener(e -> buttonNextActionPerformed());
    contentPane.add(buttonNext, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- jLabel5 ----
    jLabel5.setText(bundle.getString("EditorWindow.jLabel5.text"));
    contentPane.add(jLabel5, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- txtHeading ----
    txtHeading.setColumns(40);
    contentPane.add(txtHeading, new GridBagConstraints(2, 6, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- jLabel6 ----
    jLabel6.setText(bundle.getString("EditorWindow.jLabel6.text"));
    contentPane.add(jLabel6, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
        GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //======== jScrollPane1 ========
    {

      //---- txtText ----
      txtText.setLineWrap(true);
      txtText.setWrapStyleWord(true);
      jScrollPane1.setViewportView(txtText);
    }
    contentPane.add(jScrollPane1, new GridBagConstraints(2, 7, 2, 1, 1.0, 1.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- jLabel3 ----
    jLabel3.setText(bundle.getString("EditorWindow.jLabel3.text"));
    contentPane.add(jLabel3, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- lblFilename ----
    lblFilename.setText("filename");
    contentPane.add(lblFilename, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- lblYear ----
    lblYear.setText(bundle.getString("EditorWindow.lblYear.text"));
    lblYear.setHorizontalAlignment(SwingConstants.RIGHT);
    contentPane.add(lblYear, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 5, 5, 5), 0, 0));

    //---- lblYearValue ----
    lblYearValue.setText(bundle.getString("EditorWindow.lblYearValue.text"));
    contentPane.add(lblYearValue, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 7, 0, 0), 0, 0));

    //---- doneButton ----
    doneButton.setText(bundle.getString("EditorWindow.doneButton.text"));
    doneButton.addActionListener(e -> doneButtonActionPerformed());
    contentPane.add(doneButton, new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    setSize(600, 500);
    setLocationRelativeTo(getOwner());
  }// </editor-fold>//GEN-END:initComponents


  /**
   * Handle when a user clicks the close button on the window.
   */
  private void formWindowClosing() {
    this.doClose();
  }


  /**
   * Save, show a dialog, and exit.
   */
  private void doClose() {
    this.saveEntry();
    this.makeZipFiles();
    Readsy.getMainWindow().setVisible(true, false);
    this.setVisible(false);
    this.dispose();
  }


  private void buttonPreviousActionPerformed() {
    saveEntry();
    // if we are at the beginning of the year, go to the end
    if (this.calendar.get(Calendar.MONTH) == Calendar.JANUARY && this.calendar.get(Calendar.DAY_OF_MONTH) == 1) {
      this.calendar.set(Calendar.MONTH, Calendar.DECEMBER);
      this.calendar.set(Calendar.DAY_OF_MONTH, 31);
    } else {
      this.calendar.add(Calendar.DAY_OF_MONTH, -1);
    }
    updateDisplay();
  }

  private void buttonNextActionPerformed() {
    saveEntry();
    // if we are at the end of the year, go to the beginning
    if (this.calendar.get(Calendar.MONTH) == Calendar.DECEMBER && this.calendar.get(Calendar.DAY_OF_MONTH) == 31) {
      this.calendar.set(Calendar.MONTH, Calendar.JANUARY);
      this.calendar.set(Calendar.DAY_OF_MONTH, 1);
    } else {
      this.calendar.add(Calendar.DAY_OF_MONTH, 1);
    }
    updateDisplay();
  }

  private void doneButtonActionPerformed() {
    this.doClose();
  }

  /**
   * Update the display.
   * The current date, heading and text are set based on the index of the
   * object model.  The focus is set to the heading.
   */
  private void updateDisplay() {
    StringBuilder buf = new StringBuilder();
    Date d = this.calendar.getTime();
    buf.append(this.prettyDateFormat.format(d));
    String mmdd = this.monthDateFormat.format(d);
    buf.append(" (").append(mmdd).append(")");

    this.lblDate.setText(buf.toString());
    try (BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File(this.directory, mmdd)), "UTF-8"))) {
      this.txtHeading.setText(in.readLine());
      this.txtHeading.setCaretPosition(0);
      this.txtText.setText("");
      String line;
      while ((line = in.readLine()) != null) {
        this.txtText.append(line);
        this.txtText.append("\n");
      }
      this.txtText.setCaretPosition(0);
    } catch (Exception e) {
      logger.error("Error reading content from file " + directory + "/" + mmdd, e);
    }
    this.txtHeading.requestFocus();
  }


  /**
   * Save changes.
   * <p>The current value of heading, text, description, short description, and
   * ignore year are passed to the object model.</p>
   * <p>Display an error message if there is any error during the save process.
   * The actual work is delegated to the object model.</p>
   */
  private boolean saveEntry() {
    boolean saved = false;
    if (validateEntry()) {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      // write the entry
      try (FileOutputStream entry = new FileOutputStream(
          new File(this.directory, this.monthDateFormat.format(this.calendar.getTime())))) {

        entry.write(this.txtHeading.getText().trim().getBytes("UTF-8"));
        entry.write("\n".getBytes("UTF-8"));
        entry.write(this.txtText.getText().trim().getBytes("UTF-8"));
        entry.flush();

        // write metadata if the description or shortDescription have changed
        if (!this.metadata.getProperty("description").equals(this.txtDescription.getText().trim()) ||
            !this.metadata.getProperty("shortDescription").equals(this.txtShortDescription.getText().trim())) {
          try (OutputStreamWriter metadataWriter = new OutputStreamWriter(new FileOutputStream(
              new File(this.directory, "metadata")), "UTF-8")) {
            this.metadata.setProperty("description", this.txtDescription.getText().trim());
            this.metadata.setProperty("shortDescription", this.txtShortDescription.getText().trim());
            this.metadata.store(metadataWriter, "readsy Desktop " + System.getProperty("os.name"));
          }
        }
        saved = true;
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "There was an error while writing the entry for file " +
                this.directory + "/" + this.monthDateFormat.format(this.calendar.getTime()),
            "File Error",
            JOptionPane.ERROR_MESSAGE);
      } finally {
        setCursor(Cursor.getDefaultCursor());
      }
    }
    return saved;
  }

  private void makeZipFiles() {
    Path sourceDir = Paths.get(directory.getAbsolutePath());
    String zipFileName = directory.getParent() + "/" +
        metadata.getProperty(Constants.KEY_METADATA_SHORT_DESCRIPTION) + ".zip";
    String readsyFileName = directory.getParent() + "/" +
        metadata.getProperty(Constants.KEY_METADATA_SHORT_DESCRIPTION) + ".readsy";
    try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFileName));
         ZipOutputStream readsyStream = new ZipOutputStream(new FileOutputStream(readsyFileName))) {
      // walk the source directory, adding files to the zip files
      Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
          FileVisitResult result = FileVisitResult.CONTINUE;
          try {
            Path targetFile = sourceDir.relativize(file);
            zipStream.putNextEntry(new ZipEntry(
                metadata.getProperty(Constants.KEY_METADATA_SHORT_DESCRIPTION) +
                    "/" + targetFile.toString()));
            readsyStream.putNextEntry(new ZipEntry(
                metadata.getProperty(Constants.KEY_METADATA_SHORT_DESCRIPTION) +
                    "/" + targetFile.toString()));
            byte[] bytes = Files.readAllBytes(file);
            zipStream.write(bytes, 0, bytes.length);
            zipStream.closeEntry();
            readsyStream.write(bytes, 0, bytes.length);
            readsyStream.closeEntry();
          } catch (Exception e) {
            logger.error("Error writing zip file.", e);
            result = null;
          }
          return result;
        }
      });
    } catch (Exception e) {
      logger.error("Error creating zip files.", e);
      JOptionPane.showMessageDialog(this,
          "There was an error while creating the zip/readsy archives.\nCheck the log for details.",
          "File Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }


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
    }

    return valid;
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JLabel label1;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JTextField txtDescription;
  private JTextField txtShortDescription;
  private JLabel lblDate;
  private JButton buttonPrevious;
  private JButton buttonNext;
  private JLabel jLabel5;
  private JTextField txtHeading;
  private JLabel jLabel6;
  private JScrollPane jScrollPane1;
  private JTextArea txtText;
  private JLabel jLabel3;
  private JLabel lblFilename;
  private JLabel lblYear;
  private JLabel lblYearValue;
  private JButton doneButton;
  // End of variables declaration//GEN-END:variables
}
