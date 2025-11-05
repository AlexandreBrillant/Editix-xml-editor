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

package com.japisoft.editix.action.xml.refactor;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;
import com.japisoft.framework.xml.refactor2.elements.SurroundElement;
import com.japisoft.xmlpad.XMLContainer;

public class RefactorSurroundElementAction extends FormatAction {

	protected boolean isIllegalName( String txt ) {
		if ( "".equals( txt ) )
			return true;
		for ( int i = 0; i < txt.length(); i++ )
			if ( Character.isWhitespace( txt.charAt( i ) ) )
				return true;
		return false;
	}

	protected AbstractRefactor getRefactor() {
		
		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();
		FPNode element = container.getCurrentElementNode();
		if ( element == null ) {
			throw new RuntimeException( "No current element" );
		}

		String oldValue = element.getContent();
		String newValue = 
			EditixFactory.buildAndShowInputDialog( "Surrounding element name" );
		if ( newValue == null ) {
			throw new RuntimeException( "" );
		}

		SurroundElement re = new SurroundElement();
		re.setOldValue( oldValue );
		re.setNewValue( newValue );

		// Check for space
		if ( isIllegalName( newValue ) ) {
			throw new RuntimeException( "Illegal name" );
		}
		
		return re;
	}

}

