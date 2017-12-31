package com.japisoft.editix.document.schema;

import java.io.IOException;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.error.ErrorManager;
import com.japisoft.xmlpad.xml.validator.Validator;

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
		// Save it
		ActionModel.activeActionById("save", null);

		currentErrorManager = container.getErrorManager();
		currentErrorManager.initErrorProcessing();

		try {

			XMLSchemaLoader schemaLoader = new XMLSchemaLoader();
			schemaLoader.setErrorHandler(this);
//			SharedProperties.DEFAULT_ENTITY_RESOLVER );
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
