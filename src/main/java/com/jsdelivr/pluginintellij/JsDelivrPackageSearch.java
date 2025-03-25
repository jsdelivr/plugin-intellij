package com.jsdelivr.pluginintellij;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jsdelivr.pluginintellij.packagename.NameInput;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class JsDelivrPackageSearch extends AnAction {
	private Editor editor;
	private Project project;

	@Override
	public void update(AnActionEvent event) {
		project = event.getProject();
		editor = event.getData(CommonDataKeys.EDITOR);
		event.getPresentation().setVisible(project != null && editor != null);
	}

	@Override
	public void actionPerformed(AnActionEvent event) {
		editor = event.getData(CommonDataKeys.EDITOR);

		if (editor == null) {
			return;
		}

		project = event.getProject();

		Font editorFont = editor.getContentComponent().getFont();
		new NameInput(editor, project).setFont(new Font(editorFont.getFontName(), Font.PLAIN, editorFont.getSize() + 1));
	}

	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread() {
		return ActionUpdateThread.EDT;
	}
}
