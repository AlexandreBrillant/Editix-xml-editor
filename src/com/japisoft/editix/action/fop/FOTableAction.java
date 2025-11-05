// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.action.fop;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.wizard.WizardContext;
import com.japisoft.editix.wizard.WizardContextFactory;
import com.japisoft.editix.wizard.table.TableWizard;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.xml.parser.node.FPNode;

public class FOTableAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
		TableWizard tw = new TableWizard();
		tw.setContext( getContext() );
		if ( DialogManager.showDialog( 
				EditixFrame.THIS, 
				"New Table", 
				"Create a new table", 
				"Generate a new table at the current location", 
				null, 
				tw ) == DialogManager.OK_ID ) {
			FPNode node = tw.getResult();
			EditixFrame.THIS.getSelectedContainer().insertText( node.getRawXML( 1 ) );
		}
	}

	protected WizardContext getContext() {
		return WizardContextFactory.getInstance().getContext( "FO" );
	}

}

