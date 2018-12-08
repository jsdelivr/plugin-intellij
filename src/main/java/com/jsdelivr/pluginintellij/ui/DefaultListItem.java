package com.jsdelivr.pluginintellij.ui;

public class DefaultListItem implements IJsDelivrListItem {
	private String value;

	public DefaultListItem(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}
}
