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

package com.japisoft.editix.ui.leftpanels.project2;

import java.io.File;
import java.util.List;

public class ProjectManager {

	private ProjectManager() {}
	
	public static ProjectManager INSTANCE = null;
	
	public static ProjectManager getInstance() {
		if ( INSTANCE == null )
			INSTANCE = new ProjectManager();
		return INSTANCE;
	}
	
	public Project newProject( File f ) {
		if ( !f.exists() ) {
			f.mkdirs();
		}
		return new DefaultProject( f );
	}
	
	private Project lastProject = null;
	
	public Project loadProject( File f ) {
		lastProject = new DefaultProject( f );
		return lastProject;
	}
	
	public void searchFilesForType( List l, String type ) {
		if ( lastProject != null ) {
			try {
				lastProject.searchFilesForType( l, type );
			} catch( Throwable th ) {
			}
		}
	}

}
