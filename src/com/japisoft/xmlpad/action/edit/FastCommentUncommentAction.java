package com.japisoft.xmlpad.action.edit;

import javax.swing.JOptionPane;

import com.japisoft.xmlpad.action.XMLActionForSelection;

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
public class FastCommentUncommentAction extends XMLActionForSelection {

	public static final String ID = FastCommentUncommentAction.class.getName();
	
	public boolean notifyAction() {
		String selection = container.getEditor().getSelectedText();
		try {
		if ( selection != null ) {
			String newContent = null;
			if ( selection.startsWith( "<!--" ) 
				&& selection.endsWith( "-->" ) ) {
				newContent = selection.substring( 4, selection.length() - 3 );
			} else {
				if ( selection.indexOf( "<!--" ) > -1 ) {
					JOptionPane.showMessageDialog( container.getView(), "You must remove the comment part before operating", "Error", JOptionPane.ERROR_MESSAGE );
					return false;
				} else
					newContent = "<!--" + selection + "-->";
			}
			container.getEditor().replaceSelection( newContent );
			return true;
		} else
			JOptionPane.showMessageDialog( container.getView(), "No selection found", "Error", JOptionPane.ERROR_MESSAGE );
		} catch( Throwable th ) {
			System.err.println( "Can't comment/uncomment" );
		}
		return false;
	}
	
}
 
