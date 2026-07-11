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

import javax.swing.AbstractAction;
import javax.swing.table.DefaultTableModel;

import com.japisoft.editix.action.search.XPathDialog;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.BrowserCaller;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class XPathBuilderAction extends AbstractAction {

	String[] items = null;
	DefaultTableModel vars = null;
	DefaultTableModel namespaces = null;

	public void actionPerformed(ActionEvent e) {
		
		String title = "Build an XPath 1.0 expression and apply it from the current node or root node. A dialog will appear for non node result";
			
		XPathDialog dialog = new XPathDialog(
			title,
			false, 
			vars, 
			namespaces );
		if ( items != null )
			dialog.setItems( items );					
		dialog.setVisible( true );
		dialog.dispose();
		items = dialog.getItems();
		vars = dialog.getVariablesModel();
		namespaces = dialog.getNamespacesModel();

	}

}
