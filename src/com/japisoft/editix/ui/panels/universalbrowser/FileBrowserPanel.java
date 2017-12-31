package com.japisoft.editix.ui.panels.universalbrowser;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
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
public class FileBrowserPanel extends CommonUniversalBrowserPanel {

	protected String getTitle() {
		return "File Browser";
	}

	protected Action[] getBrowserActions() {
		return new Action[] {
			new ChangeDefaultDirectory(),
			new RefreshContent()
		};
	}	

	protected boolean storeLastBrowsedFileObject() {
		return false;
	}	
	
	class RefreshContent extends AbstractAction {
		
		public RefreshContent() {
			putValue( 
					Action.SMALL_ICON, 
					new ImageIcon( 
							ClassLoader.getSystemResource( 
									"images/folder_refresh.png" ) ) );
			putValue(
					Action.SHORT_DESCRIPTION,
					"Refresh the file content" );			
		}
		
		public void actionPerformed(ActionEvent e) {
			try {
				browserTree.refresh();
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't refresh : " + exc.getMessage() );
			}
		}
	}

	class ChangeDefaultDirectory extends AbstractAction {

		ChangeDefaultDirectory() {
			putValue( 
					Action.SMALL_ICON, 
					new ImageIcon( 
							ClassLoader.getSystemResource( 
									"images/folder_view.png" ) ) );
			putValue(
					Action.SHORT_DESCRIPTION,
					"Change the default directory" );
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser cc = new JFileChooser();
			cc.setCurrentDirectory(					
					new File(
					Preferences.getPreference( 
							"filebrowser", 
							"defaultpath", 
							System.getProperty( "user.home" ) )					
					
					) );
			cc.setMultiSelectionEnabled( false );
			cc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			if ( cc.showOpenDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {
				
				Preferences.setPreference( 
						"filebrowser", 
						"defaultpath", 
						cc.getSelectedFile().toString() );

				String uri = "file:///";
				uri += cc.getSelectedFile().toString();
				
				Preferences.setPreference( 
						Preferences.SYSTEM_GP, 
						FileBrowserPanel.class.getName(),
						uri );				

				browse(
						uri, 
						null, 
						null );

			}
		}

	}

}
