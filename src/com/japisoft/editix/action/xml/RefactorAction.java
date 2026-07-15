// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.refactor.Refactor;
import com.japisoft.framework.xml.refactor.RefactorManager;
import com.japisoft.framework.xml.refactor.elements.RefactorObj;
import com.japisoft.framework.xml.refactor.ui.RefactorUI;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.xml.FormatAction;

public class RefactorAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		final XMLContainer container = EditixFrame.THIS
				.getSelectedContainer();

		if (container == null)
			return;

		if (EditixFactory.mustSaveDialog(container))
			return;

		boolean ok = com.japisoft.xmlpad.action.ActionModel.activeActionByName(
				com.japisoft.xmlpad.action.ActionModel.SAVE_ACTION, container,
				container.getEditor());

		if (!ok)
			return;

		ArrayList al = (ArrayList) container.getDocumentInfo().getProperty(
				"refactor");
		RefactorManager.removeNonDefaultRefactors();
		if (al != null) {
			for (int i = 0; i < al.size(); i++) {
				RefactorManager.addRefactorAt((RefactorObj) al.get(i), i);
			}
		}

		final RefactorUI ui = new RefactorUI(container.getCurrentElementNode());

		if (DialogManager
				.showDialog(
						EditixFrame.THIS,
						"Refactor",
						"XML refactoring",
						"Choose a refactor element and click on the Action column\nYou can set several refactor elements\nATTR is for ATTRIBUTE",
						null, ui) == DialogManager.OK_ID) {

			ui.dispose();

			try {
				Refactor r = new Refactor();
				Boolean b = (Boolean) container
						.getProperty(FormatAction.PREF_APOSENTITY);
				if (b != null)
					r.setReplaceAPos(b.booleanValue());

				r.setReplaceAmp(Preferences.getPreference("xmlconfig",
						"format-replaceAmp", true));
				r.setReplaceGt(Preferences.getPreference("xmlconfig",
						"format-replaceGt", true));
				r.setReplaceLt(Preferences.getPreference("xmlconfig",
						"format-replaceLt", true));
				r.setReplaceAPos(Preferences.getPreference("xmlconfig",
						"format-replaceAPos", true));
				r.setReplaceQuote(Preferences.getPreference("xmlconfig",
						"format-replaceQuote", true));

				Refactor.INDENT_SIZE_PROPERTY = Preferences.getPreference(
						"xmlconfig", "format-space", 1);

				String xpath = null;

				if (ui.getModel().isRelativeRefactoring()) {
					xpath = container.getCurrentElementNode()
							.getXPathLocation();
					if (xpath != null) {
						r.setContext(xpath);
					}
				}

				String result = r.refactor(new File(container
						.getCurrentDocumentLocation()), ui.getModel()
						.getRefactorObjs());

				if (xpath == null) {
					container.setText(result);
				} else {

					int start = container.getCurrentElementNode()
							.getStartingOffset();
					int endOffset = container.getCurrentElementNode()
							.getStoppingOffset();
					container.getEditor().select(start, endOffset);
					container.getEditor().replaceSelection(result);
					container.getEditor().select(container.getCaretPosition(),
							container.getCaretPosition());

				}

			} catch (Exception e1) {
				EditixFactory.buildAndShowErrorDialog("Cannot refactor "
						+ e1.getMessage());
			}
		}
		
	}
}
