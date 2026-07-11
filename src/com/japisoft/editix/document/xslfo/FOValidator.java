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

package com.japisoft.editix.document.xslfo;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.japisoft.editix.action.fop.EditixFOPFactory;
import com.japisoft.framework.xml.XSLTTransformer;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.xml.validator.Validator;

public class FOValidator implements Validator {

	public int validate(XMLContainer container, boolean silentMode) {
		
		container.getErrorManager().initErrorProcessing();
		try {
		
			try {
				FopFactory factory = EditixFOPFactory.newInstance( container.getCurrentDocumentLocationURL() );
				Fop fop = factory.newFop( MimeConstants.MIME_PLAIN_TEXT, new ByteArrayOutputStream() );
				TransformerFactory factory2 = XSLTTransformer.getTransformerFactory();
				Transformer transformer = factory2.newTransformer(); // identity transformer
				Result res = new SAXResult(fop.getDefaultHandler());
				transformer.transform( new StreamSource( new StringReader( container.getText() )), res);
			} catch( Exception exc ) {
				exc.printStackTrace();
				String message = exc.getMessage();
				
				if ( message != null && !"".equals( message ) ) {
					Pattern p = Pattern.compile( "position\\s(\\d+):(\\d+)" );
					Matcher m = p.matcher( message );			
					int row = -1;
					int col = -1;

					if ( m.find() ) {
						row = Integer.parseInt( m.group( 1 ) );
						col = Integer.parseInt( m.group( 2 ) );
					}
	
					container.getErrorManager().notifyError(
						this,
						false,
						container.getCurrentDocumentLocation(),
						row,
						col,
						-1,
						message,
						false );	
				}

				return ERROR;
			}
			return OK;
			
		} finally {
			container.getErrorManager().stopErrorProcessing();
		}
	}

}
