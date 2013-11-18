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

package net.jeremybrooks.readsy.gui;

import net.jeremybrooks.readsy.DataAccess;
import net.jeremybrooks.readsy.PropertyManager;
import net.jeremybrooks.readsy.Readsy;
import net.jeremybrooks.readsy.gui.workers.DeleteFileWorker;
import net.jeremybrooks.readsy.gui.workers.InstallFileWorker;
import org.apache.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * The main window.
 * This window allows the user to view data files, and navigate to different days.
 *
 * @author Jeremy Brooks
 */
public class MainWindow extends javax.swing.JFrame {

	private static final long serialVersionUID = 5051044422765434977L;
	private Logger logger = Logger.getLogger(MainWindow.class);
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
	private Calendar currentDate = new GregorianCalendar();
	private List<TabPanel> tabList;
	private ResourceBundle bundle;
	/**
	 * Change the date for day, week, or month.
	 */
	enum DateChangeMode {
		DAY, WEEK, MONTH
	}

	/** Reference to the window instance. */
	public static MainWindow instance;

	/**
	 * Creates new form MainWindow.
	 * The date is set to today, and the tabs are built.
	 */
	public MainWindow() {
		this.bundle = ResourceBundle.getBundle("localization.main_window");
		this.currentDate.setTime(new Date());
		initComponents();
		this.updateButton.setVisible(false);

		// restore last window size and position
		setBounds(
				PropertyManager.getInstance().getPropertyAsInt(PropertyManager.PROPERTY_WINDOW_X),
				PropertyManager.getInstance().getPropertyAsInt(PropertyManager.PROPERTY_WINDOW_Y),
				PropertyManager.getInstance().getPropertyAsInt(PropertyManager.PROPERTY_WINDOW_WIDTH),
				PropertyManager.getInstance().getPropertyAsInt(PropertyManager.PROPERTY_WINDOW_HEIGHT));

		if (System.getProperty("os.name").contains("Mac")) {
			// HIDE THE File -> Exit and Help MENU ITEMS
			Component[] items = this.fileMenu.getMenuComponents();
			for (Component item : items) {
				javax.swing.JMenuItem jmi = (javax.swing.JMenuItem) item;
				if (jmi.getText().equals(bundle.getString("MainWindow.menuItemExit.text"))) {
					fileMenu.remove(item);
				}
			}
			this.helpMenu.setVisible(false);
		}
		instance = this;
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
		jMenuBar1 = new JMenuBar();
		fileMenu = new JMenu();
		installMenu = new JMenuItem();
		deleteMenu = new JMenuItem();
		menuItemExit = new JMenuItem();
		goMenu = new JMenu();
		menuPreviousDay = new JMenuItem();
		menuToday = new JMenuItem();
		menuNextDay = new JMenuItem();
		jSeparator1 = new JSeparator();
		menuPreviousWeek = new JMenuItem();
		menuNextWeek = new JMenuItem();
		jSeparator2 = new JSeparator();
		menuPreviousMonth = new JMenuItem();
		menuNextMonth = new JMenuItem();
		jSeparator3 = new JSeparator();
		menuFirstUnread = new JMenuItem();
		toolsMenu = new JMenu();
		toolsMenu.addMenuListener(new ToolsMenuHandler());
		editorMenu = new JMenuItem();
		preferencesMenu = new JMenuItem();
		defineMenu = new JMenuItem();
		helpMenu = new JMenu();
		aboutMenu = new JMenuItem();
		jToolBar1 = new JToolBar();
		backButton = new JButton();
		homeButton = new JButton();
		nextButton = new JButton();
		updateButton = new JButton();
		tabPane = new JTabbedPane();
		lblDate = new JLabel();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing();
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		//======== jMenuBar1 ========
		{

			//======== fileMenu ========
			{
				fileMenu.setMnemonic(bundle.getString("MainWindow.fileMenu.mnemonic").charAt(0));
				fileMenu.setText(bundle.getString("MainWindow.fileMenu.text"));

				//---- installMenu ----
				installMenu.setIcon(new ImageIcon(getClass().getResource("/images/709-plus_16.png")));
				installMenu.setMnemonic(bundle.getString("MainWindow.installMenu.mnemonic").charAt(0));
				installMenu.setText(bundle.getString("MainWindow.installMenu.text"));
				installMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						installMenuActionPerformed();
					}
				});
				fileMenu.add(installMenu);

