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

package com.japisoft.stylededitor.model;

import javax.swing.text.Document;
import org.w3c.dom.Text;

public class TextElement extends BasicNodeElement {

	public TextElement(
			Document document, 
			Text node, 
			int startOffset ) {
		super( 
			document, 
			node, 
			startOffset );
		setEndOffset( 
				getStartOffset() + 
					( node.getNodeValue().length() )
		);
	}

	@Override
	public String getName() {
		return getNode().getNodeValue();
	}
	
	public void setName( String newName ) {
		getNode().setNodeValue( newName );
	}
	
	public int getLength() {
		return getNode().getNodeValue().length();
	}
	
	public char getChar( int index ) {
		return getName().charAt( index );
	}
	
	public void deleteChar( int index ) {
		Text t = ( Text )getNode();
		if ( index == t.getLength() ) {
			if ( t.getNextSibling() instanceof Text ) {
				t.setNodeValue( t.getNodeValue() + t.getNextSibling().getNodeValue() );
				setEndOffset( getEndOffset() + t.getNextSibling().getNodeValue().length() );
				t.getParentNode().removeChild(t.getNextSibling());
			}
		} else {
			if ( index < t.getLength() )
				t.deleteData( index, 1 );
		}
	}

}

