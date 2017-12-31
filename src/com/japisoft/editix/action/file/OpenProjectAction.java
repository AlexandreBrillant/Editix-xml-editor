package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.panels.PanelManager;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
		PanelManager.initByAction( "projectManager" );
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
