package com.jsdelivr.pluginintellij.packagefile;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.jsdelivr.pluginintellij.InsertModel;
import com.jsdelivr.pluginintellij.packagefile.remotetypes.ApiPackageFile;
import com.jsdelivr.pluginintellij.packagefile.remotetypes.ApiPackageFiles;
import com.jsdelivr.pluginintellij.packageinsert.InsertOptions;
import com.jsdelivr.pluginintellij.packagename.AlgoliaSearch;
import com.jsdelivr.pluginintellij.packagename.NameThread;
import com.jsdelivr.pluginintellij.ui.JsDelivrInput;

import java.util.List;

public class FileInput extends JsDelivrInput {
	private static final String messageLoading = "Loading...";
	private static final String messageNotFound = "File not found";
	private static final String messagePlaceholder = "Package file";

	private String pkgName;
	private String pkgVersion;

	String pkgFile;
	List<String> files;
	ApiPackageFiles apiPackageFiles;

	public FileInput(Editor editor, Project project, String pkgName, String pkgVersion) {
		super(editor, project, messagePlaceholder);

		this.pkgName = pkgName;
		this.pkgVersion = pkgVersion;

		list.getDefaultModel().clear();
		list.setEmptyText(messageLoading);

		FileThread fileThread = new FileThread(pkgName, pkgVersion, this);
		Thread thread = new Thread(fileThread);
		thread.start();
	}

	public FileInput(Editor editor, Project project, String pkgName, String pkgVersion, String pkgFile) {
		super(editor, project, messagePlaceholder);

		this.pkgName = pkgName;
		this.pkgVersion = pkgVersion;
		this.pkgFile = pkgFile;

		list.getDefaultModel().clear();
		list.setEmptyText(messageLoading);

		FileThread fileThread = new FileThread(pkgName, pkgVersion, this);
		Thread thread = new Thread(fileThread);
		thread.start();
	}

	@Override
	protected void updateAutocomplete(String text) {
		list.getDefaultModel().clear();
		list.setEmptyText(messageLoading);

		for (String file : files) {
			String woMin = text;

			if (text.endsWith(".min.js")) {
				woMin = text.substring(0, text.lastIndexOf(".min.js")) + ".js";
			} else if (text.endsWith(".min.css")) {
				woMin = text.substring(0, text.lastIndexOf(".min.css")) + ".css";
			}

			if ((file.contains(text) || file.contains(woMin)) && (file.endsWith(".js") || file.endsWith(".css"))) {
				if (file.contains(text) && !list.getDefaultModel().contains(file.replaceFirst("/", ""))) {
					list.getDefaultModel().addElement(file.replaceFirst("/", ""));
				}

				String tmp = file.replaceFirst("/", "");

				if (tmp.endsWith(".min.js") || tmp.endsWith(".min.css")) {
					if (!list.getDefaultModel().contains(tmp)) {
						list.getDefaultModel().addElement(tmp);
					}
				}

				if (tmp.endsWith(".js") && !tmp.endsWith(".min.js")) {
					tmp = tmp.substring(0, tmp.lastIndexOf(".js")) + ".min.js";

					if (!list.getDefaultModel().contains(tmp)) {
						list.getDefaultModel().addElement(tmp);
					}
				}

				if (tmp.endsWith(".css") && !tmp.endsWith(".min.css")) {
					tmp = tmp.substring(0, tmp.lastIndexOf(".css")) + ".min.css";

					if (!list.getDefaultModel().contains(tmp)) {
						list.getDefaultModel().addElement(tmp);
					}
				}
			}
		}

		if (list.getDefaultModel().isEmpty()) {
			list.setEmptyText(messageNotFound);
		}
	}

	@Override
	protected boolean inputComplete(String text) {
		list.getDefaultModel().clear();
		list.setEmptyText(messageLoading);

		ApiPackageFile file = null;
		boolean generatedMin = false;
		text = "/" + text;

		if (apiPackageFiles.files == null || apiPackageFiles.files.length == 0) {
			list.setEmptyText(messageNotFound);
			return false;
		}

		for (ApiPackageFile tmp : apiPackageFiles.files) {
			if (tmp.name.equals(text)) {
				file = tmp;
				break;
			}
		}

		if (file == null) {
			String notMin;

			if (text.endsWith(".min.js")) {
				notMin = text.substring(0, text.lastIndexOf(".min")) + ".js";
			} else if (text.endsWith(".min.css")) {
				notMin = text.substring(0, text.lastIndexOf(".min")) + ".css";
			} else {
				list.setEmptyText("File not found");
				return false;
			}

			for (ApiPackageFile tmp : apiPackageFiles.files) {
				if (tmp.name.equals(notMin)) {
					file = tmp;
					generatedMin = true;
					break;
				}
			}
		}

		if (file == null) {
			list.setEmptyText("File not found");
			return false;
		}

		file.name = text;

		new InsertOptions(editor, project, new InsertModel(pkgName, pkgVersion, file), generatedMin).setFont(getFont());
		return true;
	}

	@Override
	protected void previousInput() {
		list.getDefaultModel().clear();
		list.setEmptyText("Loading...");

		NameThread thread = new NameThread(new AlgoliaSearch(), pkgName, pkgVersion, this, true);
		Thread thr = new Thread(thread);
		thr.start();
	}
}
