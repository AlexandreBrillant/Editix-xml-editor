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

package com.japisoft.editix.document.schema;

import java.io.IOException;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.error.ErrorManager;
import com.japisoft.xmlpad.xml.validator.Validator;

public class SchemaValidator implements Validator, XMLErrorHandler {

	public void error(String arg0, String arg1, XMLParseException arg2)
			throws org.apache.xerces.xni.XNIException {
		currentErrorManager.notifyError(this, true, arg2.getBaseSystemId(),
				arg2.getLineNumber(), arg2.getColumnNumber(), -1, arg2
						.getMessage(), false);
	}

	public void fatalError(String arg0, String arg1, XMLParseException arg2)
			throws org.apache.xerces.xni.XNIException {
		currentErrorManager.notifyError(this, true, arg2.getBaseSystemId(),
				arg2.getLineNumber(), arg2.getColumnNumber(), -1, arg2
						.getMessage(), false);
	}

	public void warning(String arg0, String arg1, XMLParseException arg2)
			throws org.apache.xerces.xni.XNIException {
	}

	ErrorManager currentErrorManager = null;

	public int validate(XMLContainer container, boolean silentMode ) {

		if (container.getCurrentDocumentLocation() == null) {
			if ( !silentMode )
				EditixFactory
					.buildAndShowInformationDialog("Please save your document before checking");
			return WARNING;
		}
		
		if ( Preferences.getPreference( "xsdEditor", "saveBeforeChecking", true ) ) {
			// Save it
			ActionModel.activeActionById("save", null);
		}

		currentErrorManager = container.getErrorManager();
		currentErrorManager.initErrorProcessing();

		try {

			XMLSchemaLoader schemaLoader = new XMLSchemaLoader();
			schemaLoader.setErrorHandler(this);
			try {
				schemaLoader.loadGrammar(new XMLInputSource(null, container
						.getCurrentDocumentLocation(), null));
			} catch (org.apache.xerces.xni.XNIException e) {

				if (e.getCause() instanceof XMLParseException) {
					XMLParseException p = (XMLParseException) e.getCause();
					currentErrorManager.notifyError(this, true, p
							.getLiteralSystemId(), p.getLineNumber(), p
							.getColumnNumber(), -1, p.getMessage(), false);
				}

			} catch (IOException e) {
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

}
