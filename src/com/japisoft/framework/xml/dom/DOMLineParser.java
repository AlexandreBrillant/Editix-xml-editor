package com.japisoft.framework.xml.dom;

import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
public class DOMLineParser {

	public final static String START_LINE_NUMBER_KEY_NAME = "startLineNumber";
	public final static String STOP_LINE_NUMBER_KEY_NAME = "stopLineNumber";

	static SAXParserFactory saxFactory = null;
	static DocumentBuilderFactory builderFactory = null;
	
	private DOMLineParser() {
		saxFactory = SAXParserFactory.newInstance();
		builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setIgnoringElementContentWhitespace( true );
	}
	
	private static DOMLineParser parser = null;
	
	public static DOMLineParser getInstance() {
		if ( parser == null )
			parser = new DOMLineParser();
		return parser;
	}

	public Document parseContent( String content ) throws Exception {
		SAXParser parser = saxFactory.newSAXParser();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.newDocument();
		
		LineDefaultHandler ldf = new LineDefaultHandler( doc );
		parser.parse( new InputSource( new StringReader( content ) ), ldf );
		ldf.doc.normalize();
		ldf.doc.normalizeDocument();
		return ldf.doc;
	}

	class LineDefaultHandler extends DefaultHandler {
		Locator locator = null;
		Document doc = null;
		Stack<Element> elementStack = new Stack<Element>();
		StringBuilder textBuffer = new StringBuilder();

		public LineDefaultHandler( Document doc ) {
			this.doc = doc;
		}
		
		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}
		
		@Override
		public void startElement(
		   final String uri, 
		   final String localName, 
		   final String qName, 
		   final Attributes attributes)
		           throws SAXException {
		       addTextIfNeeded();
		       final Element el = doc.createElement(qName);
		       for (int i = 0; i < attributes.getLength(); i++) {
		           el.setAttribute(attributes.getQName(i), attributes.getValue(i));
		       }
		       el.setUserData( START_LINE_NUMBER_KEY_NAME, this.locator.getLineNumber(), null );
		       elementStack.push(el);
		}

		@Override
		public void endElement(final String uri, final String localName, final String qName) {
			addTextIfNeeded();
			final Element closedEl = elementStack.pop();
       
			closedEl.setUserData( STOP_LINE_NUMBER_KEY_NAME, this.locator.getLineNumber(), null );           
			if ( elementStack.isEmpty() ) { // Is this the root element?
				doc.appendChild(closedEl);
			} else {
				final Element parentEl = elementStack.peek();
				parentEl.appendChild(closedEl);
			}
		}

		@Override
		public void characters( 
				final char ch[], 
				final int start, 
				final int length ) throws SAXException {
			boolean ok = false;
			for ( int i = start; i < start + length; i++ ) {
				if ( !Character.isWhitespace( ch[ i ] ) ) {
					ok = true;
					break;
				}
			}
			if ( ok )
				textBuffer.append(ch, start, length);
		}

		// Outputs text accumulated under the current node
		private void addTextIfNeeded() {
			if (textBuffer.length() > 0) {
	           final Element el = elementStack.peek();
	           final Node textNode = doc.createTextNode(textBuffer.toString());
	           el.appendChild(textNode);
	           textBuffer.delete(0, textBuffer.length());
			}
		}
		
	}
	
}
