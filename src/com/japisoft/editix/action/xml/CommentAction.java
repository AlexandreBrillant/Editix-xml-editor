package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
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
public class CommentAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "No document" );
			return;
		}
		
		// Check if we are inside a comment
		boolean in = container.getXMLDocument().isInsideComment( container.getEditor().getCaretPosition() );
		if ( in ) {
			// Remove the comment
			Integer[] l = container.getXMLDocument().getCommentDelimiters( container.getEditor().getCaretPosition() );
			if ( l != null ) {
				int start = l[ 0 ].intValue();
				int stop = l[ 1 ].intValue();
				container.getEditor().select( 
						start, 
						stop + 1 );
				container.getEditor().replaceSelection( 
						container.getEditor().getSelectedText().substring(
								4,
								container.getEditor().getSelectedText().length() - 3
						)
				);
				return;
			} else {
				EditixFactory.buildAndShowErrorDialog( "Can't remove the current comment" );
				return;
			}
		}

		String selection = container.getEditor().getSelectedText();
		if ( selection == null ) {
			// Try with the current node
			FPNode n = container.getCurrentElementNode();
			if ( n != null ) {
				int start = n.getStartingOffset();
				int stop = n.getStoppingOffset();
				container.getEditor().select( start, stop + 1 );
				selection = container.getEditor().getSelectedText();
			}
		}

		if ( selection != null ) {
				// Test for comment inside
				if ( selection.indexOf( "<!--" ) > -1 ) {
					EditixFactory.buildAndShowWarningDialog( "Please remove the comment(s) inside before operating" );
					return;
				}			
				String newContent = "<!--" + selection + "-->";
				container.getEditor().replaceSelection( newContent );
		} else {
			try {
				// Insert empty comment at the cursor location
				container.getEditor().getDocument().insertString(
						container.getEditor().getCaretPosition(),
						"<!-- -->", null );
				container.getEditor().setCaretPosition(
						container.getEditor().getCaretPosition() + 4 );
			} catch (BadLocationException e1) {
				EditixFactory.buildAndShowErrorDialog( "Can't comment" );
			}
		}
			
	}

}
