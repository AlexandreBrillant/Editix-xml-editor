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

package com.japisoft.editix.action.tree;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class CollapseAction extends AbstractAction {
	
	private void collapseNode( FPNode sn, JTree t ) {
		FPNode sni = sn;
		ArrayList l = new ArrayList();
		l.add( sn );
		while ( sn != null ) l.add( 0, sn = sn.getFPParent() );
		l.remove( 0 );
		
		t.collapsePath( new TreePath( l.toArray() ) );
		
		for ( int i = 0; i < sni.childCount(); i++ ) {
			collapseNode( sni.childAt( i ), t );
		}
	}

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();
		if ( container != null ) {
			JTree t = container.getTree();
			TreePath tp = t.getSelectionPath();
			if ( tp != null ) {
				FPNode sn = ( FPNode )tp.getLastPathComponent();
				if ( sn != null )
					collapseNode( sn, t );
			}
		}
	}

}

