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

package com.japisoft.editix.script;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import com.japisoft.editix.editor.jsx.action.NodeFactory;
import com.japisoft.editix.editor.jsx.domapi.Document;
import com.japisoft.editix.javascript.JavaScriptFactory;
import com.japisoft.editix.plugin.EditiXManager;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFactory;

public class ScriptAction extends AbstractAction {

	private File path = null;
	
	public ScriptAction( Script s ) {
		putValue( Action.NAME, s.getName() );
		if ( !"".equals( s.getShortkey() ) );
		putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( s.getShortkey() ) );
		this.path = s.getPath();
	}

	private boolean jsxMode = false;
	
	public ScriptAction( File path, boolean jsxMode ) {
		this.path = path;
		this.jsxMode = jsxMode;
	}
	
	public ScriptAction( File path ) {
		this( path, false );
	}
	
	public boolean errorFound = false;
	
	public void actionPerformed(ActionEvent e) {
		errorFound = false;
		if ( !path.exists() ) {
			EditixFactory.buildAndShowErrorDialog( "Can't find this script" );
		} else {
	        ScriptEngine engine = JavaScriptFactory.newFactory().engine();

	        try {
	        	Bindings b = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
	        	b.put( "EditiXManager", EditiXManager.getInstance());
	        	b.put( "EditixManager", EditiXManager.getInstance());
	        	
	        	if( jsxMode ) {
	        		try {
	        			if ( EditiXManager.getInstance().getCurrentDocument() == null ) {
	        				EditixFactory.buildAndShowErrorDialog( "Need an XML document for running this script" );
	        				return;
	        			}
	        			String currentDocument = EditiXManager.getInstance().getCurrentDocument().getLocation();
	        			if ( currentDocument != null ) {
	        				Document doc = NodeFactory.newInstance().getDocument( currentDocument );
	        				b.put( "document", doc );
	        			} else
	        				EditixFactory.buildAndShowErrorDialog( "You must save your document before running the script" );
	    			} catch( Exception exc ) {
	    				EditixFactory.buildAndShowErrorDialog( "Can't parse this data file ?" );
	    				return;
	    			}
	        	}
	        	
	        	engine.eval(new java.io.FileReader( path ));
	        } catch( ScriptException exc ) {
	        	EditixFactory.buildAndShowErrorDialog( "Error(s) in your script : line " + exc.getLineNumber() );
	        	errorFound = true;
	        } catch( FileNotFoundException fne ) {
	        }
		}
	}

}

