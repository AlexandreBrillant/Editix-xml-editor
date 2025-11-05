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

package com.japisoft.editix.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.register.RegisteredPane2;
import com.japisoft.p3.Manager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
class RA extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		RegisteredPane2 rp2 = new RegisteredPane2();
		if ( DialogManager.showDialog(
			(Window)null, 
			"Register",
			"Register", 
			"You can order at https://www.editix.com and receive by mail your activating key. Copy and Paste your registered name and key or fill it from a file", 
			null, 
			rp2 
		) == DialogManager.OK_ID ) {
			String u = rp2.getUser();
			String p = rp2.getKey();
			
			try {
				boolean ok = Manager.registered( u, p );
				if ( ok ) {
					EditixFactory.buildAndShowInformationDialog( "Registered EditiX is now activated. Please restart the application" );
				} else {
					EditixFactory.buildAndShowErrorDialog( "Wrong name or key, please check for your registered name and key (remove extra spaces...)\nNote that a 2008 key is only for a 2008 version, a 2009 key is only for a 2009 version, etc...\n\nYour current version is EditiX XML Editor " + EditixApplicationModel.getAppYear() + "\n\nIf you have non ASCII characters inside your registered name, it may be wrongly encoded, please contact us at : editixsupport@japisoft.com for generating a new key" );
				}
			} catch( Exception exc ) {
				if ( !"Extend evaluation".equals( exc.getMessage() ) )
					EditixFactory.buildAndShowErrorDialog( "Can't register : " + exc.getMessage() );
				else
					throw new RuntimeException( "Extend evaluation" );
			}			
		}

	}

}
	
