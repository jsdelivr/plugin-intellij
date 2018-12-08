package com.jsdelivr.pluginintellij.ui;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Function;

public class JsDelivrList extends JBList<IJsDelivrListItem> {
	private Font font;
	private JBScrollPane pane;

	public JsDelivrList(Function<IJsDelivrListItem, Void> onItemSelected) {
		super(new DefaultListModel<>());

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectedIndex(0);
		setVisibleRowCount(5);
		setEmptyText("Loading...");

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				JBList lst = (JBList) mouseEvent.getSource();
				int index = lst.locationToIndex(mouseEvent.getPoint());

				if (index >= 0) {
					IJsDelivrListItem object = (IJsDelivrListItem) lst.getModel().getElementAt(index);
					onItemSelected.apply(object);
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent event) {
				JBList lst = (JBList) event.getSource();
				int index = lst.locationToIndex(event.getPoint());

				if (index >= 0) {
					lst.setSelectedIndex(index);
				}
			}
		});

		pane = new JBScrollPane(this);
		pane.setBorder(null);
	}

	public Font getFont() {
		return this.font;
	}

	public void setFont(Font font) {
		super.setFont(font);
		this.font = font;
	}

	public JBScrollPane getPane() {
		return pane;
	}

	public IJsDelivrListItem getSelectedItem() {
		if (getSelectedValue() == null) {
			return null;
		}

		return getSelectedValue();
	}

	public void resetSelection() {
		if (!isEmpty()) {
			setSelectedIndex(0);
		}
	}

	public void keyEvent(KeyEvent ke) {
		if (ke.getID() == KeyEvent.KEY_PRESSED) {
			if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
				setSelectedIndex(getSelectedIndex() + 1);
			} else if (ke.getKeyCode() == KeyEvent.VK_UP) {
				setSelectedIndex(getSelectedIndex() - 1);
			}
		}

		ensureIndexIsVisible(getSelectedIndex());
	}

	public DefaultListModel<IJsDelivrListItem> getDefaultModel() {
		return (DefaultListModel<IJsDelivrListItem>) getModel();
	}
}
