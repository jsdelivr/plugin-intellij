package com.jsdelivr.pluginintellij.packageversion;

import com.github.zafarkhaja.semver.Version;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jsdelivr.pluginintellij.packagefile.FileInput;
import com.jsdelivr.pluginintellij.packagename.NameInput;
import com.jsdelivr.pluginintellij.packagename.remotetypes.AlgoliaPackage;
import com.jsdelivr.pluginintellij.ui.DefaultListItem;
import com.jsdelivr.pluginintellij.ui.JsDelivrInput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VersionInput extends JsDelivrInput {
	private static final String messageLoading = "Loading...";
	private static final String messageNotFound = "Version not found";
	private static final String messagePlaceholder = "Package version";

	private AlgoliaPackage pkg;
	private Comparator<Version> versionAscending = Comparator.reverseOrder();

	public VersionInput(Editor editor, Project project, AlgoliaPackage pkg) {
		super(editor, project, messagePlaceholder);

		loading = true;

		this.pkg = pkg;
		updateAutocomplete("");
		list.resetSelection();

		loading = false;
	}

	public VersionInput(Editor editor, Project project, AlgoliaPackage pkg, String version) {
		super(editor, project, messagePlaceholder);

		loading = true;

		this.pkg = pkg;
		inputField.setText(version);
		inputField.setForeground(editor.getContentComponent().getForeground());
		updateAutocomplete(version);
		list.resetSelection();

		loading = false;
	}

	@Override
	protected void updateAutocomplete(String text) {
		List<Version> list = new ArrayList<>();

		JsDelivrInput.list.setEmptyText(messageLoading);

		if (pkg.getVersions() == null) {
			JsDelivrInput.list.setEmptyText(messageNotFound);
		}

		for (String version : pkg.getVersions()) {
			if (version.startsWith(text)) {
				try {
					list.add(Version.valueOf(version));
				} catch (Exception e) {
					System.out.println("Unrecognized version " + version);
				}
			}
		}

		if (list.isEmpty()) {
			JsDelivrInput.list.setEmptyText(messageNotFound);
		}

		list.sort(versionAscending);
		JsDelivrInput.list.getDefaultModel().clear();

		for (Version version : list) {
			JsDelivrInput.list.getDefaultModel().addElement(new DefaultListItem(version.toString()));
		}
	}

	@Override
	protected boolean inputComplete(String text) {
		list.getDefaultModel().clear();
		list.setEmptyText(messageLoading);

		if (!pkg.getVersions().contains(text)) {
			list.setEmptyText(messageNotFound);
			return false;
		}

		new FileInput(editor, project, pkg.getName(), text).setFont(getFont());
		return true;
	}

	@Override
	protected void previousInput() {
		new NameInput(editor, project, pkg.getName()).setFont(getFont());
		popup.closePopup();
	}
}
