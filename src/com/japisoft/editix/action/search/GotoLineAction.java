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

package com.japisoft.editix.action.search;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.Element;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 * Goto to a line number
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class GotoLineAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		String line = EditixFactory.buildAndShowInputDialog( "Go to line" );
		if ( line != null ) {
			try {
				int l = Integer.parseInt( line ) - 1;
				XMLPadDocument document = ( XMLPadDocument )container.getEditor().getDocument();
				try {
					Element ee = document.getDefaultRootElement().getElement( l );
					container.getEditor().setCaretPosition( ee.getStartOffset() );
				} catch( Throwable th ) {
					
				}
			} catch( NumberFormatException exc ) {
			}
		}
	}

}

