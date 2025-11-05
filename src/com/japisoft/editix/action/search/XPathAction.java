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

package com.japisoft.editix.action.search;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.table.DefaultTableModel;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class XPathAction extends AbstractAction {

	String[] items = null;
	DefaultTableModel vars = null;
	DefaultTableModel namespaces = null;

	public void actionPerformed(ActionEvent e) {
		
		if ( Manager.isFree() ) {		

			EditixFactory.buildAndShowInformationDialog( "This action is not available inside the Free Edition.\nPlease look at http://www.editix.com" );
			BrowserCaller.displayURL( "http://www.editix.com" );
			
		} else {
			//���
			String title = "Find/Build an XPath 1.0 expression and apply it from the current node \n or root node. This dialog applies only for searching nodes";
				
			XPathDialog dialog = new XPathDialog(
				title,
				true, 
				vars, 
				namespaces );
	
			if ( items != null )
				dialog.setItems( items );					
			dialog.setVisible( true );
			dialog.dispose();
			items = dialog.getItems();
			vars = dialog.getVariablesModel();
			namespaces = dialog.getNamespacesModel();
			//��
		}
	}

}

