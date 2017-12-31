package com.japisoft.editix.action.file.imp;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.dialog.DialogManager;

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
