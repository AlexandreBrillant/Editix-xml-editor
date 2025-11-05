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

package com.japisoft.framework.xml.refactor2.elements;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.japisoft.framework.xml.refactor2.AbstractRefactor;

public class ConvertAttributesToElement extends AbstractRefactor {

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

		String name = localName;
		if ( name == null ) {
			name = qName;
			int i = name.lastIndexOf( ":" );
			if ( i > -1 ) {
				name = name.substring( i + 1 );
			}
		}

		Attributes atts2 = atts;
		
		// Remove the attributes
		if ( name.equals( oldValue ) ) 
			atts = null;
		
		super.startElement(uri, localName, qName, atts);
		
		if ( atts2 != null && name.equals( oldValue ) ) {
			for ( int i = 0; i < atts2.getLength(); i++ ) {

				String n = atts2.getQName( i );
				String v = atts2.getValue( i );
				super.startElement(
						uri,
						n,
						n,
						null );
				super.characters(
						v.toCharArray(),
						0,
						v.length() );
				super.endElement(
						uri,
						n,
						n );				
			}
		}
	}
	
}

