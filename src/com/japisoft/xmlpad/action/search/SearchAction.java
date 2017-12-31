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
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

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

// SearchAction ends here
