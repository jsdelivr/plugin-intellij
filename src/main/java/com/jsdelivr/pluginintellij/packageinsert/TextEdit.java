package com.jsdelivr.pluginintellij.packageinsert;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;

class TextEdit {
	private Editor editor;
	private Project project;

	TextEdit(Editor editor, Project project) {
		this.editor = editor;
		this.project = project;
	}

	void insertText(String text) {
		final Document document = editor.getDocument();
		final SelectionModel selectionModel = editor.getSelectionModel();
		final int start = selectionModel.getSelectionStart();
		final int end = selectionModel.getSelectionEnd();
		WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(start, end, text));
		selectionModel.removeSelection();
		editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + text.length());
	}
}
