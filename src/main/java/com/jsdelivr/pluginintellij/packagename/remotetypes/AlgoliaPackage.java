package com.jsdelivr.pluginintellij.packagename.remotetypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlgoliaPackage {
	private String name;
	private List<String> versions;
	private String version;
	private AlgoliaRepository repository;

	public String getName() {
		return name;
	}

	public AlgoliaPackage setName(String name) {
		this.name = name;
		return this;
	}

	public AlgoliaRepository getRepository() {
		return repository;
	}

	public AlgoliaPackage setRepository(AlgoliaRepository repository) {
		this.repository = repository;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public AlgoliaPackage setVersion(String version) {
		this.version = version;
		return this;
	}

	public List<String> getVersions() {
		return versions;
	}

	public AlgoliaPackage setVersions(Map<String, String> versions) {
		this.versions = new ArrayList<>(versions.keySet());
		return this;
	}
}
