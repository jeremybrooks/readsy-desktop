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

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Provides a dialog box that can be updated from a SwingWorker.
 * <p>This dialog is modal, so it will block user input during long operations, while
 * allowing your SwingWorker to update a progress bar and display status messages to the
 * user.</p>
 *
 * @author Jeremy Brooks
 */
public class WorkerDialog implements PropertyChangeListener {

	private JDialog dialog;
	private JLabel titleLabel;
	private JLabel messageLabel;
	private JProgressBar progressBar;
	private JLabel spinnerLabel;
	private SwingWorker swingWorker;
	private boolean useProgressBar;

	private static final int DEFAULT_WIDTH = 495;
	private static final int DEFAULT_HEIGHT = 125;

	public static final String EVENT_DIALOG_TITLE = "dialogTitle";
	public static final String EVENT_DIALOG_MESSAGE = "dialogMessage";

	/**
	 * Create a WorkerDialog with a progress bar and default height/width.
	 *
	 * @param owner
	 * @param swingWorker
	 * @param title
	 * @param message
	 */
	public WorkerDialog(Window owner, SwingWorker swingWorker, String title, String message) {
		this(owner, swingWorker, title, message, null, true, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public WorkerDialog(Window owner, SwingWorker swingWorker, String title, String message, int width, int height) {
		this(owner, swingWorker, title, message, null, true, width, height);
	}

	public WorkerDialog(Window owner, SwingWorker swingWorker, String title, String message, ImageIcon spinnerIcon, int width, int height) {
		this(owner, swingWorker, title, message, spinnerIcon, false, width, height);
	}

	public WorkerDialog(Window owner, SwingWorker swingWorker, String title, String message, ImageIcon spinnerIcon) {
		this(owner, swingWorker, title, message, spinnerIcon, false, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	private WorkerDialog(Window owner, SwingWorker swingWorker, String title, String message, ImageIcon spinnerIcon, boolean useProgressBar, int width, int height) {
		this.swingWorker = swingWorker;
		this.useProgressBar = useProgressBar;

		this.dialog = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);
		this.dialog.setUndecorated(true);

		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new GridLayout(3, 0));

		// top grid is the title label
		titleLabel = new JLabel();
		titleLabel.setText(title);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(titleLabel);

		// middle grid is the progress bar or spinner icon
		if (useProgressBar) {
			progressBar = new JProgressBar();
			progressBar.setStringPainted(true);
			contentPane.add(progressBar);
		} else {
			spinnerLabel = new JLabel();
			spinnerLabel.setText("");
			spinnerLabel.setIcon(spinnerIcon);
			spinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPane.add(spinnerLabel);
		}

		// bottom grid is the message label
		messageLabel = new JLabel();
		messageLabel.setText(message);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(messageLabel);

		// set up size and position on owner
		this.dialog.setSize(width, height);
		this.dialog.setLocationRelativeTo(owner);

		// register for events from the SwingWorker
		swingWorker.addPropertyChangeListener(this);
	}


	/**
	 * Handle property change events from SwingWorker.
	 *
	 * @param event
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if ("state".equals(event.getPropertyName()) && SwingWorker.StateValue.DONE == event.getNewValue()) {
			dialog.setVisible(false);
			dialog.dispose();
		} else if (EVENT_DIALOG_TITLE.equals(event.getPropertyName())) {
			this.titleLabel.setText(event.getNewValue().toString());
		} else if (EVENT_DIALOG_MESSAGE.equals(event.getPropertyName())) {
			this.messageLabel.setText(event.getNewValue().toString());
		} else if ("progress".equals(event.getPropertyName()) && this.useProgressBar) {
			this.progressBar.setValue((Integer) event.getNewValue());
		}
	}

	public Dialog getDialog() {
		return this.dialog;
	}

	public void executeAndShowDialog() {
		this.swingWorker.execute();
		this.dialog.setVisible(true);
	}
}
