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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.ui.panels.universalbrowser.CommonUniversalBrowserPanel;
import com.japisoft.editix.ui.panels.universalbrowser.FileBrowserPanel;
import com.japisoft.framework.preferences.Preferences;

public class BrowseFileAction extends PanelAction {

	public void actionPerformed(ActionEvent e) {

		String uri = "file://";

		uri += Preferences.getPreference( 
				"filebrowser", 
				"defaultpath", 
				System.getProperty( "user.home" )
		);

		Preferences.setPreference(
				Preferences.SYSTEM_GP, 
				FileBrowserPanel.class.getName(),
				uri
		);

		if ( preparePanel().isShown() )
			( ( CommonUniversalBrowserPanel )( preparePanel() ) ).init();

		alwaysShown = true;

		super.actionPerformed(e);

	}	

}

