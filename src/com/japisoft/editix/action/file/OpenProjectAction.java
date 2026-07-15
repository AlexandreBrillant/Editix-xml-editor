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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.leftpanels.LeftPanelManager;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.descriptor.InterfaceBuilder;
import com.japisoft.editix.ui.EditixFactory;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class OpenProjectAction extends AbstractAction {

	private boolean canCloseProject( ActionEvent e ) {
		CloseAllAction.RETURN_STATUS = true;
		return CloseAllAction.RETURN_STATUS;
	}

	public void actionPerformed( ActionEvent e ) {
		// Check to save
		
		if ( getValue( "param" ) != null ) {
			if ( !canCloseProject( e ) )
				return;
			openProject( true, "" + getValue( "param" ) );
			return;
		}
		
		JFileChooser fc = EditixFactory.buildProjectFileChooser();
		if ( fc.showOpenDialog( EditixFrame.THIS.getComponent( 0 ) ) == JFileChooser.APPROVE_OPTION ) {
			File f = fc.getSelectedFile();
			if ( !f.exists() || !canCloseProject( e ) ) {
				if ( !f.exists() ) {
					EditixFactory.buildAndShowErrorDialog("Can't load " + f);
				}
				return;			
			}
			openProject( false, f.toString());
		} else {
			EditixFrame.THIS.checkOneContainer();
		}
	}
	
	public static void openProject( boolean param, String file) {
		// Ask for the ProjectPanel to be visible
		LeftPanelManager.initByAction( "projectManager" );
		boolean ok = ProjectManager.openProject( file );

		if ( !ok ) {
			EditixFactory.buildAndShowErrorDialog( "Can't load " + file );
			EditixFrame.THIS.getBuilder().removeMenuItemForParam(
					InterfaceBuilder.MENU_RECENT_PROJECT, file );			
			EditixFrame.THIS.checkOneContainer();
			return;
		} else {
			// Open the project panel
			
		}

		if ( !param ) {
			updateOpenRecent( file );
		}
	}

	static void updateOpenRecent( String file ) {
		if ( file != null ) {
			Action a = new OpenProjectAction();
			a.putValue( Action.NAME, file );
			a.putValue( "param", file );
			a.putValue( "iconPath", "images/package.png" );
	
			EditixFrame.THIS.getBuilder().insertMenuItemAtFirst(
				InterfaceBuilder.MENU_RECENT_PROJECT,
				a,
				20 );
		}
	}

}
