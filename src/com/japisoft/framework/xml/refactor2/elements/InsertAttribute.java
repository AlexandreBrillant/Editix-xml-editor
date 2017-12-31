package com.japisoft.framework.xml.refactor2.elements;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;

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
public class InsertAttribute extends AbstractRefactor {

	private String newAttributeName;
	private String newAttributeValue;

	public void setNewValue(String newValue) {
		
		int stop1 = newValue.indexOf( "=" );
		if ( stop1 == -1 ) {
			newAttributeName = newValue;
			newValue = newValue + "=\"\"";
		} else
			newAttributeName = newValue.substring( 0, stop1 );

		int stop2 = newValue.indexOf( "\"" );
		int stop3 = newValue.lastIndexOf( "\"" );

		if ( stop2 == -1 || 
				stop3 == -1 || 
					( stop3 == stop2 ) ) {
			throw new RuntimeException( "Wrong attribute syntax : Must write name=\"value\"" );
		}

		newAttributeValue = newValue.substring( stop2 + 1, stop3 );
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		String name = localName;
		if ( name == null ) {
			name = qName;
			int i = name.lastIndexOf( ":" );
			if ( i > -1 ) {
				name = name.substring( i + 1 );
			}
		}

		if ( name.equals( oldValue ) ) {
			atts = new AttributesProxy( atts );
		}

		super.startElement( 
				uri, 
				localName, 
				qName, 
				atts
		);

	}

	//////////////////////////////////////////////////////////////////////////////
	
	class AttributesProxy implements Attributes {

		private Attributes ref;
		private boolean ignoreIt = false;
		
		AttributesProxy( Attributes ref ) {
			this.ref = ref;
			// Check if this attribute doesn't already exist
			for ( int i = 0; i < ref.getLength(); i++ ) {
				if ( newAttributeName.equals( getQName( i ) ) ) {
					ignoreIt = true;
					break;
				}
			}
		}

		public int getIndex(String uri, String localName) {
			return ref.getIndex( uri, localName );
		}

		public int getIndex(String qName) {
			return ref.getIndex( qName );
		}

		public int getLength() {
			return ref.getLength() + ( !ignoreIt ? 1 : 0 );
		}

		public String getLocalName(int index) {
			if ( !ignoreIt && index == ref.getLength() ) {
				return newAttributeName;
			}
			String name = ref.getLocalName( index );
			return name;
		}

		public String getQName(int index) {
			if ( !ignoreIt && index == ref.getLength() ) {
				return newAttributeName;
			}

			String name = ref.getQName( index );
			if ( name.equals( oldValue ) )
				return newValue;
			int i = name.indexOf( ":" );
			if ( i > -1 ) {
				String tmp = name.substring( i + 1 );
				if ( tmp.equals( oldValue ) ) 
					return name.substring( 0, i + 1 ) + newValue;
			}
			return name;
		}

		public String getType(int index) {
			return ref.getType( index );
		}

		public String getType(String uri, String localName) {
			return ref.getType( uri, localName );
		}

		public String getType(String qName) {
			return ref.getType( qName );
		}

		public String getURI(int index) {
			return ref.getURI( index );
		}

		public String getValue(int index) {
			
			if ( !ignoreIt && index == ref.getLength() ) {
				return newAttributeValue;
			}
			
			return ref.getValue( index );
		}

		public String getValue(String uri, String localName) {			
			return ref.getValue( uri, localName );
		}

		public String getValue(String qName) {
			return ref.getValue( qName );
		}
		
	}
	
}
