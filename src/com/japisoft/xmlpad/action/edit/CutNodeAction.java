package com.japisoft.xmlpad.action.edit;

import java.awt.Point;

import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

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
public class CutNodeAction extends CopyNodeAction {

	public static final String ID = CutNodeAction.class.getName();

	public boolean notifyAction() {
		boolean res = super.notifyAction();
		if ( res == VALID_ACTION ) {
			// Cut it

			return cutAction( container, container.getCurrentNode() );
			
		}

		return res;
	}
	
	public static boolean cutAction( 
		XMLContainer container, 
		FPNode node ) {

		Point n = getNodeOffset( node );
		if ( n == null )
			return INVALID_ACTION;
		container.requestFocus();

		try {
			container.getXMLDocument().replace(
				n.x, 
				( n.y - n.x + 1 ), 
				"", 
				null 
			);
		} catch (BadLocationException e) {
			return INVALID_ACTION;
		}
		return VALID_ACTION;
	}

}
