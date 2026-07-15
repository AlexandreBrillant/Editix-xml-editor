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

package com.japisoft.editix.action.search;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class BookmarkGoToAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		container.getEditor().requestFocus();
		String loc = ( String )getValue( Action.NAME );
		if ( loc.startsWith( "Cursor at" ) ) {
			// Move the cursor to it
			int cursor = Integer.parseInt( loc.substring( 10 ) );
			container.getEditor().setCaretPosition( cursor );
		} else {

			FPNode rootNode = ( FPNode )container.getTree().getModel().getRoot();
			if ( rootNode != null ) {
				FPNode node = rootNode.getNodeForXPathLocation( loc, true );
				if ( node == null ) {
					EditixFactory.buildAndShowWarningDialog( "Can't find this node " + loc );
				} else {
					container.getEditor().setCaretPosition( node.getStartingOffset() + 1 );
				}
			}
		}

	}

}
