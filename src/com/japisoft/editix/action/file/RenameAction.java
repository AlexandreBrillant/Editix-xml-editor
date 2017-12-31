package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

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
public class RenameAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();		
		if ( container != null ) {
			if ( container.getCurrentDocumentLocation() == null )
				EditixFactory.buildAndShowErrorDialog( "No location for this document" );
			else {
				String currentName = container.getDocumentInfo().getDocumentName();
				String newName = EditixFactory.buildAndShowInputDialog( "Rename", currentName );
				if ( newName != null ) {
					String oldLocation = container.getCurrentDocumentLocation();
					File f = new File( container.getCurrentDocumentLocation() );
					File f2 = null;
					if ( !f.renameTo(
							f2 = new File( f.getParentFile(), newName ) ) ) {
						EditixFactory.buildAndShowErrorDialog( "Can't rename to " + newName );
					} else {
						container.getDocumentInfo().setCurrentDocumentLocation(
								f2.toString() );
						EditixFrame.THIS.refreshCurrentTabName();
						ProjectManager.updateFilePath( oldLocation, f2.toString() );
					}
				}
			}
		}
	}
}
