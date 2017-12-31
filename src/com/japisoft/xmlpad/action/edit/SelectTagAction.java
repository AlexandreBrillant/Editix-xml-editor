package com.japisoft.xmlpad.action.edit;

import javax.swing.JOptionPane;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;

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
public class SelectTagAction extends XMLAction {

	public static final String ID = SelectTagAction.class.getName();
	
	protected boolean autoRequestFocus() { return false; }

	public boolean notifyAction() {
		if ( container.getTreeListeners() == null )
			return INVALID_ACTION;
		if ( container.getTree() == null )
			return INVALID_ACTION;
		if  ( container.getTree().getSelectionPath() == null )
			return INVALID_ACTION;
		FPNode n = ( FPNode )container.getTree().getSelectionPath().getLastPathComponent();
		if (n == null) {
			JOptionPane.showMessageDialog( container.getView(), "No Tag Found", "Error", JOptionPane.ERROR_MESSAGE );
			return INVALID_ACTION;
		}
		selectNode( container, n );
		return VALID_ACTION;		
	}
	
	public static void selectNode( 
			XMLContainer container, 
			FPNode n ) {
		int start = n.getStartingOffset();
		int stop = n.getStoppingOffset();
		if ( n.isTag() )
			stop++;
		container.getEditor().requestFocus();
		container.getEditor().select( start, stop );		
	}

}
