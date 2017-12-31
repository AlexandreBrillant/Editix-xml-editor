package com.japisoft.editix.action.options;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.application.descriptor.composer.DescriptorComposer;
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
public class DescriptorComposerAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {

		URL u = EditixApplicationModel.INTERFACE_BUILDER.getDocumentURL();
		
		DescriptorComposer dcPanel = new DescriptorComposer();

		DialogManager.ACTIVE_DEFAULT_BUTTON = false;

		try {
			dcPanel.loadDescriptor( u, EditixApplicationModel.getCustomEditiXDescriptor() );

			DialogManager.showDialog( 
				EditixApplicationModel.MAIN_FRAME, 
				"Application Descriptor", 
				"EditiX Application Descriptor", 
				"Update the application descriptor. Change menus/items/toolbars of EditiX. You must save and restart the application for usage", 
				null, 
				dcPanel 
			);

		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't load the editix descriptor ? : " + exc.getMessage() );
		}

		DialogManager.ACTIVE_DEFAULT_BUTTON = true;
		
	}	
	
}
