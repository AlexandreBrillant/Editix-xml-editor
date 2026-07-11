// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

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
