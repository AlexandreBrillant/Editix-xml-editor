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

package com.japisoft.editix.ui.leftpanels.universalbrowser;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.preferences.Preferences;

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
