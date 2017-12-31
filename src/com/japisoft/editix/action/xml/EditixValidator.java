package com.japisoft.editix.action.xml;

import java.io.File;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.japisoft.editix.action.dtdschema.AssignSchematron;
import com.japisoft.editix.action.dtdschema.SchematronAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.RelaxNGValidator;

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
public class EditixValidator extends DefaultValidator {

	protected boolean canValidate(
			XMLContainer container ) {
		String type = "xml";
		if ( container.getDocumentInfo() != null )
			type = container.getDocumentInfo().getType();
		FPNode root = container.getRootNode();
		if ( root != null ) {
			if ( "XSLT".equals( type ) ) {
				// Check for version = 1
				String version = 
					root.getAttribute( "version" );
				if ( "2.0".equals( version ) ) {
					EditixFactory.buildAndShowWarningDialog( "You are using an xslt 2.0 document, but your document type was for xslt 1.0, please reopen your document as an XSLT 2.0" );
				}
			} else
			if ( "XSLT2".equals( type ) ) {
				// Check for version = 2
				String version = 
					root.getAttribute( "version" );				
				if ( "1.0".equals( version ) ) {
					EditixFactory.buildAndShowWarningDialog( "You are using an xslt 1.0 document, but your document type was for xslt 2.0, please reopen your document as an XSLT 1.0" );
				}
			}
		}
		return super.canValidate( container );
	}	

	public int notifyAction(XMLContainer container, boolean silentMode ) {
		int res = 0;
		res = super.notifyAction( container, silentMode );

		if ( res == OK ) {
		
			// Check for schematron processing instruction
			
			XMLPadDocument doc = container.getXMLDocument();
			String txt = container.getText();
	
			int t = doc.nextTag( 0 );
			if ( t > -1 )
				txt = txt.substring( 0, t );
			
			int i = txt.indexOf( AssignSchematron.PI );
			if ( i > 0 ) {
				int j = txt.indexOf( "path=", i );
				if ( j > 0 ) {
					int k = txt.indexOf( "\"", j );
					if ( k > 0 ) {
						int l = txt.indexOf( "\"", k + 1 );
						if ( l > -1 ) {
							String path = txt.substring( k + 1, l );

							// Check relative schematron path
							if ( container.getCurrentDocumentLocation() != null ) {
								File tmp = new File( 
									new File( container.getCurrentDocumentLocation() ).getParentFile(),
									path
								);
								if ( tmp.exists() )
									path = tmp.toString();
							}
							
							res = SchematronAction.checkSchematron( container, new File( path ), false );
						}
					}
				}
			}
			
		}

		return res;
	}
	
	protected SAXParser buildCustomParser( XMLContainer container ) {
		// Requires jdk 5.x
		try {
			SAXParserFactory parserFactory;
			parserFactory = SAXParserFactory.newInstance();
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL u = container.getDocumentInfo().getSchemaXSDValid();
			Schema schema = schemaFactory.newSchema(u);
			parserFactory.setNamespaceAware(true);
			parserFactory.setSchema(schema);	
			return parserFactory.newSAXParser();
		} catch (Throwable e) {
			ApplicationModel.debug(	e );
		}	
		return null;
	}

	// Validate with RelaxNG
	protected boolean postAction( XMLContainer container, boolean silentMode ) {
		if ( container.getDocumentInfo().getSchemaRNGValid() != null ) {	
			URL loc = container.getDocumentInfo().getSchemaRNGValid();

			String ef = loc.toExternalForm();
			ef = ef.replace( "%20", " " );

			container.getSchemaAccessibility().setRelaxNGValidationLocation(
					ef );

			RelaxNGValidator val = new RelaxNGValidator();
			container.getErrorManager().initErrorProcessing();
			val.validate( container, silentMode );
			container.getErrorManager().stopErrorProcessing();
			return val.error;
			// Restore it
			//container.getSchemaAccessibility().setRelaxNGValidationLocation(
			//		(String)null );
		} else
			return false;
	}
}
