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

package com.japisoft.framework.xml.refactor2.elements.xslt;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;

public class TemplateModeRefactor extends AbstractRefactor {

	private String extractTemplateName( FPNode node ) {

		if ( node.matchContent( "template" ) && 
				node.hasAttribute( "mode" ) )
			return node.getAttribute( "mode" );

		if ( node.matchContent( "apply-templates" ) && 
				node.hasAttribute( "mode" ) )
			return node.getAttribute( "mode" );

		return null;
	}	

	public boolean process(FPNode node) {

		if ( node.matchContent( "template" )
				&& node.hasAttribute( "mode" ) ) {
			oldValue = node.getAttribute( "mode" );
			return true;
		}

		if ( node.matchContent( "apply-templates" )
				&& node.hasAttribute( "mode" ) ) {
			oldValue = node.getAttribute( "mode" );
			return true;
		}

		return super.process( node );

	}

	public String getTitle(FPNode node) {
		return "The template mode '" + extractTemplateName( node ) + "'";
	}
	
	public boolean requireNewValue() {
		return true;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

		if ( VariableParamRefactor.NS.equals( uri ) && 
				( "template".equals( localName ) || 
						( "apply-templates".equals( localName ) ) ) ) {
			if ( oldValue.equals( 
					atts.getValue( "mode" ) ) ) {

				atts = new AttributesProxy( atts );				
			}
		}

		super.startElement(uri, localName, qName, atts);
	}

	// -----------------------------------------------------------

	class AttributesProxy implements Attributes {

		private Attributes ref;
		
		AttributesProxy( Attributes ref ) {
			this.ref = ref;
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
			if ( "mode".equals( name ) && 
					value.equals( oldValue ) )
				return newValue;
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

