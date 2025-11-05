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

package com.japisoft.editix.action.file.project;

import java.awt.event.ActionEvent;
import java.io.File;

import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.ui.panels.Panel;
import com.japisoft.editix.ui.panels.project2.NewProjectPanel;
import com.japisoft.editix.ui.panels.project2.ProjectPanel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.OKAction;
import com.japisoft.framework.preferences.Preferences;

public class NewProjectAction extends PanelAction implements ApplicationModelListener {

	private static final String PROJECT_HISTORY = "lastProjects.txt";

	public NewProjectAction() {
		super();
		// For avoiding duplicate call
		if ( getClass() == NewProjectAction.class ) {
			ApplicationModel.addApplicationModelListener( this );
		}
	}

	public void actionPerformed(ActionEvent e) {
		NewProjectPanel npp = new NewProjectPanel();
		if ( DialogManager.showDialog(
				null, 
				"Create a a new project",
				"New Project", 
				"Choose your project name and location",
				null,
				npp ) == OKAction.ID ) {

			File projectLocation = npp.getProjectLocation();
			if ( !projectLocation.exists() )
				projectLocation.mkdirs();
			loadProject( projectLocation );
		}
	}

	public void fireApplicationData(String key, Object... values) {
		if ( "lastProject".equals( key ) ) {
			String fileName = ( String )values[ 0 ];
			if ( fileName == null ) {
				try {
					hide();
				} catch( Exception exc ) {}
			} else {
				showHide();				
				File f = new File( fileName );
				ApplicationModel.setSharedProperty( "lastProject", f );
				( ( ProjectPanel )getPanel() ).loadProject( f );	
			}
		} else
		if ( "quit".equals( key ) ) {
			stop();
		}
	}

	public void loadProject( File projectLocation ) {

		String projectPath = null;

		if ( projectLocation != null )
			projectPath = projectLocation.toString();
		
		ApplicationModel.fireApplicationValue( 
			"lastProject",
			projectPath );

	}
	
	private ProjectPanel pp = null;
	
	@Override
	public Panel preparePanel() {
		if ( pp == null ) {
			pp = new ProjectPanel(); 
		}
		return pp;
	}
	
}

