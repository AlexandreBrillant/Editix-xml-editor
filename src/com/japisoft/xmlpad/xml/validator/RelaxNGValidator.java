// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.xmlpad.xml.validator;

import com.japisoft.xmlpad.XMLContainer;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import com.sun.msv.verifier.ErrorInfo;
import com.sun.msv.verifier.ValidityViolation;
import org.iso_relax.verifier.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Validate the container content for RelaxNG. It requires an external library
 * MSV
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class RelaxNGValidator implements Validator {

	XMLContainer container;
	
	public int validate(XMLContainer container, boolean silentMode ) {
		this.container = container;
		try {
			return validateRelaxNG();
		} finally {
			container = null;
		}

	}
	
	private int validateRelaxNG() {

		try {
			
			VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();

			// compile a schema and gets a verifier.
			final Verifier verifier = factory.newVerifier( 
					new InputSource(
							container.getSchemaAccessibility().getRelaxNGValidationLocation().getReader() ) );

			// create a SAX parser			
			SAXParser parser = XMLPadSAXParserFactory.getNewSAXParser( false );
			XMLReader reader = parser.getXMLReader();

			Interceptor interceptor = new Interceptor();

			interceptor.setParent(reader);
			interceptor.setContentHandler(verifier.getVerifierHandler());

			// set an error handler that throws any error.
			verifier
					.setErrorHandler(com.sun.msv.verifier.util.ErrorHandlerImpl.theInstance);

			// parse the file.
			interceptor.parse( 
					new InputSource( 
							new StringReader( container	.getText() ) ) );

		} catch (Exception exc) {			
			if ( exc instanceof SAXParseException ) {
				SAXParseException spe = ( SAXParseException )exc;				
				container.getErrorManager().notifyError(
						null,
						true,
						spe.getSystemId(),
						spe.getLineNumber(),
						spe.getColumnNumber(),
						0,
						spe.getMessage(),
						false );
				error = true;
			} else
			if ( exc instanceof ValidityViolation ) {
				ValidityViolation vv = ( ValidityViolation )exc;
				container.getErrorManager().notifyError(
						null,
						true,
						vv.getSystemId(),
						vv.getLineNumber(),
						vv.getColumnNumber(),
						0,
						vv.getMessage(),
						false );
				
				error = true;
			} else
			if ( exc instanceof VerifierConfigurationException ) {
				VerifierConfigurationException ve = ( VerifierConfigurationException )exc;
				container.getErrorManager().notifyError(
						null,
						true,
						null,
						0,
						0,
						0,
						ve.getMessage(),
						false );
								
				error = true;
			}
		}

		return ( error ? Validator.ERROR : Validator.OK );
	}

	public boolean error = false;
	
	private class Interceptor extends XMLFilterImpl {

		private StringBuffer buffer = new StringBuffer();
		private Locator loc = null;

		public void setDocumentLocator(Locator l) {
			this.loc = l;
			super.setDocumentLocator( l );
		}

		public void startElement(String ns, String local, String qname,
				Attributes atts) throws SAXException {

			ErrorInfo ei = null;
			
			try {
				super.startElement(ns, local, qname, atts);
				// there is no error.
			} catch (ValidityViolation vv) {
				// there was an error.
				ei = vv.getErrorInfo();
				if ( !error ) {
					container.getErrorManager().notifyError(
							null,
							true,
							vv.getSystemId(),
							vv.getLineNumber(),
							vv.getColumnNumber(),
							0,
							vv.getMessage(),
							false );					
				}
				//	container.getEditor().notifyError( vv.getSystemId(), ( loc != null ) ? loc.getLineNumber() : 0, vv.getMessage() );
				error = true;
			}
		}

		public void endElement(String ns, String local, String qname)
				throws SAXException {

			ErrorInfo ei = null;

			try {
				super.endElement(ns, local, qname);
				// there is no error.
			} catch (ValidityViolation vv) {
				// there was an error.
				ei = vv.getErrorInfo();
				
				if ( !error ) {
					container.getErrorManager().notifyError(
							null,
							true,
							vv.getSystemId(),
							vv.getLineNumber(),
							vv.getColumnNumber(),
							0,
							vv.getMessage(),
							false );					
				}
				
				error = true;
			}
		}

	}
}