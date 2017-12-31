package com.japisoft.xmlpad.tree.action;

import javax.swing.JOptionPane;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.ActionModel;

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
public class CommentNode extends AbstractTreeAction {
	
	public static final String ID = CommentNode.class.getName();
	
	public CommentNode() {
		super();
	}
	
	public boolean notifyAction() {
		boolean ok = ActionModel.activeActionByName( SelectNode.class.getName() );
		
		if ( ok ) {
			String content = container.getUIAccessibility().getEditor().getSelectedText();
			
			if ( content.indexOf( "<!--" ) > -1 ) {
				JOptionPane.showMessageDialog( container.getView(), 
						XMLContainer.getLocalizedMessage( "COMMENT_ERROR", "Can't comment this node, you must remove the inner comments" ),
						XMLContainer.getLocalizedMessage( "ERROR", "Error" ),
						JOptionPane.ERROR_MESSAGE );
				container.getUIAccessibility().getEditor().select( 
						container.getCaretPosition(),
						container.getCaretPosition() );

			} else
				container.getUIAccessibility().getEditor().replaceSelection(
						"<!--" + content + "-->" );
		}
		
		return ok;
	}
	
}
