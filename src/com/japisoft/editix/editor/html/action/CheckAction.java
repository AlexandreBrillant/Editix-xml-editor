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

package com.japisoft.editix.editor.html.action;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.AbstractAction;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.w3c.tidy.Tidy;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.japisoft.editix.editor.html.HTMLContainer;
import com.japisoft.editix.editor.html.HTMLParser;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.framework.xml.parser.ParseException;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

public class CheckAction extends AbstractAction implements ErrorHandler, ErrorParsingListener {

	private boolean errorFound = false;

	public void actionPerformed(ActionEvent e) {
		IXMLPanel panelHTML = EditixFrame.THIS.getSelectedPanel();
		if ( panelHTML == null )
			return;
		panelHTML = panelHTML.getPanelParent();
		if ( !( panelHTML instanceof HTMLContainer ) )
			return;
		HTMLContainer htmlContainer = ( HTMLContainer )panelHTML;
		XMLContainer panel = getCurrentContainer();
		if ( panel == null )
			return;		
		
		panel.getErrorManager().notifyNoError( false );
		panel.getErrorManager().initErrorProcessing();
		if ( htmlContainer.isHTML5() ) {
			checkHTML5( panel );
		} else {
			checkHTML( panel );
		}
		if ( !panel.getErrorManager().hasLastError() ) {
			EditixFactory.buildAndShowInformationDialog( "Your document is correct" );
		} else {
			EditixFactory.buildAndShowErrorDialog( "Error(s) found" );
		}
		panel.getErrorManager().stopErrorProcessing();
	}

/*
line 7 column 9 - Error: <tistle> is not recognized!
line 7 column 9 - Warning: discarding unexpected <tistle>
line 7 column 17 - Warning: inserting missing 'title' element
line 7 column 27 - Warning: discarding unexpected </tistle>
line 8 column 5 - Warning: </head> isn't allowed in <body> elements
line 9 column 6 - Warning: <body> isn't allowed in <body> elements
This document has errors that must be fixed before
using HTML Tidy to generate a tidied up version. 	
 */
	private void checkHTML( XMLContainer panel ) {
		Tidy tidy = new Tidy();
		tidy.setXmlOut(true);
		tidy.setNumEntities(true);
		tidy.setQuoteNbsp(true);
		tidy.setQuiet(true);
		StringWriter errRes = new StringWriter();
		tidy.setErrout( new PrintWriter( errRes ) );
		tidy.parse( 
				new ByteArrayInputStream( 
					panel.getText().getBytes() ),
				new ByteArrayOutputStream() );
		String[] errors = errRes.toString().split( "\n" );
		for ( String error : errors ) {
			if ( error.contains( "Error" ) ) {
				error = error.substring( "line ".length() );
				int i = error.indexOf( " " );
				String line = error.substring( 0, i );
				i = error.lastIndexOf( ":" );
				String msg = error;
				if ( i > -1 ) {
					msg = error.substring( i + 1 );
				}
				panel.getErrorManager().notifyError( 
					this,
					true,
					null,
					Integer.parseInt(line), 
					-1, 
					-1, 
					msg, 
					false 
				);
			}
		}
	}

	private void checkHTML5( XMLContainer panel ) { 
		HTMLParser p = new HTMLParser();
		p.setErrorSignal( this );
		try {
			p.parse(new StringReader(panel.getText()), panel);
		} catch( ParseException pe ) {		
		}
	}
	
	public void parsingError(String message, int offset, int line, int column) {
		getCurrentContainer().getErrorManager().notifyError(this,true,null,line,-1,-1, message, false );
		errorFound = true;		
	}

	public XMLContainer getCurrentContainer() {
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		if ( panel == null )
			return null;
		return panel.getMainContainer();
	}

	public void error(SAXParseException exception) throws SAXException {
		getCurrentContainer().getErrorManager().notifyError(this,true,null,exception.getLineNumber(),-1,-1, exception.getMessage(), false );
		errorFound = true;
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		getCurrentContainer().getErrorManager().notifyError(this,true,null,exception.getLineNumber(),-1,-1, exception.getMessage(), false );
		errorFound = true;
	}

	public void warning(SAXParseException exception) throws SAXException {
	}

}

