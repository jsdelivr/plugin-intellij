package com.jsdelivr.pluginintellij.packagefile.remotetypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiPackageFile {
	public String name;
	public String hash;
}
