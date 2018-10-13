package com.jsdelivr.pluginintellij.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.*;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;

public class JsDelivrPopup {
	JBPopup popup;
	Editor editor;

	public static final Dimension popupDim = new Dimension(200, 62);

	public JsDelivrPopup(Editor editor, KeyEventDispatcher keyDispatcher, JComponent... components) {
		this.editor = editor;

		FormBuilder formBuilder = FormBuilder.createFormBuilder();

		for (JComponent component : components) {
			formBuilder.addComponent(component);
		}

		ComponentPopupBuilder builder = JBPopupFactory.getInstance().createComponentPopupBuilder(formBuilder.getPanel(), null);
		popup = builder.setModalContext(true).setRequestFocus(true).setResizable(true).setMovable(true).createPopup();

		popup.setMinimumSize(popupDim);

		// Under caret position
		popup.showInBestPositionFor(editor);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);

		popup.addListener(new JBPopupListener() {
			@Override
			public void onClosed(LightweightWindowEvent event) {
				if (keyDispatcher != null) {
					KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyDispatcher);
				}
			}
		});
	}

	public void closePopup() {
		if (popup != null) {
			popup.cancel();
			popup.dispose();
			popup = null;
		}
	}
}
