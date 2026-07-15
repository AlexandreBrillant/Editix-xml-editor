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

package com.japisoft.xmlpad.action.file;

import javax.swing.*;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.toolkit.XMLToolkit;

import java.io.*;

public class SaveAction extends XMLAction {

	public static final String ID = SaveAction.class.getName();
	
	public static String ENCODING_PROPERTY = "encoding";
	
	public SaveAction() {
		super();
	}

	String currentEncoding = null;

	public void setProperty(String propertyName, Object value) {
		if ( ENCODING_PROPERTY.equals( propertyName ) ) {
			currentEncoding = value.toString();
		}
	}

	/** Save the current document at the location */
	protected boolean saveDocument(String location) {
		if (container.getDocumentIntegrity().isParseBeforeSaving()) {
			// Error detected
			if (!ActionModel.activeActionByName(ActionModel.PARSE_ACTION))
				return false;
		}

		String txt = editor.getText();
		
		try {
			Reader r = null;
			r = new StringReader( txt );

			BufferedReader br = new BufferedReader( r );
			try {
				Writer fw = null;
				if ( "DEFAULT".equals( currentEncoding ) ) 
					fw = new FileWriter( location );
				else {
					if ( "AUTOMATIC".equals( currentEncoding ) || 
							currentEncoding == null ) {						
						currentEncoding = XMLToolkit.getXMLEncoding( txt, currentEncoding );
						if ( currentEncoding == null || "AUTOMATIC".equals( currentEncoding ) )
							currentEncoding = "UTF-8";
						container.getDocumentInfo().setEncoding( currentEncoding );
					} else
						currentEncoding = container.getDocumentInfo().getEncoding();
					
					// Search for processing instruction for skiping XML encoding
					int i = txt.indexOf( "<?encoding " );
					if ( i > 0 ) {
						int j = txt.indexOf( "?>", i );
						if ( j > 0 ) {
							currentEncoding = txt.substring( i + 11, j ).trim();
						}
					}
					
					if ( currentEncoding != null ) {
						try {
							fw = new OutputStreamWriter(
									new FileOutputStream( location ), currentEncoding );
						} catch( UnsupportedEncodingException e ) {
							fw = new FileWriter( location );
						}
					} else
						fw = new FileWriter( location );
				}

				Debug.debug( "Write with " + currentEncoding );

				BufferedWriter writer =
					new BufferedWriter( fw );
				try {
					for (String line = null; ( line = br.readLine() ) != null; ) {
						writer.write( line );
						writer.newLine();
					}
				} finally {
					writer.close();
				}
			} finally {
				br.close();
			}
			
			container.getDocumentInfo().setCurrentDocumentModifiedDate(
					new File( location ).lastModified() );			
			container.setModifiedState( false );
			
		} catch (Throwable th) {
			JOptionPane.showMessageDialog(
				editor,
				"Can't save to " + container.getCurrentDocumentLocation());
			return INVALID_ACTION;
		}
		return VALID_ACTION;
	}

	public boolean notifyAction() {
		if (container.getCurrentDocumentLocation() == null) {
			com.japisoft.framework.descriptor.ActionModel.activeActionById( "saveAs", null );
			return false;
		} else {
			return saveDocument(container.getCurrentDocumentLocation());
		}
	}
}
