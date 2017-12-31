package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.refactor.Refactor;
import com.japisoft.framework.xml.refactor.RefactorManager;
import com.japisoft.framework.xml.refactor.elements.RefactorObj;
import com.japisoft.framework.xml.refactor.ui.RefactorUI;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.xml.FormatAction;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class RefactorAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		//£££
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
		//££
		
	}
}
