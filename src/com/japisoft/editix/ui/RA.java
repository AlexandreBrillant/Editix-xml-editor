package com.japisoft.editix.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.p3.Manager;

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
class RA extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		RegisteredDialog dialog = new RegisteredDialog();
		dialog.setSize( 400, 460 );
		dialog.setVisible( true );
		if ( dialog.isOk() ) {
			String u = dialog.getUser();
			String p = dialog.getKey();
			
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
	
