package com.jsdelivr.pluginintellij.packagefile.remotetypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiPackageFiles {
	public ApiPackageFile[] files;
}
