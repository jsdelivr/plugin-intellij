package com.jsdelivr.pluginintellij.packagename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jsdelivr.pluginintellij.ui.JsDelivrInput;

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
			thread.stop();
		}

		thread = new Thread(pkgNameThr);
		thread.start();

		return false;
	}

	@Override
	protected void previousInput() {
		// no op
	}
}
