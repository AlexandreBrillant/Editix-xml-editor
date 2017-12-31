package com.japisoft.xmlpad.helper.model;

import javax.swing.DefaultListModel;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;

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
