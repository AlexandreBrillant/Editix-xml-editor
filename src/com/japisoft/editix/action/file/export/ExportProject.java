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

package com.japisoft.editix.action.file.export;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.toolkit.FileManager;

public class ExportProject extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		File f = FileManager.getSelectedFile( false, "zip", "Project ZIP" );
		try {
			if ( f != null )
				ProjectManager.exportToZip( f );
			ApplicationModel.debug( "Write " + f );
		} catch (Throwable e1) {
			ApplicationModel.debug( e1 );
			EditixFactory.buildAndShowErrorDialog( "Can't export : " + e1.getMessage() );
		}
	}

}

