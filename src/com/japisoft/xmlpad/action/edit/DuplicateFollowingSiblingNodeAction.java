package com.japisoft.xmlpad.action.edit;

import java.awt.Point;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
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
public class DuplicateFollowingSiblingNodeAction extends XMLAction {

	public boolean notifyAction() {
		FPNode node = container.getCurrentNode();
		if ( node == null ) {
			JOptionPane.showMessageDialog( container.getView(), "Can't find a following sibling node", "Error", JOptionPane.ERROR_MESSAGE );
			return false;
		}

		int caret = container.getEditor().getCaretPosition();

		FPNode followingSiblingCandidate = null;

		for ( int i = 0; i < node.childCount(); i++ ) {
			FPNode child = node.childAt( i );
			if ( child.getStartingOffset() > caret ) {
				followingSiblingCandidate = child;
				break;
			}
		}

		if ( followingSiblingCandidate != null ) {

			Point n = CopyNodeAction.getNodeOffset( followingSiblingCandidate );
			try {
				String mustCopy = container.getEditor().getText( n.x, n.y - n.x + 1 );
				
				container.getEditor().insertText( mustCopy );
				
			} catch (BadLocationException e) {
				JOptionPane.showMessageDialog( container.getView(), "Can't find a following sibling node", "Error", JOptionPane.ERROR_MESSAGE );							
			}
			
		} else {

			JOptionPane.showMessageDialog( container.getView(), "Can't find a following sibling node", "Error", JOptionPane.ERROR_MESSAGE );			

		}

		return false;
	}

}
 
