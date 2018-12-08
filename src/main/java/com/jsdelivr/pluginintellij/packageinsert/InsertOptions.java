package com.jsdelivr.pluginintellij.packageinsert;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jsdelivr.pluginintellij.InsertModel;
import com.jsdelivr.pluginintellij.packagefile.FileInput;
import com.jsdelivr.pluginintellij.ui.DefaultListItem;
import com.jsdelivr.pluginintellij.ui.JsDelivrList;
import com.jsdelivr.pluginintellij.ui.JsDelivrPopup;

import java.awt.*;
import java.awt.event.KeyEvent;

public class InsertOptions {
	private static final String jsDelivrUrl = "https://cdn.jsdelivr.net/npm/";

	private static final String messageInsertUrl = "Insert URL";
	private static final String messageInsertHtml = "Insert HTML";
	private static final String messageInsertHtmlSri = "Insert HTML + SRI";

	private JsDelivrList list;
	private JsDelivrPopup popup;
	private InsertModel insertModel;
	private Editor editor;
	private Project project;
	private Font font;

	public InsertOptions(Editor editor, Project project, InsertModel insertModel, boolean generatedMin) {
		this.editor = editor;
		this.insertModel = insertModel;
		this.project = project;

		list = new JsDelivrList(s -> {
			inputComplete(s.toString());
			popup.closePopup();
			return null;
		});

		list.getDefaultModel().addElement(new DefaultListItem(messageInsertUrl));
		list.getDefaultModel().addElement(new DefaultListItem(messageInsertHtml));

		if (!generatedMin) {
			list.getDefaultModel().addElement(new DefaultListItem(messageInsertHtmlSri));
		}

		popup = new JsDelivrPopup(editor, ke -> {
			if (ke.getID() == KeyEvent.KEY_RELEASED) {
				if (ke.getKeyCode() == KeyEvent.VK_TAB && ke.isShiftDown()) {
					new FileInput(editor, project, insertModel.name, insertModel.version, insertModel.file.name.replaceFirst("/", "")).setFont(font);
					popup.closePopup();
				} else if (ke.getKeyCode() == KeyEvent.VK_ENTER || ke.getKeyCode() == KeyEvent.VK_TAB) {
					inputComplete(list.getSelectedItem().toString());
					popup.closePopup();
				}
			}

			list.keyEvent(ke);
			return false;
		}, list.getPane());

		list.resetSelection();
	}

	private void inputComplete(String text) {
		if (text.equals(messageInsertUrl)) {
			new TextEdit(editor, project).insertText(getUrl());
		} else if (text.equals(messageInsertHtml)) {
			if (insertModel.file.name.endsWith(".js")) {
				new TextEdit(editor, project).insertText("<script src=\"" + getUrl() + "\"></script>");
			} else {
				new TextEdit(editor, project).insertText("<link rel=\"stylesheet\" href=\"" + getUrl() + "\">");
			}
		} else if (text.equals(messageInsertHtmlSri)) {
			if (insertModel.file.name.endsWith(".js")) {
				new TextEdit(editor, project).insertText("<script src=\"" + getUrl() + "\" integrity=\"sha256-" + insertModel.file.hash + "\" crossorigin=\"anonymous\"></script>");
			} else {
				new TextEdit(editor, project).insertText("<link rel=\"stylesheet\" href=\"" + getUrl() + "\" integrity=\"sha256-" + insertModel.file.hash + "\" crossorigin=\"anonymous\">");
			}
		}
	}

	private String getUrl() {
		return jsDelivrUrl + insertModel.name + "@" + insertModel.version + insertModel.file.name;
	}

	public void setFont(Font font) {
		list.setFont(font);
		this.font = font;
	}
}
