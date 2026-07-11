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
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;


/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class NamespaceAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		FPNode node = container.getCurrentNode();
		if ( node == null ) {
			EditixFactory.buildAndShowErrorDialog( "No selected node" );
			return;
		}
		
		if ( container.hasErrorMessage() ) {
			EditixFactory.buildAndShowErrorDialog( "Please fix your document before" );
			return;
		}
		
		NamespacePanel panel = new NamespacePanel( node );

		if ( DialogManager.showDialog(
				EditixFrame.THIS,
				"Namespace manager",
				"Namespace manager",
				"Add or remove a namespace definition. The current one is for the namespace of the selected node",
				null,
				panel,
				new Dimension( 400, 400 )) == 
					DialogManager.OK_ID ) {
			panel.updateNode();
			XMLPadDocument doc = container.getXMLDocument();
			String openPart = node.openDeclaration();
			String closePart = node.closeDeclaration();
			if ( node.isAutoClose() )
				closePart = null;
			doc.updateElement( openPart, closePart, node.getStartingOffset(), node.getStoppingOffset() );
		}
	}

}
