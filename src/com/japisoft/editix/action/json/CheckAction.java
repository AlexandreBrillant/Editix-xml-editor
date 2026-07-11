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

package com.japisoft.editix.action.json;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import org.everit.json.schema.JSONPointer;
import org.everit.json.schema.Schema;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.Validator;
import org.everit.json.schema.event.CombinedSchemaMatchEvent;
import org.everit.json.schema.event.CombinedSchemaMismatchEvent;
import org.everit.json.schema.event.ConditionalSchemaMatchEvent;
import org.everit.json.schema.event.ConditionalSchemaMismatchEvent;
import org.everit.json.schema.event.SchemaReferencedEvent;
import org.everit.json.schema.event.ValidationListener;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.japisoft.editix.editor.json.JSONContainer;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.error.ErrorManager;

public class CheckAction extends AbstractAction {
	
	private int extractLine( String m, JSONObject root ) {
		int i = m.indexOf( ':' );
		int line = -1;
		if ( i > -1 ) {
			try {
				String pointer = m.substring( 0, i );
				JSONPointer jp = new JSONPointer( pointer );
				JSONObject obj = ( JSONObject )jp.queryFrom( root );
				if ( obj != null ) {
					line = obj.getLine();
				}
			} catch( Throwable th ) {
				th.printStackTrace();
			}
		}
		return line;
	}
	
	public void actionPerformed(ActionEvent e) {
		JSONContainer jsc = ( JSONContainer )EditixFrame.THIS.getSelectedContainer();
		String type = jsc.getDocumentInfo().getType();
		
		String text = jsc.getText();
		ErrorManager rm = jsc.getErrorManager();
		rm.notifyNoError(false);
		rm.initErrorProcessing();
		
		JSONObject root = null;
		
		try {
			
			if ( text.trim().startsWith( "[" ) ) {
				new JSONArray( text );
			} else
				root = new JSONObject( text );

			boolean withErrors = false;
			
			String schemaPath = ( String )jsc.getProperty( "json-schema" );
			if ( schemaPath != null && root != null ) {				
				try {
					JSONObject rootSchema = new JSONObject( new JSONTokener( new FileInputStream( schemaPath ) ) );
					
					try {
						Schema schema = SchemaLoader.load( rootSchema );
						schema.validate( root );
					} catch( SchemaException exc ) {
						EditixFactory.buildAndShowErrorDialog( "Invalid JSON schema :" + exc.getMessage() );
						withErrors = true;
					}
					
				} catch( IOException exc ) {
					EditixFactory.buildAndShowWarningDialog( "Can't load the JSON Schema " + schemaPath );
				} catch( ValidationException ve ) {
					List<String> messages = ve.getAllMessages();
					for( String m : messages ) {						
						rm.notifyError( m, extractLine( m, root ), false );
					}
					withErrors = true;
				}
			}
			
			if ( "JSONC".equals( type ) ) {
				// Parse the JSON Schema
				try {
					Schema schema = SchemaLoader.load( root );
				} catch( SchemaException exc ) {
					String message = exc.getMessage();
					rm.notifyError( message, extractLine( message, root ), false );
					withErrors = true;
				}
			}
			
			if ( !withErrors )
				EditixFactory.buildAndShowInformationDialog( "Your document is correct" );

		} catch( JSONException exc ) {		
			rm.notifyError( exc.getMessage(), exc.line, false );
			EditixFactory.buildAndShowErrorDialog( "Error(s) found" );
		}

		rm.stopErrorProcessing();
	}
	
}
