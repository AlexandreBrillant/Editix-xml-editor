package com.japisoft.framework.xml.refactor2;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import com.japisoft.framework.xml.parser.node.FPNode;

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
public class AbstractRefactor implements LexicalHandler, ContentHandler {
	private ContentHandler handler;
	protected String oldValue;
	protected String newValue;

	public void setOldValue( String oldValue ) {
		this.oldValue = oldValue;
	}
	
	public void setNewValue( String newValue ) {
		this.newValue = newValue;
	}
	
	public String getOldValue() { return oldValue; }
	public String getNewValue() { return newValue; }

	public String getErrorMessage() { return null; }
	
	public void setContentHandler( ContentHandler handler ) {
		this.handler = handler;
	}

	public boolean process( FPNode node ) {
		return false;
	}
	
	public String getTitle( FPNode node ) {
		return null;
	}
	
	public boolean requireNewValue() { 
		return true;
	}

	/////////////////////////////////////////////////////////////////
	
	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		( ( LexicalHandler )handler ).startDTD(
				name,
				publicId,
				systemId );
	}

	public void endDTD() throws SAXException {
		( ( LexicalHandler )handler ).endDTD();
	}

	public void startEntity(String name) throws SAXException {
		( ( LexicalHandler )handler ).startEntity( name );
	}

	public void endEntity(String name) throws SAXException {
		( ( LexicalHandler )handler ).endEntity( name );	
	}

	public void startCDATA() throws SAXException {
		( ( LexicalHandler )handler ).startCDATA();	
	}

	public void endCDATA() throws SAXException {
		( ( LexicalHandler )handler ).endCDATA();			
	}

	public void comment(char[] ch, int start, int length) throws SAXException {
		( ( LexicalHandler )handler ).comment(
				ch, start, length );
	}

	public void setDocumentLocator(Locator locator) {
		handler.setDocumentLocator( locator );
	}

	public void startDocument() throws SAXException {
		handler.startDocument();
	}

	public void endDocument() throws SAXException {
		handler.endDocument();
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		handler.startPrefixMapping( prefix, uri );
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		handler.endPrefixMapping( prefix );
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		handler.startElement( uri, localName, qName, atts );
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		handler.endElement( uri, localName, qName );
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		handler.characters( ch, start, length );
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		handler.ignorableWhitespace( ch, start, length );
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		handler.processingInstruction( target, data );
	}

	public void skippedEntity(String name) throws SAXException {
		handler.skippedEntity( name );
	}

}
