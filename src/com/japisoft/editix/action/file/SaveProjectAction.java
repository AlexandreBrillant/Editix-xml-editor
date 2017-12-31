package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixStatusBar;

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
