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

package com.japisoft.framework.xml.refactor2.elements;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.japisoft.framework.xml.refactor2.AbstractRefactor;

public class RenameElement extends AbstractRefactor {
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		if ( localName != null ) {
			if ( localName.equals( oldValue ) )
				localName = newValue;
		}
		
		if ( qName != null ) {
			// With prefix ?
			int i = qName.lastIndexOf( ":" );
			if ( i > -1 ) {
				String tmp = qName.substring( i + 1 );
				if ( tmp.equals( oldValue ) ) {
					qName = qName.substring( 0, i + 1 ) + newValue;
				}
			} else
			if ( qName.equals( oldValue ) ) {
				qName = newValue;
			}
		}

		super.startElement(uri, localName, qName, atts);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if ( localName != null ) {
			if ( localName.equals( oldValue ) )
				localName = newValue;
		}
		
		if ( qName != null ) {
			// With prefix ?
			int i = qName.lastIndexOf( ":" );
			if ( i > -1 ) {
				String tmp = qName.substring( i + 1 );
				if ( tmp.equals( oldValue ) ) {
					qName = qName.substring( 0, i + 1 ) + newValue;
				}
			} else
			if ( qName.equals( oldValue ) ) {
				qName = newValue;
			}
		}
		
		super.endElement(uri, localName, qName);
	}

}
