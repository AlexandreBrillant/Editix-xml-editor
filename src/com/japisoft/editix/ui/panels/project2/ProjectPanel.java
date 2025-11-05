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

package com.japisoft.editix.ui.panels.project2;

import java.io.File;

import javax.swing.JComponent;

import com.japisoft.editix.ui.panels.AbstractPanel;
import com.japisoft.framework.ApplicationModel;

public class ProjectPanel extends AbstractPanel {

	@Override
	protected JComponent buildView() {
		return new ProjectUI();
	}

	@Override
	protected String getTitle() {
		return "Project";
	}
	
	@Override
	protected String getId() {
		return "project2";
	}

	public void loadProject( File file ) {
		if ( file == null ) {
			hide();
		} else {
			Project project = ProjectManager.getInstance().loadProject( file );
			try {
				project.load();
			} catch( Exception exc ) {
				ApplicationModel.debug( exc );
			}
			( ( ProjectUI )getView() ).setProject( project );
			show();
			setState( true );
		}
	}
	
	public void stop() {
		try {
			( ( ProjectUI )getView() ).save();
		} catch( Exception e ) {
			ApplicationModel.debug( e );
		}
	}
	
	@Override
	protected void preHide() {
		stop();
	}

}

