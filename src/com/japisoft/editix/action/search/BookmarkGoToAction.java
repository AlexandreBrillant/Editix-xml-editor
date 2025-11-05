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
import javax.swing.Action;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
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

