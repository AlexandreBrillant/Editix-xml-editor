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

package com.japisoft.framework.xml.parser.tools;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Trace all content handler event
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class TraceContentHandler implements ContentHandler {
	Locator l;

	public void setDocumentLocator(Locator locator) {
		this.l = locator;
	}

	public void startDocument() throws SAXException {
		System.out.println("- start document");
	}

	public void endDocument() throws SAXException {
		System.out.println("- end document");
	}

	public void startPrefixMapping(String prefix, String uri)
		throws SAXException {
		System.out.println("- startPrefixMapping " + prefix + " / uri=" + uri);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		System.out.println("- endPrefixMapping " + prefix);
	}

	public void startElement(
		String uri,
		String localName,
		String qname,
		Attributes atts)
		throws SAXException {
		System.out.println(
			"* start tag uri="
				+ uri
				+ " localname="
				+ localName
				+ " qname="
				+ qname
				+ " "
				+ atts
				+ " at line:"
				+ l.getLineNumber()
				+ " col:"
				+ l.getColumnNumber());
	}

	public void endElement(String uri, String localName, String qname)
		throws SAXException {
		System.out.println(
			"* end tag uri="
				+ uri
				+ " localname="
				+ localName
				+ " qname="
				+ qname);
	}

	public void characters(char ch[], int start, int length)
		throws SAXException {
		System.out.println("+ text [" + new String(ch) + "]");
	}

	public void ignorableWhitespace(char ch[], int start, int length)
		throws SAXException {
	}

	public void processingInstruction(String target, String data)
		throws SAXException {
		System.out.println("! instruction " + target + " " + data);
	}

	public void skippedEntity(String name) throws SAXException {
		System.out.println( "Skipped entity :" + name );
	}

}

