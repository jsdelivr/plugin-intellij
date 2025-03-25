package com.jsdelivr.pluginintellij.packagename;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import com.jsdelivr.pluginintellij.ui.JsDelivrInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class NameInput extends JsDelivrInput {
	private static final String messageLoading = "Loading...";
	private static final String messagePlaceholder = "Package name";

	private AlgoliaSearch algoliaSearch;

	Thread thread = null;

	public NameInput(Editor editor, Project project) {
		super(editor, project, messagePlaceholder);

		this.algoliaSearch = new AlgoliaSearch();
		updateAutocomplete("");
		list.resetSelection();
	}

	public NameInput(Editor editor, Project project, String name) {
		super(editor, project, messagePlaceholder);

		this.algoliaSearch = new AlgoliaSearch();
		inputField.setText(name);
		inputField.setForeground(editor.getContentComponent().getForeground());
		updateAutocomplete(name);
		list.resetSelection();
	}

	@Override
	protected void updateAutocomplete(String text) {
		list.getDefaultModel().clear();
		list.setEmptyText(messageLoading);

		NameThread thread = new NameThread(algoliaSearch, text, this, false);
		Thread thr = new Thread(thread);
		thr.start();
	}

	@Override
	protected boolean inputComplete(String text) {
		list.getDefaultModel().clear();
		list.setEmptyText(messageLoading);

		NameThread pkgNameThr = new NameThread(algoliaSearch, text, this, true);

		if (thread != null) {
			thread.interrupt();
		}

		thread = new Thread(pkgNameThr);
		thread.start();

		return false;
	}

	@Override
	protected void previousInput() {
		// no op
	}

	@Override
	protected void onKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
			if (isShortcutPressed("com.jsdelivr.pluginintellij.actions.OpenJsDelivr", keyEvent)) {
				if (list.getSelectedItem() != null && !list.getSelectedItem().toString().isEmpty()) {
					openBrowser("https://www.jsdelivr.com/package/npm/" + list.getSelectedItem().toString());
				}
			}

			if (isShortcutPressed("com.jsdelivr.pluginintellij.actions.OpenNpm", keyEvent)) {
				if (list.getSelectedItem() != null && !list.getSelectedItem().toString().isEmpty()) {
					openBrowser("https://www.npmjs.com/package/" + list.getSelectedItem().toString());
				}
			}

			if (isShortcutPressed("com.jsdelivr.pluginintellij.actions.OpenGithub", keyEvent) && list.getSelectedItem() != null) {
				String url = ((ListNameItem) list.getSelectedItem()).getRepositoryUrl();

				if (url != null && !url.isEmpty()) {
					openBrowser(url);
				}
			}
		}
	}

	private boolean isShortcutPressed(String action, KeyEvent ke) {
		AnAction jsdelivrSite = ActionManager.getInstance().getAction(action);

		for (Shortcut shortcut : jsdelivrSite.getShortcutSet().getShortcuts()) {
			KeyStroke stroke = KeyStroke.getKeyStroke(getKeyStrokeString(shortcut));

			if (ke.getKeyCode() == stroke.getKeyCode()) {
				if ((stroke.getModifiers() & KeyEvent.CTRL_DOWN_MASK) != 0 && !ke.isControlDown()) {
					continue;
				}

				if ((stroke.getModifiers() & KeyEvent.ALT_DOWN_MASK) != 0 && !ke.isAltDown()) {
					continue;
				}

				if ((stroke.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) != 0 && !ke.isShiftDown()) {
					continue;
				}

				return true;
			}
		}

		return false;
	}

	private void openBrowser(String website) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(website));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(new String[]{"xdg-open", website});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getKeyStrokeString(Shortcut shortcut) {
		return KeymapUtil.getShortcutText(shortcut)
			.replaceAll("\\+", " ")
			.replace("Ctrl", "ctrl")
			.replace("Alt", "alt")
			.replace("Shift", "shift");
	}
}
