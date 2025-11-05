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

package com.japisoft.xmlpad.helper.model;

import javax.swing.DefaultListModel;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class AttributeValuesHelper extends AbstractHelper {

	private AttDescriptor descriptor;
	private char delimiter; 
	
	public AttributeValuesHelper( AttDescriptor descriptor, char delimiter ) {
		this.descriptor = descriptor;
		this.delimiter = delimiter;
	}

	public String getTitle() {
		return descriptor.getName();
	}

	protected boolean hasElements() {
		return ( descriptor != null && descriptor.hasEnumValues() );
	}
	
	protected void prepareDocumentBeforeInserting( XMLPadDocument document, int offset ) {
		try {
			// Search for another delimiter without a '='
			boolean firstDelimiter = false;
			boolean secondDelimiter = false;
			int location = 0;
			for ( int i = ( offset ); i < document.getLength(); i++ ) {
				char ch = document.getText( i, 1 ).charAt( 0 );
				if ( ch == delimiter ) {
					if ( !firstDelimiter ) {
						firstDelimiter = true;
						location = i;
					} else {
						secondDelimiter = true;
						location = i;
						break;
					}
				}
				if ( ch == '>' )
					break;
				if ( ch == '\n' )
					break;
				if ( ch == '=' ) {
					break;
				}
			}
			
			if ( firstDelimiter || 
					secondDelimiter ) {
				
				document.remove( 
						offset, 
						location - offset + 1 );
			}

		} catch (BadLocationException e) {
		}
	}
	
	protected void fillList( FPNode node, DefaultListModel model ) {
		String[] values = descriptor.getEnumValues();
		if ( values != null ) {
			for ( int i = 0; i < values.length; i++ ) {
				model.addElement( new AttValueDescriptor( values[ i ], delimiter ) );
			}
		}
	}
	
}

