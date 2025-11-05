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

public class DeletePrefix extends AbstractRefactor {

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if (!oldValue.equals(prefix))
			super.startPrefixMapping(prefix, uri);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		if (!oldValue.equals(prefix))
			super.endPrefixMapping(prefix);
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		if ( qName != null ) {
			int i = qName.lastIndexOf( ":" );
			if ( i > -1 ) {
				String tmp = qName.substring( 0, i );
				if ( oldValue.equals( tmp ) )
					qName = qName.substring( i + 1 );
			}
		}

		if ( atts != null && atts.getLength() > 0 ) {
			atts = new AttributesProxy( atts );
		}
		
		super.startElement(uri, localName, qName, atts);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if ( qName != null ) {
			int i = qName.lastIndexOf( ":" );
			if ( i > -1 ) {
				String tmp = qName.substring( 0, i );
				if ( oldValue.equals( tmp ) )
					qName = qName.substring( i + 1 );
			}
		}

		super.endElement(uri, localName, qName);
	}
	
	// -----------------------------------------------------------------------------------

	class AttributesProxy implements Attributes {

		private Attributes ref;

		AttributesProxy(Attributes ref) {
			this.ref = ref;
		}

		public int getIndex(String uri, String localName) {
			return ref.getIndex(uri, localName);
		}

		public int getIndex(String qName) {
			return ref.getIndex(qName);
		}

		public int getLength() {
			return ref.getLength();
		}

		public String getLocalName(int index) {
			String name = ref.getLocalName(index);
			if (oldValue.equals(name))
				return null;
			else
				return name;
		}

		public String getQName(int index) {
			String name = ref.getQName(index);
			int i = name.indexOf(":");
			if (i > -1) {
				String tmp = name.substring(0, i );				
				if (oldValue.equals(tmp))
					return name.substring( i + 1 );
			}

			return name;
		}

		public String getType(int index) {
			return ref.getType(index);
		}

		public String getType(String uri, String localName) {
			return ref.getType(uri, localName);
		}

		public String getType(String qName) {
			return ref.getType(qName);
		}

		public String getURI(int index) {
			return ref.getURI(index);
		}

		private String prepareValue( String value ) {
			if ( value.startsWith( oldValue + ":" ) ) {
				return value.substring( oldValue.length() + 1 );
			} else
				return value;
		}

		public String getValue(int index) {
			String value = ref.getValue(index);
			return prepareValue( value );
		}

		public String getValue(String uri, String localName) {
			String value = ref.getValue(uri, localName);
			return prepareValue( value );
		}

		public String getValue(String qName) {
			String value = ref.getValue(qName);
			return prepareValue( value );
		}

	}

}

