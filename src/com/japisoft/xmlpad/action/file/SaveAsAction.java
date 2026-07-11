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

import java.io.*;
import javax.swing.*;

import com.japisoft.xmlpad.UIFactory;

/** Action for saving the current content
 *  @version 1.1 */
public class SaveAsAction extends SaveAction {
	
	public static final String ID = SaveAsAction.class.getName();
	public static final String CURRENT_DIR_PROPERTY = "currentDir";
	
	public SaveAsAction() {
		super();
	}
	
	private String currentDir = null;
	
	public void setProperty(String propertyName, Object value) {
		if ( CURRENT_DIR_PROPERTY.equals( propertyName ) ) {
			currentDir = ( String )value;
		}
	}
	
	public Object getProperty(String propertyName, Object defaultValue) {
		if ( CURRENT_DIR_PROPERTY.equals( propertyName ) )
			return currentDir;
		return super.getProperty( propertyName, defaultValue );
	}

	private boolean enabledExt = true;

	/** Force the XML extension when choosing a file name */
	public void setForceExtension(boolean enable) {
		this.enabledExt = enable;
	}

	public boolean notifyAction() {
		if ( container == null )
			return false;

		JFileChooser chooser = UIFactory.getInstance().getSaveFileChooser();
		if ( currentDir != null ) {
			chooser.setCurrentDirectory( new File( currentDir ) );
		}

		chooser.setFileFilter(container.getDocumentInfo().getFileFilter());
		if (container.getDocumentInfo().getWorkingDirectory() != null) {
			chooser.setCurrentDirectory(
				new File(container.getDocumentInfo().getWorkingDirectory()));
		} else
			if ( container.getDocumentInfo().getCurrentDocumentLocation() != null )
				chooser.setCurrentDirectory( 
					new File( container.getDocumentInfo().getCurrentDocumentLocation() ).getParentFile() );

		int returnVal = chooser.showSaveDialog(editor);
		if ( returnVal == JFileChooser.APPROVE_OPTION ) {
			String fileName = "" + chooser.getSelectedFile();
			
			try {
				currentDir = chooser.getSelectedFile().getParentFile().toString();
			} catch( Throwable th ) {}
			
			if (enabledExt) {
				// Check if the file name only contains a '.' or not
				if (chooser.getSelectedFile().getName().indexOf(".") == -1)
					fileName += "."
						+ container.getDocumentInfo().getDefaultFileExt();
			}			
			try {
				
				// Check if this is to another file name than the current one
				if ( container.getCurrentDocumentLocation() == null ||
						!container.getCurrentDocumentLocation().equals( fileName ) ) {
					File f = new File( fileName );
					if ( f.exists() ) {
						if ( JOptionPane.showConfirmDialog(
								container.getView(),
								"Overwrite " + fileName + " ?" ) !=
									JOptionPane.YES_OPTION ) {
							return false;
						}
					}
				}

				if (saveDocument(fileName)) {
					container.setCurrentDocumentLocation(fileName);
					return VALID_ACTION;
				} else
					return INVALID_ACTION;
			} catch (Throwable th) {
				JOptionPane.showMessageDialog(
					editor,
					th.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
				return INVALID_ACTION;
			}
		} else
			return INVALID_ACTION;
	}

}
