package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.japisoft.editix.document.TemplateModel;
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
public class SaveAsTemplate extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowWarningDialog( "Please create or load a document for building a template" );
			return;
		}
		String name = null;
		if ( ( name = EditixFactory.buildAndShowInputDialog( "Template name ?" ) ) != null ) {
			String type = container.getDocumentInfo().getType();
			String content = container.getText();
			try {
				TemplateModel.createNewTemplate( name, type, content );
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't save this template : " + exc.getMessage() );
			}
		}
	}

}
