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

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.hexa.HexaPanel;
import com.japisoft.editix.ui.hexa.ShowRowHexaListener;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.xmlpad.XMLContainer;

public class HexaAction extends AbstractAction {
	
	public void actionPerformed( ActionEvent e ) {	
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowWarningDialog(
				"Can't find a document ?"
			);
		} else {
			HexaPanel hp = new HexaPanel(
				container.getDocument(),
				container.getCaretRow()
			);
						
			hp.setHexaListener(
				new ShowRowHexaListener( container )
			);
			
			DialogManager.showDialog(
				null,
				"Hexadecimal editor",
				"Hexadecimal mode",
				"Edit you document in hexadecimal\nDouble-click on a cell for editing the character, it will update your current document",
				null,
				hp,
				new DialogActionModel( DialogActionModel.DEFAULT_OKACTION ),
				new Dimension( 800, 600 )
			);

		}
	}

}
