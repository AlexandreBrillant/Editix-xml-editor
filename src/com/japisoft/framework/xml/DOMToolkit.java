package com.japisoft.framework.xml;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.japisoft.framework.xml.format.Formatter;

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
public class DOMToolkit {

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	public static Element getFirstElement( 
			Element parent, 
			String matching ) {
		NodeList nl = parent.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				if ( matching.equals( ( ( Element )nl.item( i ) ).getLocalName() ) ) {
					return ( Element )nl.item( i );
				}
			}
		}
		return null;
	}

	public static Document parse(
			String content, 
			String location ) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource( new StringReader( content ) );
		if ( location != null ) {
			is.setSystemId( location );
		}
		return db.parse( is );
	}

	public static String DOM2String( 
			Node n, 
			int indentSize ) throws Exception {
		return Formatter.format( n );
	}
	
}
