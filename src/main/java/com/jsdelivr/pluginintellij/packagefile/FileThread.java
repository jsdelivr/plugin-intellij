package com.jsdelivr.pluginintellij.packagefile;

import com.intellij.openapi.application.ApplicationManager;
import com.jsdelivr.pluginintellij.packagefile.remotetypes.ApiPackageFile;

import java.util.ArrayList;

public class FileThread implements Runnable {
	private String pkgName;
	private String pkgVersion;
	private FileInput fileInput;

	FileThread(String pkgName, String pkgVersion, FileInput fileInput) {
		this.pkgName = pkgName;
		this.pkgVersion = pkgVersion;
		this.fileInput = fileInput;
	}

	@Override
	public void run() {
		fileInput.loading = true;
		fileInput.apiPackageFiles = PackageFiles.getFiles(pkgName, pkgVersion);

		ApplicationManager.getApplication().invokeLater(() -> {
			ApplicationManager.getApplication().runWriteAction(() -> {
				fileInput.files = new ArrayList<>();

				if (fileInput.apiPackageFiles != null) {
					for (ApiPackageFile file : fileInput.apiPackageFiles.files) {
						fileInput.files.add(file.name);
					}

					if (fileInput.pkgFile == null) {
						fileInput.updateAutocomplete(fileInput.inputField.getText().equals(fileInput.placeholder) ? "" : fileInput.inputField.getText());
						fileInput.list.resetSelection();
					} else {
						fileInput.inputField.setText(fileInput.pkgFile);
						fileInput.inputField.setForeground(fileInput.editor.getContentComponent().getForeground());
						fileInput.updateAutocomplete(fileInput.pkgFile);
						fileInput.list.resetSelection();
					}
				} else if (PackageFiles.error != null) {
					fileInput.list.setEmptyText(PackageFiles.error.message);
				} else {
					fileInput.list.setEmptyText("Package not found");
				}
			});
		});

		fileInput.loading = false;
	}
}
