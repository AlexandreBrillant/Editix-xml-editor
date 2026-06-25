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

package com.japisoft.editix.action.options;

import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.net.URL;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ui.toolkit.BrowserCaller;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class CheckVersionAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {
		try {
			String version = getLastVersion();
			double officialVersion = Double.parseDouble( version );
			
			double currentVersion = EditixApplicationModel.MAJOR_VERSION;
			currentVersion += EditixApplicationModel.MINOR_VERSION / 10;

			if ( currentVersion > officialVersion ) {
				EditixFactory.buildAndShowInformationDialog( "You have the a non official release" );
			} else
			if ( currentVersion < officialVersion ) {
				EditixFactory.buildAndShowInformationDialog( "You can download the version " + version + " at https://www.editix.com" );
				BrowserCaller.displayURL( "https://www.editix.com" );
			} else
				EditixFactory.buildAndShowInformationDialog( "You have the latest version" );
		} catch( Throwable th ) {
			EditixFactory.buildAndShowErrorDialog( "Can't check the last version" );
		}
	}

	public static String getLastVersion() throws Exception {
		URL url = new URL( "https://www.editix.com/editix.ver" );
		InputStream input = url.openStream();
		StringBuffer sb = new StringBuffer();
		int c;
		while ( ( c = input.read() ) != -1 ) {
			sb.append( ( char )c );
		}
		return sb.toString().trim();
	}
	
}