				//---- deleteMenu ----
				deleteMenu.setIcon(new ImageIcon(getClass().getResource("/images/711-trash_16.png")));
				deleteMenu.setMnemonic(bundle.getString("MainWindow.deleteMenu.mnemonic").charAt(0));
				deleteMenu.setText(bundle.getString("MainWindow.deleteMenu.text"));
				deleteMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						deleteMenuActionPerformed();
					}
				});
				fileMenu.add(deleteMenu);

				//---- menuItemExit ----
				menuItemExit.setMnemonic(bundle.getString("MainWindow.menuItemExit.mnemonic").charAt(0));
				menuItemExit.setText(bundle.getString("MainWindow.menuItemExit.text"));
				menuItemExit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuItemExitActionPerformed();
					}
				});
				fileMenu.add(menuItemExit);
			}
			jMenuBar1.add(fileMenu);

			//======== goMenu ========
			{
				goMenu.setMnemonic(bundle.getString("MainWindow.goMenu.mnemonic").charAt(0));
				goMenu.setText(bundle.getString("MainWindow.goMenu.text"));

				//---- menuPreviousDay ----
				menuPreviousDay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_MASK));
				menuPreviousDay.setIcon(new ImageIcon(getClass().getResource("/images/765-arrow-left_16.png")));
				menuPreviousDay.setMnemonic(bundle.getString("MainWindow.menuPreviousDay.mnemonic").charAt(0));
				menuPreviousDay.setText(bundle.getString("MainWindow.menuPreviousDay.text"));
				menuPreviousDay.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuPreviousDayActionPerformed();
					}
				});
				goMenu.add(menuPreviousDay);

				//---- menuToday ----
				menuToday.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.ALT_MASK));
				menuToday.setIcon(new ImageIcon(getClass().getResource("/images/750-home_16.png")));
				menuToday.setMnemonic(bundle.getString("MainWindow.menuToday.mnemonic").charAt(0));
				menuToday.setText(bundle.getString("MainWindow.menuToday.text"));
				menuToday.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuTodayActionPerformed();
					}
				});
				goMenu.add(menuToday);

				//---- menuNextDay ----
				menuNextDay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_MASK));
				menuNextDay.setIcon(new ImageIcon(getClass().getResource("/images/766-arrow-right_16.png")));
				menuNextDay.setMnemonic(bundle.getString("MainWindow.menuNextDay.mnemonic").charAt(0));
				menuNextDay.setText(bundle.getString("MainWindow.menuNextDay.text"));
				menuNextDay.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuNextDayActionPerformed();
					}
				});
				goMenu.add(menuNextDay);
				goMenu.add(jSeparator1);

				//---- menuPreviousWeek ----
				menuPreviousWeek.setText(bundle.getString("MainWindow.menuPreviousWeek.text"));
				menuPreviousWeek.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuPreviousWeekActionPerformed();
					}
				});
				goMenu.add(menuPreviousWeek);

				//---- menuNextWeek ----
				menuNextWeek.setText(bundle.getString("MainWindow.menuNextWeek.text"));
				menuNextWeek.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuNextWeekActionPerformed();
					}
				});
				goMenu.add(menuNextWeek);
				goMenu.add(jSeparator2);

				//---- menuPreviousMonth ----
				menuPreviousMonth.setText(bundle.getString("MainWindow.menuPreviousMonth.text"));
				menuPreviousMonth.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuPreviousMonthActionPerformed();
					}
				});
				goMenu.add(menuPreviousMonth);

				//---- menuNextMonth ----
				menuNextMonth.setText(bundle.getString("MainWindow.menuNextMonth.text"));
				menuNextMonth.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuNextMonthActionPerformed();
					}
				});
				goMenu.add(menuNextMonth);
				goMenu.add(jSeparator3);

				//---- menuFirstUnread ----
				menuFirstUnread.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.ALT_MASK));
				menuFirstUnread.setText(bundle.getString("MainWindow.menuFirstUnread.text"));
				menuFirstUnread.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						menuFirstUnreadActionPerformed();
					}
				});
				goMenu.add(menuFirstUnread);
			}
			jMenuBar1.add(goMenu);

			//======== toolsMenu ========
			{
				toolsMenu.setMnemonic(bundle.getString("MainWindow.toolsMenu.mnemonic").charAt(0));
				toolsMenu.setText(bundle.getString("MainWindow.toolsMenu.text"));

				//---- editorMenu ----
				editorMenu.setIcon(new ImageIcon(getClass().getResource("/images/704-compose_16.png")));
				editorMenu.setText(bundle.getString("MainWindow.editorMenu.text"));
				editorMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						editorMenuActionPerformed();
					}
				});
				toolsMenu.add(editorMenu);

				//---- preferencesMenu ----
				preferencesMenu.setIcon(new ImageIcon(getClass().getResource("/images/740-gear_16.png")));
				preferencesMenu.setText(bundle.getString("MainWindow.preferencesMenu.text"));
				preferencesMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						preferencesMenuActionPerformed();
					}
				});
				toolsMenu.add(preferencesMenu);

				//---- defineMenu ----
				defineMenu.setIcon(new ImageIcon(getClass().getResource("/images/721-bookmarks_16.png")));
				defineMenu.setText(bundle.getString("MainWindow.defineMenu.text"));
				defineMenu.setName(bundle.getString("MainWindow.defineMenu.name"));
				defineMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						defineMenuActionPerformed();
					}
				});
				toolsMenu.add(defineMenu);
			}
			jMenuBar1.add(toolsMenu);

			//======== helpMenu ========
			{
				helpMenu.setMnemonic(bundle.getString("MainWindow.helpMenu.mnemonic").charAt(0));
				helpMenu.setText(bundle.getString("MainWindow.helpMenu.text"));

				//---- aboutMenu ----
				aboutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
				aboutMenu.setIcon(new ImageIcon(getClass().getResource("/images/739-question_16.png")));
				aboutMenu.setText(bundle.getString("MainWindow.aboutMenu.text"));
				aboutMenu.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						aboutMenuActionPerformed();
					}
				});
				helpMenu.add(aboutMenu);
			}
			jMenuBar1.add(helpMenu);
		}
		setJMenuBar(jMenuBar1);

		//======== jToolBar1 ========
		{

			//---- backButton ----
			backButton.setIcon(new ImageIcon(getClass().getResource("/images/765-arrow-left_16.png")));
			backButton.setToolTipText(bundle.getString("MainWindow.backButton.toolTipText"));
			backButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					backButtonActionPerformed();
				}
			});
			jToolBar1.add(backButton);

			//---- homeButton ----
			homeButton.setIcon(new ImageIcon(getClass().getResource("/images/750-home_16.png")));
			homeButton.setToolTipText(bundle.getString("MainWindow.homeButton.toolTipText"));
			homeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					homeButtonActionPerformed();
				}
			});
			jToolBar1.add(homeButton);

			//---- nextButton ----
			nextButton.setIcon(new ImageIcon(getClass().getResource("/images/766-arrow-right_16.png")));
			nextButton.setToolTipText(bundle.getString("MainWindow.nextButton.toolTipText"));
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					nextButtonActionPerformed();
				}
			});
			jToolBar1.add(nextButton);

			//---- updateButton ----
			updateButton.setIcon(new ImageIcon(getClass().getResource("/images/726-star_16.png")));
			updateButton.setText(bundle.getString("MainWindow.updateButton.text"));
			updateButton.setToolTipText(bundle.getString("MainWindow.updateButton.toolTipText"));
			updateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateButtonActionPerformed();
				}
			});
			jToolBar1.add(updateButton);
		}
		contentPane.add(jToolBar1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
		contentPane.add(tabPane, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));

		//---- lblDate ----
		lblDate.setText(bundle.getString("MainWindow.lblDate.text"));
		contentPane.add(lblDate, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
		setLocationRelativeTo(getOwner());
	}// </editor-fold>//GEN-END:initComponents

	private void formWindowClosing() {//GEN-FIRST:event_formWindowClosing
		// save window position and size
		this.savePositionAndSize();
		System.exit(0);
	}//GEN-LAST:event_formWindowClosing


	public void savePositionAndSize() {
		// save window position and size
		PropertyManager.getInstance().setProperty(PropertyManager.PROPERTY_WINDOW_X, Integer.toString(this.getX()));
		PropertyManager.getInstance().setProperty(PropertyManager.PROPERTY_WINDOW_Y, Integer.toString(this.getY()));
		PropertyManager.getInstance().setProperty(PropertyManager.PROPERTY_WINDOW_WIDTH, Integer.toString(this.getWidth()));
		PropertyManager.getInstance().setProperty(PropertyManager.PROPERTY_WINDOW_HEIGHT, Integer.toString(this.getHeight()));
	}

	private void preferencesMenuActionPerformed() {//GEN-FIRST:event_preferencesMenuActionPerformed
		try {
			new PreferencesDialog(this, true).setVisible(true);
		} catch (Throwable t) {
			logger.error("ERROR", t);
		}
	}//GEN-LAST:event_preferencesMenuActionPerformed


	/*
	 * Find the date with the first unread item and go to it.
	 */
	private void menuFirstUnreadActionPerformed() {//GEN-FIRST:event_menuFirstUnreadActionPerformed
		// GET SELECTED TAB
		Component c = this.tabPane.getSelectedComponent();
		if (c != null) {
			if (c instanceof TabPanel) {
				logger.debug("Found tab panel.");
					Date date = ((TabPanel)c).getFirstUnreadItemDate();
					if (date != null) {
						this.currentDate.setTime(date);
						updateDisplay();
					}
			} else {
				logger.debug("Not a tab panel.");
			}
		} else {
			logger.debug("No tab is selected.");
		}
	}//GEN-LAST:event_menuFirstUnreadActionPerformed


	/**
	 * Handle clicks on the update button.
	 * The user will be asked if they want to go to the readsy home page.
	 * If so, we will try to open a browser.  If there is an error launching
	 * the browser, let the user know, and tell them that they can download it
	 * manually.
	 */
	private void updateButtonActionPerformed() {//GEN-FIRST:event_updateButtonActionPerformed
		int response = JOptionPane.showOptionDialog(this,
				bundle.getString("MainWindow.joption.newVersion.message1") + '\n' + Readsy.HOME_PAGE + "\n\n" + bundle.getString("MainWindow.joption.newVersion.message2"),
				bundle.getString("MainWindow.joption.newVersion.title"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null, null, null);

		if (response == JOptionPane.YES_OPTION) {
			try {
				Desktop.getDesktop().browse(new URI(Readsy.HOME_PAGE));
			} catch (Exception e) {
				logger.error("Could not launch browser.", e);
				JOptionPane.showMessageDialog(this,
						bundle.getString("MainWindow.joption.newVersionError.message1") + " " + Readsy.HOME_PAGE + '\n' + bundle.getString("MainWindow.joption.newVersionError.message2"),
						bundle.getString("MainWindow.joption.newVersionError.title"),
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}//GEN-LAST:event_updateButtonActionPerformed


	/**
	 * Navigate to the previous month.
	 */
	private void menuPreviousMonthActionPerformed() {//GEN-FIRST:event_menuPreviousMonthActionPerformed
		this.previous(DateChangeMode.MONTH);
	}//GEN-LAST:event_menuPreviousMonthActionPerformed


	/**
	 * Navigate to the next month.
	 */
	private void menuNextMonthActionPerformed() {//GEN-FIRST:event_menuNextMonthActionPerformed
		this.next(DateChangeMode.MONTH);
	}//GEN-LAST:event_menuNextMonthActionPerformed


	/**
	 * Navigate to the next week.
	 */
	private void menuNextWeekActionPerformed() {//GEN-FIRST:event_menuNextWeekActionPerformed
		this.next(DateChangeMode.WEEK);
	}//GEN-LAST:event_menuNextWeekActionPerformed


	/**
	 * Navigate to the previous week.
	 */
	private void menuPreviousWeekActionPerformed() {//GEN-FIRST:event_menuPreviousWeekActionPerformed
		this.previous(DateChangeMode.WEEK);
	}//GEN-LAST:event_menuPreviousWeekActionPerformed


	/*
	 * Display the editor.
	 * This window is hidden, and a new EditorStartWindow is displayed.
	 */
	private void editorMenuActionPerformed() {//GEN-FIRST:event_editorMenuActionPerformed
		EditorStartWindow editorStartWindow = new EditorStartWindow();
		this.setVisible(false, false);
		editorStartWindow.setVisible(true);
	}//GEN-LAST:event_editorMenuActionPerformed


	private void deleteMenuActionPerformed() {//GEN-FIRST:event_deleteMenuActionPerformed
		DeleteFileWorker dfw = new DeleteFileWorker(this);
		WorkerDialog wd = new WorkerDialog(this, dfw, bundle.getString("worker.deleteTitle"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
		wd.executeAndShowDialog();
		if (dfw.getError() != null) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("MainWindow.joption.deleteError.message"),
					bundle.getString("MainWindow.joption.deleteError.title"),
					JOptionPane.ERROR_MESSAGE);
		}
		if (!dfw.isUserCancelled()) {
			this.tabList = null;
			this.createTabs();
		}
	}//GEN-LAST:event_deleteMenuActionPerformed


	/*
	 * Allow the user to install readsy XML files.
	 * A JFileChooser is displayed, with multiple selection enabled, defaulting
	 * to the user home directory, and filtering all *.xml files.
	 * If the user selects some files, they are installed and the tabs are updated.
	 * <p/>
	 * If some files could not be installed, the user is informed.
	 */
	private void installMenuActionPerformed() {//GEN-FIRST:event_installMenuActionPerformed
		List<String> errList = new ArrayList<>();
		JFileChooser jfc = new JFileChooser(System.getProperty("user.home"));
		jfc.setApproveButtonText(bundle.getString("MainWindow.fileChooserApproveInstall"));
		jfc.setMultiSelectionEnabled(true);
		jfc.setFileFilter(new SwingXMLFileFilter());

		if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File[] files = jfc.getSelectedFiles();
			for (File source : files) {
				InstallFileWorker worker = new InstallFileWorker(source);
				WorkerDialog wd = new WorkerDialog(this, worker, bundle.getString("worker.installingFile"), "");
				wd.executeAndShowDialog();
				if (worker.getError() != null) {
					errList.add(source.getAbsolutePath() + " - " + worker.getError().getMessage());
				}
			}
			if (errList.size() > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(bundle.getString("MainWindow.joption.installError.message")).append('\n');
				for (String s : errList) {
					sb.append(s).append('\n');
				}
				JOptionPane.showMessageDialog(this,
						sb.toString(), bundle.getString("MainWindow.joption.installError.title"), JOptionPane.ERROR_MESSAGE);
			}

			this.tabList = null;
			this.createTabs();
		}
	}//GEN-LAST:event_installMenuActionPerformed


	/**
	 * Display the about dialog as modal.
	 */
	private void aboutMenuActionPerformed() {//GEN-FIRST:event_aboutMenuActionPerformed
		new AboutDialog(this, true).setVisible(true);
	}//GEN-LAST:event_aboutMenuActionPerformed


	/**
	 * When a user selects the next menu, go to the next day.
	 */
	private void menuNextDayActionPerformed() {//GEN-FIRST:event_menuNextDayActionPerformed
		this.next(DateChangeMode.DAY);
	}//GEN-LAST:event_menuNextDayActionPerformed


	/**
	 * When a user selects the home menu, go to today.
	 */
	private void menuTodayActionPerformed() {//GEN-FIRST:event_menuTodayActionPerformed
		this.today();
	}//GEN-LAST:event_menuTodayActionPerformed


	/**
	 * When a user selects the back menu, go to the previous day.
	 */
	private void menuPreviousDayActionPerformed() {//GEN-FIRST:event_menuPreviousDayActionPerformed
		this.previous(DateChangeMode.DAY);
	}//GEN-LAST:event_menuPreviousDayActionPerformed


	/**
	 * When the user clicks the home button, go to today.
	 */
	private void homeButtonActionPerformed() {//GEN-FIRST:event_homeButtonActionPerformed
		this.today();
	}//GEN-LAST:event_homeButtonActionPerformed


	/**
	 * When the user clicks the next button, go to the next day.
	 */
	private void nextButtonActionPerformed() {//GEN-FIRST:event_nextButtonActionPerformed
		this.next(DateChangeMode.DAY);
	}//GEN-LAST:event_nextButtonActionPerformed


	/**
	 * When the user clicks the back button, go to the previous day.
	 */
	private void backButtonActionPerformed() {//GEN-FIRST:event_backButtonActionPerformed
		this.previous(DateChangeMode.DAY);
	}//GEN-LAST:event_backButtonActionPerformed


	/**
	 * Set the date to today.
	 */
	void today() {
		this.currentDate.setTime(new Date());
		updateDisplay();
	}


	/**
	 * Navigate to the next day, week, or month.
	 *
	 * @param changeBy the unit of time to change the date by.
	 */
	void next(DateChangeMode changeBy) {
		switch (changeBy) {
			case DAY:
				this.currentDate.add(GregorianCalendar.DAY_OF_MONTH, 1);
				break;

			case WEEK:
				this.currentDate.add(GregorianCalendar.WEEK_OF_YEAR, 1);
				break;

			case MONTH:
				this.currentDate.add(GregorianCalendar.MONTH, 1);
				break;

			default:
				logger.warn("INVALID CHANGE MODE: " + changeBy);
				break;
		}
		updateDisplay();
	}


	/**
	 * Navigate to the previous day, week, or month.
	 *
	 * @param changeBy the unit of time to change the date by.
	 */
	void previous(DateChangeMode changeBy) {
		switch (changeBy) {
			case DAY:
				this.currentDate.add(GregorianCalendar.DAY_OF_MONTH, -1);
				break;

			case WEEK:
				this.currentDate.add(GregorianCalendar.WEEK_OF_YEAR, -1);
				break;

			case MONTH:
				this.currentDate.add(GregorianCalendar.MONTH, -1);
				break;

			default:
				logger.warn("INVALID CHANGE MODE: " + changeBy);
				break;
		}
		updateDisplay();
	}


	/**
	 * Make the update button visible when a new version is available.
	 */
	public void newVersionAvailable() {
		this.updateButton.setVisible(true);
	}


	protected List<TabPanel> getTabList() {
		return this.tabList;
	}


	/*
	 * Goodbye.
	 */
	private void menuItemExitActionPerformed() {//GEN-FIRST:event_menuItemExitActionPerformed
		System.exit(0);
	}//GEN-LAST:event_menuItemExitActionPerformed


	/*
	 * Display definition for the selected word.
	 */
	private void defineMenuActionPerformed() {//GEN-FIRST:event_defineMenuActionPerformed
		TabPanel tp = (TabPanel) this.tabPane.getSelectedComponent();
		DefinitionDialog def = new DefinitionDialog(Readsy.getMainWindow(), false);
		def.setVisible(true);
		def.define(tp.getWordToDefine());
	}//GEN-LAST:event_defineMenuActionPerformed


	/**
	 * Update the display when the user navigates to a new day.
	 * The work is performed by an instance of RecreateTabs, and the work
	 * is executed in a new thread using the SwingWorker.
	 */
	private void updateDisplay() {
		WorkerDialog wd = new WorkerDialog(Readsy.getMainWindow(),
				new UpdateDisplay(), bundle.getString("worker.updating"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
		wd.executeAndShowDialog();
	}


	/**
	 * Re-create the tabs when a user adds or deletes a data file.
	 * The work is performed by an instance of RecreateTabs, and the work
	 * is executed in a new thread using the SwingWorker.
	 */
	public void createTabs() {
		CreateTabs ct = new CreateTabs();
		WorkerDialog wd = new WorkerDialog(Readsy.getMainWindow(),
				ct, bundle.getString("worker.loading"), "", new ImageIcon(getClass().getResource("/images/ajax-loader.gif")));
		wd.executeAndShowDialog();
		if (ct.getError() != null) {
			JOptionPane.showMessageDialog(this,
					bundle.getString("MainWindow.joption.createTabsError.message") + "\n" + ct.getError().getMessage(),
					bundle.getString("MainWindow.joption.createTabsError.title"),
					JOptionPane.WARNING_MESSAGE);
		}
	}


	/**
	 * Make this window visible, optionally creating content tabs.
	 *
	 * @param visible visibility of the window.
	 * @param createTabs if true, content tabs will be created.
	 */
	public void setVisible(boolean visible, boolean createTabs) {
		super.setVisible(visible);
		if (createTabs) {
			this.createTabs();
		}
	}


	/**
	 * Create the tabs.
	 * If the tab list is null, the TabBuilder will be called to get a
	 * list, and the list will be set.
	 */
	class CreateTabs extends javax.swing.SwingWorker<Void, Void> {
		private Exception error;

		@Override
		protected Void doInBackground() throws Exception {
			lblDate.setText(bundle.getString("MainWindow.lblDate.text") + " " + dateFormatter.format(currentDate.getTime()));
			tabList = new LinkedList<>();

			try {
				List<String> dataDirectories = DataAccess.getDataDirectoryNames();
				logger.debug("Got " + dataDirectories.size() + " directories.");
				for (String directory : dataDirectories) {
					firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "", directory);
					Properties metadata = DataAccess.getMetadata(directory);
					tabList.add(new TabPanel(directory, metadata));
				}
			} catch (Exception e) {
				error = e;
				logger.error("Error while getting directory list/metadata", e);
			} finally {
				try {
					tabPane.removeAll();
					if (tabList.size() > 0) {
						int index = 0;
						for (TabPanel tab : tabList) {
							firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "",
									bundle.getString("worker.loadingEntry") + " " + (index + 1) + "/" + tabList.size() + " " +
											bundle.getString("worker.loadingEntry.for") + " " + dateFormatter.format(currentDate.getTime()));
							tab.displayDataForDate(currentDate.getTime());
							tabPane.add(tab.getTabTitle(), tab);

							// tell the tab panel which index it is using
							tab.setIndex(index);
							tab.setTabbedPane(tabPane);
							index++;
						}
					} else {
						javax.swing.JTextArea t = new javax.swing.JTextArea();
						t.setText(bundle.getString("MainWindow.welcome"));
						t.setWrapStyleWord(true);
						t.setEditable(false);
						t.setLineWrap(true);

						tabPane.add(t);
					}
				} catch (Exception e) {
					logger.error("ERROR WHILE LOADING DATA.", e);
					error = e;
				}
			}
			return null;
		}

		public Exception getError() {
			return this.error;
		}
	}


	/**
	 * Update the display, telling each tab to display data for the current date.
	 * This is executed in a SwingWorker so we can display a modal wait dialog
	 * while the tabs are looking up data.
	 */
	class UpdateDisplay extends javax.swing.SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			lblDate.setText(bundle.getString("MainWindow.lblDate.text") + " " + dateFormatter.format(currentDate.getTime()));
			Date date = currentDate.getTime();
			int count = 1;
			for (TabPanel tab : tabList) {
				firePropertyChange(WorkerDialog.EVENT_DIALOG_MESSAGE, "",
						bundle.getString("worker.loadingEntry") + " " + (count + 1) + "/" + tabList.size());
				tab.displayDataForDate(date);
				tab.updateTabTitle();
			}
			return null;
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JMenuBar jMenuBar1;
	private JMenu fileMenu;
	private JMenuItem installMenu;
	private JMenuItem deleteMenu;
	private JMenuItem menuItemExit;
	private JMenu goMenu;
	private JMenuItem menuPreviousDay;
	private JMenuItem menuToday;
	private JMenuItem menuNextDay;
	private JSeparator jSeparator1;
	private JMenuItem menuPreviousWeek;
	private JMenuItem menuNextWeek;
	private JSeparator jSeparator2;
	private JMenuItem menuPreviousMonth;
	private JMenuItem menuNextMonth;
	private JSeparator jSeparator3;
	private JMenuItem menuFirstUnread;
	private JMenu toolsMenu;
	private JMenuItem editorMenu;
	private JMenuItem preferencesMenu;
	private JMenuItem defineMenu;
	private JMenu helpMenu;
	private JMenuItem aboutMenu;
	private JToolBar jToolBar1;
	private JButton backButton;
	private JButton homeButton;
	private JButton nextButton;
	private JButton updateButton;
	private JTabbedPane tabPane;
	private JLabel lblDate;
	// End of variables declaration//GEN-END:variables


	class ToolsMenuHandler implements MenuListener {

		/**
		 * When the Tools menu is clicked, check for a selection in the
		 * tab panel and set the "Define" menu item accordingly.
		 *
		 * @param e
		 */
		@Override
		public void menuSelected(MenuEvent e) {
			// is there something selected in the current tab?
			Component c = tabPane.getSelectedComponent();

			if (c instanceof TabPanel) {
				TabPanel tp = (TabPanel) c;
				String word = tp.getWordToDefine();
				JMenu m = (JMenu) e.getSource();
				for (Component menu : m.getMenuComponents()) {
					if (menu instanceof JMenuItem) {
						JMenuItem jmenu = (JMenuItem) menu;
						String name = jmenu.getName();
						if (name != null && name.equals("define")) {
							if (word == null) {
								jmenu.setText(bundle.getString("MainWindow.defineMenu.text"));
								jmenu.setEnabled(false);
							} else {
								jmenu.setText(bundle.getString("MainWindow.defineMenu.text") + " \"" + word + "\"");
								jmenu.setEnabled(true);
							}
						}
					}
				}
			}
		}


		@Override
		public void menuDeselected(MenuEvent e) {
		}


		@Override
		public void menuCanceled(MenuEvent e) {
		}

	}
}
