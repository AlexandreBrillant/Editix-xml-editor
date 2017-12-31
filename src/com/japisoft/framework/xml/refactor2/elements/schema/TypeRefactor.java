package com.japisoft.framework.xml.refactor2.elements.schema;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.japisoft.framework.xml.parser.node.FPNode;
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
public class TypeRefactor extends AbstractRefactor {

	public boolean requireNewValue() {
		return true;
	}

	public boolean process(FPNode node) {

		// For all references
		if ( node.matchContent( "complexType" )
				&& node.hasAttribute( "name" ) ) {
			oldValue = node.getAttribute( "name" );			
			return true;
		}

		if ( node.matchContent( "simpleType" )
				&& node.hasAttribute( "name" ) ) {
			oldValue = node.getAttribute( "name" );			
			return true;
		}

		if ( node.matchContent( "element" ) ||
				node.matchContent( "attribute" ) ) {
			if ( node.hasAttribute( "type" ) ) {
				oldValue = node.getAttribute( "type" );
				if ( !oldValue.startsWith( "xsd:" ) &&
						!oldValue.startsWith( "xs:" ) ) {	
					int i = oldValue.lastIndexOf( ":" );
					if ( i > -1 ) {
						oldValue = oldValue.substring( i + 1 );
					}
					return true;
				}
			}
		}

		if ( node.matchContent( "restriction" ) ||
				node.matchContent( "extension" ) ) {
			if ( node.hasAttribute( "base" ) ) {
				oldValue = node.getAttribute( "base" );
				if ( !oldValue.startsWith( "xsd:" ) &&
						!oldValue.startsWith( "xs:" ) ) {	
					int i = oldValue.lastIndexOf( ":" );
					if ( i > -1 ) {
						oldValue = oldValue.substring( i + 1 );
					}
					return true;
				}				
			}
		}

		return false;
	}

	public String getTitle(FPNode node) {
		return "The type '" + oldValue + "'";
	}

	public void startElement(
			String uri, 
			String localName, 
			String qName, 
			Attributes atts ) throws SAXException {

		if ( "complexType".equals( localName ) ) {
			if ( atts.getValue( "name" ) != null && 
					!atts.getValue( "name" ).equals( "" ) ) {
				atts = new AttributesProxy( atts, "name" );
			}
		} else
		if ( "simpleType".equals( localName ) ) {
			if ( atts.getValue( "name" ) != null && 
					!atts.getValue( "name" ).equals( "" ) ) {
				atts = new AttributesProxy( atts, "name" );
			}
		} else
		if ( "extension".equals( localName ) ) {
			if ( atts.getValue( "base" ) != null && 
					!atts.getValue( "base" ).equals( "" ) )
				atts = new AttributesProxy( atts, "base" );{
			}
		} else
		if ( "restriction".equals( localName ) ) {
			if ( atts.getValue( "base" ) != null && 
					!atts.getValue( "base" ).equals( "" ) )
				atts = new AttributesProxy( atts, "base" );{
			}
		} else
		if ( "element".equals( localName ) ) {
			if ( atts.getValue( "type" ) != null && 
					!atts.getValue( "type" ).equals( "" ) )
				atts = new AttributesProxy( atts, "type" );{
			}			
		} else
		if ( "attribute".equals( localName ) ) {
			if ( atts.getValue( "type" ) != null && 
					!atts.getValue( "type" ).equals( "" ) )
				atts = new AttributesProxy( atts, "type" );{
			}			
		}

		super.startElement(uri, localName, qName, atts);
	}

	// ---------------------------------------------------------------
	
	class AttributesProxy implements Attributes {

		private Attributes ref;
		private String attName;
		
		AttributesProxy( Attributes ref, String attName ) {
			this.ref = ref;
			this.attName = attName;
		}

		public int getIndex(String uri, String localName) {
			return ref.getIndex( uri, localName );
		}

		public int getIndex(String qName) {
			return ref.getIndex( qName );
		}

		public int getLength() {
			return ref.getLength();
		}

		public String getLocalName(int index) {
			return ref.getLocalName( index );
		}

		public String getQName(int index) {
			return ref.getQName( index );
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

		private String processValue( String name, String value ) {
			int i = value.lastIndexOf( ":" );
			String prefix = "";
			if ( i > -1 ) {
				prefix = value.substring( 0 , i + 1 );
				value = value.substring( i + 1 );
			}
			if ( attName.equals( name ) && 
					value.equals( oldValue ) )
				return prefix + newValue;
			return value;
		}

		public String getValue(int index) {
			return processValue( ref.getQName( index ), ref.getValue( index ) );
		}

		public String getValue(String uri, String localName) {
			return processValue( localName, ref.getValue( uri, localName ) );
		}

		public String getValue(String qName) {
			return processValue( qName, ref.getValue( qName ) );
		}

	}

}
