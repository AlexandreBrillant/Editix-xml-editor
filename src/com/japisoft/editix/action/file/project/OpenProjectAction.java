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

package com.japisoft.editix.action.file.project;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.leftpanels.project2.DefaultProject;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;

public class OpenProjectAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		
		ProjectFileChooser pfc = new ProjectFileChooser();
		if ( pfc.showOpenDialog( ApplicationModel.MAIN_FRAME ) == JFileChooser.APPROVE_OPTION ) {
			File f = pfc.getSelectedFile();
			if ( !DefaultProject.isProjectPath( f ) ) {
				EditixFactory.buildAndShowWarningDialog( "Invalid project path, please use the new project action" );
			} else {
				( ( NewProjectAction )ActionModel.restoreAction( "newp" ) ).loadProject( f );
			}
		}
		
	}

}
