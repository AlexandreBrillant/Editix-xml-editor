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

package com.japisoft.editix.editor.jsx.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.japisoft.editix.editor.jsx.domapi.Document;
import com.japisoft.editix.javascript.JavaScriptFactory;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;

import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.error.ErrorManager;

public class RunAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		
		ErrorManager rm = panel.getMainContainer().getErrorManager();
		rm.notifyNoError(false);
		rm.initErrorProcessing();
		
		String xmlDataSource = (String)panel.getProperty( "jsx.data.file" );

		if ( xmlDataSource == null ) {
			EditixFactory.buildAndShowErrorDialog( "No data file ?" );
			return;
		}
		
		File f = new File( xmlDataSource );
		if ( !f.exists() ) {
			EditixFactory.buildAndShowErrorDialog( "Can't find " + xmlDataSource + " ?" );
			return;
		}
		
		ScriptEngine engine = JavaScriptFactory.newFactory().engine();
		
		Document doc = null;
		
		try {
			Bindings b = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
			
			try {

				doc = NodeFactory.newInstance().getDocument( xmlDataSource );					
				b.put( "document", doc );
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't parse this data file ?" );
				return;
			}
			
        	engine.eval( panel.getMainContainer().getText() );
        	
        	doc.save();
        	
        	// Reload it
			panel.setProperty( "jsx.data.file", xmlDataSource );
        	
		} catch( ScriptException se ) {
			int line = se.getLineNumber();
			String message = se.getMessage();
			int i = message.lastIndexOf( ":" );
			if ( i > -1 )
				message = message.substring( i + 1 );
			rm.notifyError( this, true, null, line, se.getColumnNumber(), -1, message, false );
			EditixFactory.buildAndShowErrorDialog( "Error(s) found" );
		}
		
		rm.stopErrorProcessing();

	}
	
}
