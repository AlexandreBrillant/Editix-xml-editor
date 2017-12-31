package com.japisoft.editix.action.file.project;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.panels.project2.DefaultProject;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;

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
