package com.jsdelivr.pluginintellij.packagename;

import com.jsdelivr.pluginintellij.packagename.remotetypes.AlgoliaRepository;
import com.jsdelivr.pluginintellij.ui.IJsDelivrListItem;

public class ListNameItem implements IJsDelivrListItem {
	private String name;
	private AlgoliaRepository repository;

	ListNameItem(String name, AlgoliaRepository repository) {
		this.name = name;
		this.repository = repository;
	}

	public String getRepositoryUrl() {
		return this.repository != null ? this.repository.getUrl() : null;
	}

	public String toString() {
		return name;
	}
}
