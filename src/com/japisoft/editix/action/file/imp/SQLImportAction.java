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

package com.japisoft.editix.action.file.imp;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.dialog.DialogManager;

public class SQLImportAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {

		File jdbcDrivers = new File( 
			EditixApplicationModel.getAppUserPath(), 
			"jdbcdrivers.xml" 
		); 

		JDBCDriverModel m = new JDBCDriverModel();

		if ( jdbcDrivers.exists() ) {
			try {
				m.read( jdbcDrivers	);
			} catch( Exception exc ) {
				EditixApplicationModel.debug( exc );
			}
		}

		m.addDriver( JDBCDriver.ODBC_DRIVER );

		SQLImportPanel p = new SQLImportPanel();

		p.setDriverModel( m );

		if (
			DialogManager.showDialog( 
					EditixApplicationModel.MAIN_FRAME, 
					"SQL Import", 
					"SQL Import", 
					"Choose a driver and run your SQL query for converting to XML", 
					null,
					p ) == DialogManager.OK_ID ) {
			
			try {
				
				m.removeDriver( JDBCDriver.ODBC_DRIVER );

				// Save the driver model
				m.write( 
						jdbcDrivers
				);

			} catch( Exception exc ) {
				EditixApplicationModel.debug( exc );
			}
		}

		try {
			p.storeState();
		} catch( Exception exc ) {
			EditixFactory.buildAndShowWarningDialog( "Can't save user parameters : [" + exc.getMessage() + "]" );
		}
	}

}

