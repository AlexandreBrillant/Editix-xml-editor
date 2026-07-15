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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

/**
 * Save all modified documents
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class SaveAllAction extends AbstractAction {

	public static boolean RETURN_STATUS = true;

	public void actionPerformed(ActionEvent e) {
		if ( EditixFrame.THIS.isEmpty() )
			return;

		boolean modified = false;
		for ( int i = 0; i < EditixFrame.THIS.getXMLContainerCount(); i++ ) {
			XMLContainer container = EditixFrame.THIS.getXMLContainer( i );
			if ( container != null && 
					container.getEditor().isDocumentModified() ) {
				modified = true;
				break;
			}
		}

		if ( !modified ) {
			return;
		}

		SaveAllDialog cad = new SaveAllDialog();
		cad.setVisible(true);

		if (cad.isOk()) {
			RETURN_STATUS = true;
			// save the selection
			for (int i = 0; i < cad.getSelections().size(); i++) {
				boolean ok =
					((Boolean) cad.getSelections().get(i)).booleanValue();
				if (ok) {
					EditixFrame.THIS.activeXMLContainer(
						i);
					SaveAction.save_action();
				}
			}
		} else
			RETURN_STATUS = false;
	}

}
