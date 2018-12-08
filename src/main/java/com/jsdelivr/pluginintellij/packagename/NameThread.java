package com.jsdelivr.pluginintellij.packagename;

import com.intellij.openapi.application.ApplicationManager;
import com.jsdelivr.pluginintellij.packagename.remotetypes.AlgoliaPackage;
import com.jsdelivr.pluginintellij.packageversion.VersionInput;
import com.jsdelivr.pluginintellij.ui.JsDelivrInput;

import java.util.List;

public class NameThread implements Runnable {
	private static final String messageNotFound = "Package not found";

	private volatile List<AlgoliaPackage> packages;
	private AlgoliaSearch algoliaSearch;
	private String text;
	private String version;
	private NameInput nameInput;
	private JsDelivrInput jsDelivrInput;
	private boolean complete;

	NameThread(AlgoliaSearch algoliaSearch, String text, NameInput nameInput, boolean complete) {
		this.algoliaSearch = algoliaSearch;
		this.text = text;
		this.nameInput = nameInput;
		this.complete = complete;
		this.jsDelivrInput = nameInput;
	}

	public NameThread(AlgoliaSearch algoliaSearch, String text, String version, JsDelivrInput nameInput, boolean complete) {
		this.algoliaSearch = algoliaSearch;
		this.text = text;
		this.complete = complete;
		this.jsDelivrInput = nameInput;
		this.version = version;
	}

	@Override
	public void run() {
		jsDelivrInput.loading = true;
		packages = algoliaSearch.search(text);

		if (complete) {
			ApplicationManager.getApplication().invokeLater(() -> {
				ApplicationManager.getApplication().runWriteAction(this::runComplete);
			});
		} else {
			ApplicationManager.getApplication().invokeLater(() -> {
				ApplicationManager.getApplication().runWriteAction(() -> {
					synchronized (this) {
						if (packages == null || packages.isEmpty()) {
							JsDelivrInput.list.setEmptyText(messageNotFound);
							jsDelivrInput.loading = false;
							return;
						}

						JsDelivrInput.list.getDefaultModel().clear();

						for (AlgoliaPackage pkg : packages) {
							JsDelivrInput.list.getDefaultModel().addElement(new ListNameItem(pkg.getName(), pkg.getRepository()));
						}

						JsDelivrInput.list.resetSelection();
					}
				});
			});
		}

		jsDelivrInput.loading = false;

		if (nameInput != null) {
			nameInput.thread = null;
		}
	}

	private void runComplete() {
		synchronized (this) {
			AlgoliaPackage chosenPackage = null;

			if (packages == null || packages.isEmpty()) {
				JsDelivrInput.list.getDefaultModel().clear();
				JsDelivrInput.list.setEmptyText(messageNotFound);
				jsDelivrInput.loading = false;
				return;
			}

			for (AlgoliaPackage pkg : packages) {
				if (text.equals(pkg.getName())) {
					chosenPackage = pkg;
				}
			}

			if (chosenPackage == null) {
				JsDelivrInput.list.getDefaultModel().clear();
				JsDelivrInput.list.setEmptyText(messageNotFound);
				jsDelivrInput.loading = false;
				return;
			}

			if (version == null) {
				new VersionInput(jsDelivrInput.editor, jsDelivrInput.project, chosenPackage).setFont(jsDelivrInput.getFont());
			} else {
				new VersionInput(jsDelivrInput.editor, jsDelivrInput.project, chosenPackage, version).setFont(jsDelivrInput.getFont());
			}
			jsDelivrInput.popup.closePopup();
		}
	}
}
