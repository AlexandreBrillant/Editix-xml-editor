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

package com.japisoft.xmlpad.action.edit;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.action.XMLAction;

import java.awt.Point;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

public class TrimAction extends XMLAction {

	@Override
	public boolean notifyAction() {
		if ( container.getTreeListeners() == null )
			return INVALID_ACTION;
		if ( container.getTree() == null )
			return INVALID_ACTION;
		FPNode node = container.getCurrentNode();
		if ( node.isTag() ) {
			if ( node.childCount() == 0 ) {
				return INVALID_ACTION;
			}
			node = node.childAt( 0 );
			if ( node.isTag() )
				return INVALID_ACTION;
		}
		Point p = CopyNodeAction.getNodeOffset( node );
		try {
			for ( int i = 1; i < 100; i++ ) {
				String tmp = container.getEditor().getText( p.x - i, 1 );
				if ( Character.isWhitespace( tmp.charAt( 0 ) ) ) {
					p.x--;
					i--;
					continue;
				} else
					break;
			}
			for ( int i = 0; i < 100; i++ ) {
				String tmp = container.getEditor().getText( p.y + i, 1 );
				if ( Character.isWhitespace( tmp.charAt( 0 ) ) ) {
					p.y++;
					continue;
				} else
					break;
			}			
		
			container.getEditor().select( p.x, p.y );
			String txt = container.getEditor().getSelectedText();
			txt = txt.trim();
			container.getEditor().replaceSelection( txt );
			return VALID_ACTION;
		} catch( BadLocationException exc ) {
			return INVALID_ACTION;
		}
	}

}

