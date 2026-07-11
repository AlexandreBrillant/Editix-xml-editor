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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.ActionModel;

/** Overload of the SaveAs action from the JXMLPad component for updating
 * the tabbed name
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class SaveAsAction extends AbstractAction {
	
	public void actionPerformed( ActionEvent e ) {
		if ( EditixFrame.THIS.isEmpty() || 
				EditixFrame.THIS.getSelectedContainer() == null )
			return ;

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		String currentEncoding = ( String )container.getProperty( "encoding" );
		if ( currentEncoding == null )
			currentEncoding = Toolkit.getCurrentFileEncoding(); 
		
		// Reset the file encoding
		ActionModel.setProperty( ActionModel.SAVE_ACTION, 
			com.japisoft.xmlpad.action.file.SaveAction.ENCODING_PROPERTY,
			currentEncoding );

		// Reset the file encoding
		ActionModel.setProperty( ActionModel.SAVEAS_ACTION, 
			com.japisoft.xmlpad.action.file.SaveAction.ENCODING_PROPERTY,
			currentEncoding );

		// Restore the last file location
		String previousPath = Preferences.getPreference( 
			"file",
			"defaultPath",
			""
		);
		
		// Use the last project path
		if ( ApplicationModel.getSharedProperty( "lastProject" ) != null ) {
			ActionModel.setProperty( 
					ActionModel.SAVEAS_ACTION,
					com.japisoft.xmlpad.action.file.SaveAsAction.CURRENT_DIR_PROPERTY,
					ApplicationModel.getSharedProperty( "lastProject" ).toString() );			
		} else		
		if ( !"".equals( previousPath ) ) {
			ActionModel.setProperty( 
				ActionModel.SAVEAS_ACTION,
				com.japisoft.xmlpad.action.file.SaveAsAction.CURRENT_DIR_PROPERTY,
				previousPath );
		}

		String oldLocation = container.getCurrentDocumentLocation();

		if ( SaveAction.save_action( true ) ) {
			// Update the current tab name			
			EditixFrame.THIS.refreshCurrentTabName();
			if ( oldLocation != null )
				ProjectManager.updateFilePath( oldLocation, container.getCurrentDocumentLocation() );
			if ( Preferences.getPreference(
					"file", "restoredLastPath", true ) ) {
				Preferences.setPreference( "file", "defaultPath", ( String )ActionModel.getProperty(
						ActionModel.SAVEAS_ACTION,
						com.japisoft.xmlpad.action.file.SaveAsAction.CURRENT_DIR_PROPERTY,
						null
				) );
			}
		}
	}

}
