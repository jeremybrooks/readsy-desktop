/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2013-2019  Jeremy Brooks
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

import net.jeremybrooks.knicker.Knicker.SourceDictionary;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.dto.Definition;
import net.jeremybrooks.readsy.Readsy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.GroupLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import java.awt.Container;
import java.util.EnumSet;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Look up the definition of a word, and display it.
 *
 * @author Jeremy Brooks
 */
public class DefinitionDialog extends javax.swing.JDialog {

  private Logger logger = LogManager.getLogger(DefinitionDialog.class);
  private ResourceBundle bundle = ResourceBundle.getBundle("localization.definition_dialog");


  /**
   * Creates new form DefinitionDialog
   */
  public DefinitionDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    setIconImage(Readsy.WINDOW_IMAGE);
    logger.info("init complete");
    setBounds((parent.getWidth() / 2) + parent.getX() - (400 / 2),
        (parent.getHeight() / 2) + parent.getY() - (322 / 2),
        400, 322);
    logger.info("bounds set");
  }


  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    jScrollPane1 = new JScrollPane();
    jTextPane1 = new JTextPane();

    //======== this ========
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    Container contentPane = getContentPane();

    //======== jScrollPane1 ========
    {

      //---- jTextPane1 ----
      jTextPane1.setContentType("text/html");
      jTextPane1.setEditable(false);
      jScrollPane1.setViewportView(jTextPane1);
    }

    GroupLayout contentPaneLayout = new GroupLayout(contentPane);
    contentPane.setLayout(contentPaneLayout);
    contentPaneLayout.setHorizontalGroup(
        contentPaneLayout.createParallelGroup()
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
    );
    contentPaneLayout.setVerticalGroup(
        contentPaneLayout.createParallelGroup()
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
    );
    setLocationRelativeTo(getOwner());
  }// </editor-fold>//GEN-END:initComponents


  /**
   * Define the word.
   *
   * @param word word to define.
   */
  public void define(String word) {
    StringBuilder sb = new StringBuilder("<html><body><h1>");
    sb.append(word).append("</h1><p><i>" + bundle.getString("lookupMessage.text") + "</i></p></body></html>");
    this.jTextPane1.setText(sb.toString());

    new Lookup(word).execute();
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JScrollPane jScrollPane1;
  private JTextPane jTextPane1;
  // End of variables declaration//GEN-END:variables


  /**
   * Do the lookup in a separate thread.
   * <p/>
   * <p>This is a SwingWorker, and will handle lookup and display.</p>
   */
  class Lookup extends javax.swing.SwingWorker<List<Definition>, Object> {

    /**
     * The word to look up.
     */
    private String word;

    /**
     * Require use of the non-default constructor
     */
    private Lookup() {
    }

    /**
     * Construct a new instance of Lookup.
     *
     * @param word the word to define.
     */
    public Lookup(String word) {
      this.word = word;
    }


    /**
     * Look up the definition of the word.
     * <p/>
     * <p>This method will try each supported Wordnik dictionary, in this
     * order:
     * <ul><li>wordnet</li>
     * <li>wiktionary</li>
     * <li>webster</li>
     * <li>century</li>
     * </ul></p>
     *
     * @return list of definitions for the word.
     * @throws Exception if there are any errors.
     */
    @Override
    protected List<Definition> doInBackground() throws Exception {
      List<Definition> retList = this.getDefintions();

      if (retList.isEmpty()) {
        // the API is case sensitive, so try a lowercase version
        logger.info("Nothing found; trying again with lowercase version.");
        this.word = this.word.toLowerCase();
        retList = this.getDefintions();
      }

      return retList;
    }


    /**
     * Display the definitions when the lookup is finished.
     * <p/>
     * <p>If there were no definitions found, display a sane message.</p>
     */
    @Override
    protected void done() {
      try {
        List<Definition> list = get();
        if (list == null) {
          showError(bundle.getString("errorNullValue"));

        } else if (list.isEmpty()) {
          showError(bundle.getString("errorNoDefinition") + "\"" + word + "\"");

        } else {
          StringBuilder sb = new StringBuilder("<html><body><h1>");
          sb.append(word).append("</h1>");

          int i = 1;
          for (Definition def : list) {
            sb.append("<p><b>").append(i++).append(".</b> ");
            sb.append("<i>").append(def.getPartOfSpeech()).append(": </i>");
            sb.append(def.getText()).append("<br/>");
            sb.append('(').append(def.getAttributionText()).append(')');
            sb.append("</p>");
          }

          sb.append("</body></html>");

          jTextPane1.setText(sb.toString());
          jTextPane1.setCaretPosition(0);
        }


      } catch (Exception e) {
        logger.error("Error looking up word.", e);
        showError(e.toString());
      }

    }


    /**
     * Convenience method for displaying an error message in the
     * definition area.
     *
     * @param error the error message to display.
     */
    private void showError(String error) {
      StringBuilder sb = new StringBuilder("<html><body><h1>");
      sb.append(word).append("</h1>");
      sb.append("<p>" + bundle.getString("errorMessage.text") + "</p>");
      sb.append("<p>").append(error).append("</p>");

      jTextPane1.setText(sb.toString());
    }


    private List<Definition> getDefintions() {
      List<Definition> retList = null;
      try {
        logger.info("Looking in ahd");
        retList = WordApi.definitions(word, EnumSet.of(SourceDictionary.ahd));

        if (retList.isEmpty()) {
          logger.info("Looking in wordnet");
          retList = WordApi.definitions(word, EnumSet.of(SourceDictionary.wordnet));
        }

        if (retList.isEmpty()) {
          logger.info("Looking in wiktionary");
          retList = WordApi.definitions(word, EnumSet.of(SourceDictionary.wiktionary));
        }

        if (retList.isEmpty()) {
          logger.info("Looking in webster");
          retList = WordApi.definitions(word, EnumSet.of(SourceDictionary.webster));
        }

        if (retList.isEmpty()) {
          logger.info("Looking in century");
          retList = WordApi.definitions(word, EnumSet.of(SourceDictionary.century));
        }
      } catch (Exception e) {
        logger.error("Error looking up word.", e);
        showError(e.toString());
      }

      return retList;
    }
  }
}
