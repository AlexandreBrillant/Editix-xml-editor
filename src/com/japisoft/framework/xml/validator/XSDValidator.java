package com.japisoft.framework.xml.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
public class XSDValidator implements ErrorHandler {

	private Validator validator = null;
	
	public XSDValidator( String schemaURI ) throws Exception {
		
		System.setProperty( 
				"javax.xml.validation.SchemaFactory:" + XMLConstants.W3C_XML_SCHEMA_NS_URI, 
				"org.apache.xerces.jaxp.validation.XMLSchemaFactory" );

		SchemaFactory schemaFactory = SchemaFactory.newInstance(
				XMLConstants.W3C_XML_SCHEMA_NS_URI );

		Schema schema = null;
		if ( schemaURI.indexOf( "://" ) > -1 )
			schema =
				schemaFactory.newSchema( 
					new URL( schemaURI ) );
		else
			schema = 
				schemaFactory.newSchema(
						new File( schemaURI )
				);

		validator = schema.newValidator();
		validator.setErrorHandler( this );
		
	}

	private boolean error = false;
	private String errorMessage = null;
	
	public boolean validate( Document source ) {
		error = false;
		errorMessage = null;
		try {
			listOfErrorNodes = null;
			validator.validate( new DOMSource( source ) );
		} catch (SAXException e) {
			errorMessage = e.getMessage();
			error = true;
		} catch (IOException e) {
			errorMessage = e.getMessage();
			error = true;
		}
		return !error;
	}

	public String getLastErrorMessage() { 
		if ( errorMessage == null )
			return "";
		return errorMessage; }
	
	private ArrayList<ErrorValidationNode> listOfErrorNodes = null;
	public List<ErrorValidationNode> getErrors() { return listOfErrorNodes; }

	public void error(SAXParseException exception) throws SAXException {
		processException( exception );
	}

	private void processException( SAXParseException exception ) throws SAXException {
		if ( listOfErrorNodes == null )
			listOfErrorNodes = 
				new ArrayList<ErrorValidationNode>();
		listOfErrorNodes.add(
				new ErrorValidationNode(
					exception.getMessage(),
					( Node )validator.getProperty(
						"http://apache.org/xml/properties/dom/current-element-node" ) ) );
		error = true;		
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		processException( exception );
		error = true;
	}

	public void warning(SAXParseException exception) throws SAXException {}
	
	public static void main( String[] args ) throws Exception {
		XSDValidator validator = new XSDValidator( "c:/editix/livre.xsd" );
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware( true );
		DocumentBuilder db = factory.newDocumentBuilder();
		Document doc = db.parse( new File( "C:/Documents and Settings/alex/toto.xml" ) );
		validator.validate( doc );
	}
}
