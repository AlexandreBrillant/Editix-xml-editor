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

package com.japisoft.xmlpad.action.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;

import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.dialog.DialogManager;
import com.japisoft.xmlpad.look.LookManager;
import com.japisoft.xmlpad.tree.TreeListeners;

/**
 * Action for searching a node by its tree 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
 * @see XMLAction
 */
public class SearchAction extends XMLAction {

	public static final String ID = SearchAction.class.getName();
	
	public SearchAction() {
		super();
	}

	public boolean notifyAction() {
		JTree t = new JTree();
		LookManager.install( container,t);
		TreeListeners p = new TreeListeners(container, t);
		p.setPopupEnabled( false );
		t.addMouseListener(p);
		p.notifyStructureChanged();

		DialogManager.showDialog(
				SwingUtilities.getWindowAncestor( container.getView() ),
				"Search",
				"Locate a node",
				"Click on a node for highlighting the same editor line",
				null,
				new TreePane( t, p ) );
		
		t.removeMouseListener(p); //JPF
		return VALID_ACTION;
	}

	////////////////////////////////////////////

	/** Tree document */
	class TreePane extends JPanel {
		private int lastCaret = 0;
		private TreeListeners rtt;

		public TreePane( JTree tree, TreeListeners p ) {
			setLayout( new BorderLayout() );
			add( new JScrollPane( tree ) );

			rtt = p;

			setPreferredSize( new Dimension( 300, 400 ) );
			
			tree.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {
						rtt.mouseClicked(e);
					}
				}
			});
		}
	}

}


