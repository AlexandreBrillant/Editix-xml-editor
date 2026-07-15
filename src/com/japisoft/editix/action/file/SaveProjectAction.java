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
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.framework.descriptor.ActionModel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class SaveProjectAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {

		if ( ProjectManager.getLastOpenProject( false ) == null && 
				getValue( "param" ) == null )
			ActionModel.activeActionById( ActionModel.SAVEAS_PROJECT, e );
		else {
			String file = ProjectManager.getLastOpenProject( false );
			if ( getValue( "param" ) != null ) 
				file = getValue( "param" ).toString();

			ActionModel.activeActionById( ActionModel.SAVE_ALL, e );

			/*// Update the ProjectModel model
			 ProjectManager.cleanProjectContent();
			 for ( int i = 0; i < EditixFrame.ACCESSOR.getMainTabbedPane().getTabCount() ; i++ ) {
				XMLContainer container = ( XMLContainer ) 
					( ( IXMLPanel )EditixFrame.ACCESSOR.getMainTabbedPane().getComponentAt( i ) ).getMainContainer();
				if ( container == null )
					continue;
				if ( container.getCurrentDocumentLocation() != null && container.getCurrentDocumentLocationArg() == null ) {
					ProjectManager.addProjectElement( container );
				}
			}*/ 

			if ( file.indexOf( ".") == -1 )
				file += ".pre";
			
			if ( ProjectManager.isEmpty() ) {
				EditixFactory.buildAndShowWarningDialog( "Your project is empty. Can't save" );
			} else  {
			
				boolean ok = ProjectManager.saveProject( file );
			
				if ( !ok ) {
					EditixFactory.buildAndShowErrorDialog( "Can't save this project " + ProjectManager.getLastOpenProject( false ) );
				} else {
					OpenProjectAction.updateOpenRecent( file );
					EditixStatusBar.ACCESSOR.setDelayedMessage( "File " + file + " saved..." );
				}
			
			}
		}
	}
	
}
