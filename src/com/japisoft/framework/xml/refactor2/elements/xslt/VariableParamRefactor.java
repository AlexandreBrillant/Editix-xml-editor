package com.japisoft.framework.xml.refactor2.elements.xslt;

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
public class VariableParamRefactor extends AbstractRefactor {

	public boolean requireNewValue() {
		return true;
	}

	private String extractVariableName( FPNode node ) {
		if ( node.matchContent( "variable" ) && 
				node.hasAttribute( "name" ) )
			return node.getAttribute( "name" );

		if ( node.matchContent( "param" ) && 
				node.hasAttribute( "name" ) )
			return node.getAttribute( "name" );

		if ( node.matchContent( "with-param" ) && 
				node.hasAttribute( "name" ) )
			return node.getAttribute( "name" );

		for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
			String att = node.getViewAttributeAt( i );
			String val = node.getAttribute( att );
			int j = val.indexOf( "$" );
			if ( j > -1 ) {
				StringBuffer sb = new StringBuffer();
				for ( int k = j + 1; k < val.length(); k++ ) {
					if ( Character.isLetterOrDigit( val.charAt( k ) )
							|| val.charAt( k ) == '_' )
						sb.append( val.charAt( k ) );
					else
						break;
				}
				if ( sb.length() > 0 ) {
					return sb.toString();
				}
			}
		}
		return null;
	}

	public String getTitle( FPNode node ) {
		String name = extractVariableName( node );
		if ( name != null ) {			
			return "The variable or parameter '" + name + "'";
		}
		return null;
	}

	public boolean process( FPNode node ) {
		return ( oldValue = extractVariableName( node ) ) != null;
	}

	/////////////////////////////////////////////////////////////

	public static final String NS = "http://www.w3.org/1999/XSL/Transform";

	public void startElement(
			String uri,
			String localName, 
			String qName, 
			Attributes atts ) throws SAXException {
		
		boolean found = false;
		
		if ( NS.equals( uri ) && 
				( "variable".equals( localName ) || 
						( "param".equals( localName ) ) ||
							( "with-param".equals( localName ) ) ) ) {
			if ( oldValue.equals( 
					atts.getValue( "name" ) ) ) {

				atts = new AttributesProxy( atts, true );
				found = true;
				
			}
		}

		if ( !found ) {
			
			// Search in the atts value
			String waited = "$" + oldValue;
			for ( int i = 0; i < atts.getLength(); i++ ) {

				if ( atts.getValue( i ).contains( waited ) ) {

					atts = new AttributesProxy( atts, false );
					
				}

			}

		}

		super.startElement(
				uri, 
				localName, 
				qName, 
				atts
		);
	}

	// -----------------------------------------------------------
	
	class AttributesProxy implements Attributes {

		private Attributes ref;
		private boolean variableOrParam = false;
		
		AttributesProxy( Attributes ref, boolean variable ) {
			this.ref = ref;
			this.variableOrParam = variable;
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

		private String processValue( String value ) {
			if ( variableOrParam && 
					value.equals( oldValue ) )
				return newValue;

			if ( !variableOrParam ) {
				value = value.replaceAll( 
						"\\$" + oldValue, 
							"\\$" + newValue );
			}

			return value;
		}

		public String getValue(int index) {
			return processValue( ref.getValue( index ) );
		}

		public String getValue(String uri, String localName) {
			return processValue( ref.getValue( uri, localName ) );
		}

		public String getValue(String qName) {
			return processValue( ref.getValue( qName ) );
		}
		
	}

}
