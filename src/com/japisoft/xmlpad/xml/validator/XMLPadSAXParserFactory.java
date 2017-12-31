package com.japisoft.xmlpad.xml.validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
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
public class XMLPadSAXParserFactory {

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	static XMLPadSAXParserFactory DELEGATE = new XMLPadSAXParserFactory();

	/** This is a way to override this factory by creating a subclass */
	public static void setDelegate( XMLPadSAXParserFactory factory ) {
		DELEGATE = factory;
	}

	/** @return a new SAX Parser. It uses JAXP as default */
	public static SAXParser getNewSAXParser( boolean validating )
			throws ParserConfigurationException, SAXException {
		//System.out.println( DELEGATE );
		return DELEGATE.getNonStaticNewSAXParser( validating );
	}

	/** @return a new DocumentBuilder. This is used for formatting the document */
	public static DocumentBuilder getNewDocumentBuilder( boolean validating, boolean schemaAware )
			throws ParserConfigurationException {
		return DELEGATE.getNonStaticNewDocumentBuilder( validating, schemaAware );
	}
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	protected DocumentBuilder getNonStaticNewDocumentBuilder( boolean validating, boolean schemaAware ) 
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = getDocumentBuilderFactory(); 
		factory.setValidating( validating );
		factory.setNamespaceAware( true );
		factory.setIgnoringElementContentWhitespace( true );
		if ( schemaAware ) {
			factory.setAttribute(
				JAXP_SCHEMA_LANGUAGE,
				W3C_XML_SCHEMA 
			);
		}
		return factory.newDocumentBuilder();
	}

	protected DocumentBuilderFactory getDocumentBuilderFactory() {
		return DocumentBuilderFactory.newInstance();
	}

	protected SAXParser getNonStaticNewSAXParser( boolean validating )
			throws ParserConfigurationException, SAXException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating( validating );
		parserFactory.setNamespaceAware(true);
		
		if ( "true".equals( System.getProperty( "xmlpad.debug" ) ) )
			System.out.println( "Parsing with " + parserFactory );
		
		return parserFactory.newSAXParser();
	}
}
