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

package com.japisoft.xmlpad.xml.validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Here a factory for getting a parser for validating the edited document. The
 * default implementation is based on JAXP. You can create a subclass and
 * delegate the creation of a new parser. This is useful for very particular
 * cases like loading a remote parser or controlling some default parsing
 * properties...
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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
