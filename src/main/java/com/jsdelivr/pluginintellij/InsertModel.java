package com.jsdelivr.pluginintellij;

import com.jsdelivr.pluginintellij.packagefile.remotetypes.ApiPackageFile;

public class InsertModel {
	public String name;
	public String version;
	public ApiPackageFile file;

	public InsertModel(String name, String version, ApiPackageFile file) {
		this.name = name;
		this.version = version;
		this.file = file;
	}
}
