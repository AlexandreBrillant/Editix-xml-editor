package com.japisoft.xflows.task.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.SAXParser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.japisoft.framework.xml.XMLParser;
import com.japisoft.framework.xml.action.CustomEntityResolver;
import com.japisoft.xflows.XFlowsApplicationModel;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

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
public class FileParsingRunner implements TaskRunner, ErrorHandler {
	
	private TaskContext context;
	
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public boolean run( TaskContext context ) {

		File source = context.getCurrentSourceFile();
		SAXParser parser = null;
		
		try {
			
			boolean schemaFound = false;
			boolean validate = "true".equals( context.getParam(
					ParsingUI.VALIDATING ) );
			
			if ( validate ) {
			
				// Check for schema W3C
				BufferedReader br = new BufferedReader( new FileReader( source ) );
				try {
					String line = null;
					while ( ( line = br.readLine() ) != null ) {
						if ( line.indexOf( "http://www.w3.org/2001/XMLSchema-instance" ) > -1 ) {
							schemaFound = true;
							break;
						} else
						if ( line.indexOf( "<!DOCTYPE " ) > -1 ) {
							schemaFound = false;
							break;
						}
					}
				} finally {
					br.close();
				}

			}

			parser = XMLParser.getSaxParser( validate );		
		
			if ( schemaFound )
				parser.setProperty( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );

		} catch( Exception exc ) {
			context.addError( "FATAL PARSING ERROR CAN'T USE THE DEFAULT PARSER" );
			XFlowsApplicationModel.debug( exc );
			return ERROR;
		}

		try {
			InputSource is = new InputSource( source.toString() );
			XMLReader reader = parser.getXMLReader();
			reader.setEntityResolver( CustomEntityResolver.getInstance() );
			reader.setErrorHandler( this );
			this.context = context;			
			reader.parse(is);
		} catch (SAXException exc) {
			return ERROR;
		} catch (IOException exc) {
			context.addError(exc.getMessage());
			return ERROR;
		}

		return state;
	}

	private boolean state = OK;

	public void warning(SAXParseException exception)
			throws org.xml.sax.SAXException {
		context.addWarning(exception.getMessage());
	}

	public void error(SAXParseException exception)
			throws org.xml.sax.SAXException {
		state = ERROR;
		context.addError(exception.getMessage());
	}

	public void fatalError(SAXParseException exception)
			throws org.xml.sax.SAXException {
		state = ERROR;
		context.addError(exception.getMessage());
	}

}
