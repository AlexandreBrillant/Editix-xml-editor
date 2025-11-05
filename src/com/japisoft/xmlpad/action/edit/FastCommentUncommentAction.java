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

import javax.swing.JOptionPane;

import com.japisoft.xmlpad.action.XMLActionForSelection;

/**
 * Fast comment/uncomment
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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
 
