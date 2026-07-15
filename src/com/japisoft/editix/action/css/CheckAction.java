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

package com.japisoft.editix.action.css;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.AbstractAction;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

import com.japisoft.editix.editor.css.CSSEditor;
import com.japisoft.editix.editor.css.helper.Keywords;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.xml.CheckableAction;
import com.steadystate.css.parser.SACParserCSS21;
import com.steadystate.css.sac.DocumentHandlerExt;

public class CheckAction extends AbstractAction 
		implements ErrorHandler, DocumentHandlerExt, CheckableAction {

	private XMLContainer editor = null;
	private SACParserCSS21 parser = null;
	
	public void charset(String characterEncoding) throws CSSException {
	}

	public void comment(String arg0) throws CSSException {
	}

	public void endDocument(InputSource arg0) throws CSSException {
	}

	public void endFontFace() throws CSSException {
	}

	public void endMedia(SACMediaList arg0) throws CSSException {
	}

	public void endPage(String arg0, String arg1) throws CSSException {
	}

	public void endSelector(SelectorList arg0) throws CSSException {
	}

	public void ignorableAtRule(String arg0) throws CSSException {
	}

	public void importStyle(String arg0, SACMediaList arg1, String arg2)
			throws CSSException {
	}

	public void namespaceDeclaration(String arg0, String arg1)
			throws CSSException {
	}

	public void property(String arg0, LexicalUnit arg1, boolean arg2)
			throws CSSException {
		if ( !Keywords.isAProperty( arg0 ) ) {
			error( new CSSParseException("Unknown property " + arg0, parser.getLocator() ) );
		}
	}

	public void startDocument(InputSource arg0) throws CSSException {
	}

	public void startFontFace() throws CSSException {
	}

	public void startMedia(SACMediaList arg0) throws CSSException {
	}

	public void startPage(String arg0, String arg1) throws CSSException {
	}

	public void startSelector(SelectorList arg0) throws CSSException {
	}

	// ---------------------------------------------------------------
	
	public boolean checkDocument(XMLContainer editor, boolean silentMode ) {
		
		this.editor = editor;
		parser = new SACParserCSS21();
		parser.setDocumentHandler( this );
		parser.setErrorHandler( this );

		try {
			editor.getErrorManager().initErrorProcessing();
			// parser.setDocumentHandler( this );
			parser.parseStyleSheet( 
					new InputSource( 
							new StringReader( 
									editor.getText() ) ) );
			
			if ( !editor.getErrorManager().hasLastError() ) {
				if ( !silentMode )
					EditixFactory.buildAndShowInformationDialog( "Your CSS is correct" );
				editor.getErrorManager().notifyNoError(false);
			} else {
				EditixFactory.buildAndShowErrorDialog( "Error(s) found" );
			}

			editor.getErrorManager().stopErrorProcessing();
			
		} catch (IOException e1) {
		}

		boolean hasError = 
			editor.getErrorManager().hasLastError();
		
		parser = null;
		editor = null;
		
		return !hasError;
	}

	public void actionPerformed(ActionEvent e) {

		editor = ( CSSEditor )EditixFrame.THIS.getSelectedContainer();
		if ( editor == null ) {
			EditixFactory.buildAndShowErrorDialog( "Can't Check your CSS" );
			return;
		}
		
		checkDocument( editor, false );
			
	}

	public void error(CSSParseException e) throws CSSException {
		editor.getErrorManager().notifyError(
				this,
				true,
				null,
				e.getLineNumber(),
				e.getColumnNumber(),
				-1,
				e.getMessage(),
				false );
	}

	public void fatalError(CSSParseException e) throws CSSException {
		editor.getErrorManager().notifyError(
				this,
				true,
				null,
				e.getLineNumber(),
				e.getColumnNumber(),
				-1,
				e.getMessage(),
				false );
	}

	public void warning(CSSParseException e) throws CSSException {
	}

}
