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

package com.japisoft.editix.document.xslt;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXParseException;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltCompiler;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.error.ErrorManager;
import com.japisoft.xmlpad.xml.validator.Validator;

public class XSLT2Validator implements Validator, ErrorListener {
	
	ErrorManager currentErrorManager = null;

	public int validate(XMLContainer container, boolean silentMode ) {
		
		if ( EditixFactory.mustSaveDialog( container ) ) {
			return WARNING;
		}

		currentErrorManager = container.getErrorManager();
		currentErrorManager.initErrorProcessing();

		try {
		
			// Save it
			ActionModel.activeActionById("save", null);
	
			Processor p = new Processor( false );
			XsltCompiler compiler = p.newXsltCompiler();
			compiler.setErrorListener( this );
			try {
				compiler.compile( new StreamSource( 
						container.getCurrentDocumentLocation() ) );
			} catch (SaxonApiException e) {
				return ERROR;
			}
		} finally {
			boolean error = currentErrorManager.hasLastError();
			currentErrorManager.stopErrorProcessing();
			currentErrorManager = null;
			if (error)
				return ERROR;
		}
		return OK;
	}

	public void error(TransformerException te) throws TransformerException {
		SourceLocator locator = te.getLocator();
		if ( locator == null )
			locator = new SourceLocator() {
			public int getColumnNumber() {
				return -1;
			}
			public int getLineNumber() {
					return -1;
			}
			public String getPublicId() {
				return null;
			}
			public String getSystemId() {
				return null;
			}
		};

		int lineError = locator.getLineNumber();
		int colError = locator.getColumnNumber();
		
		Throwable th  = te.getCause();
		int i = 0;
		while ( th != null ) {
			if ( th instanceof SAXParseException ) {
				SAXParseException spe = ( SAXParseException )th;
				colError = spe.getColumnNumber();
				lineError = spe.getLineNumber();
				break;
			}
			if ( th.getCause() == th )
				break;			
			th = th.getCause();
			i++;
			if ( i > 10 )	// Non loop add-on
				break;
		}

		currentErrorManager.notifyError(
				this, 
				true, 
				null,
				lineError, 
				colError, 
				-1, 
				te.getMessage(), 
				false );

	}

	public void fatalError(TransformerException te)
			throws TransformerException {
		error( te );
	}

	public void warning(TransformerException arg0) throws TransformerException {
		
	}
	
}
