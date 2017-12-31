package com.japisoft.editix.ui.panels.project;

import javax.swing.JComponent;

import com.japisoft.editix.project.ProjectListener;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.panels.AbstractPanel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;

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
public class ProjectPanel extends AbstractPanel implements ProjectListener {

	public void init() {
		super.init();
		ProjectManager.setProjectListener( this );
		
		// Must load the last one ??
		if ( ProjectUI.isLocked() ) {
			String lastProjectFile = 
				Preferences.getPreference( 
						Preferences.SYSTEM_GP,
						"project.lastFile", ( String )null );
			if ( lastProjectFile != null ) {
				ProjectListener pl = ProjectManager.getProjectListener();
				ProjectManager.setProjectListener( null );
				try {
					ProjectManager.openProject( lastProjectFile );
				} finally {
					ProjectManager.setProjectListener( pl );
				}
			}
		}
		( ( ProjectUI )getView() ).init();
	}

	protected String getTitle() {
		return "Project";
	}

	protected JComponent buildView() {
		return new ProjectUI();
	}

	public void loadProject( String file ) {
		( ( ProjectUI )getView() ).loadProject( file );
		show();
		setState( true );
	}

	public void clean() {
		( ( ProjectUI )getView() ).clean();		
	}

	public void showPanel() {
		super.showPanel();
		ProjectManager.initProjectActions( true );
		( ( ProjectUI )getView() ).setEnabledAction( 
				ProjectUI.CHECK_CMD, true );
		( ( ProjectUI )getView() ).setEnabledAction( 
				ProjectUI.ZIP_CMD, true );
		ActionModel.setEnabled( "prjSaveAs", true );
		ActionModel.setEnabled( "prjSave", true );
		( ( ProjectUI )getView() ).init();
	}

	public void refresh() {
		( ( ProjectUI )getView() ).refresh();
	}	

	public void stop() {		
		Preferences.setPreference( 
				Preferences.SYSTEM_GP,
				"project.lastFile", ProjectManager.getLastOpenProject( true ) );
		// Save it
		ProjectManager.saveProject( ProjectManager.getLastOpenProject( true ) );
	}

	protected void preHide() {
		for ( int i = 0; i < ProjectUI.POPUP_ACTIONS.length; i++ ) {
			if ( ProjectUI.POPUP_ACTIONS[ i ] != null )
				ActionModel.setEnabled( "prj." + ProjectUI.POPUP_ACTIONS[ i ], false );
		}
		ActionModel.setEnabled( "prjSaveAs", false );
		ActionModel.setEnabled( "prjSave", false );		
	}	

}
