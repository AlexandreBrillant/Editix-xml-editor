package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.action.xml.CheckableAction;

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
public class CheckAllAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		XMLContainer currentContainer = EditixFrame.THIS.getSelectedContainer();
		
		int nbDocumentChecked = 0;
		int nbErrorFound = 0;

		int indiceFirstError = -1;

		for ( int i = 0; i < 
			EditixFrame.THIS.getXMLContainerCount(); i++ ) {
			XMLContainer editor = EditixFrame.THIS.getXMLContainer( i );
			if ( editor == null )
				continue;
			XMLDocumentInfo info = editor.getDocumentInfo();
			String checkerId = info.getParamValue( "checkerid" );
			if ( checkerId != null ) {
				Action a =
					ActionModel.restoreAction( checkerId );
				if ( a != null ) {
					if ( a instanceof CheckableAction ) {
						CheckableAction ca = ( CheckableAction )a;
						boolean error = 
							!ca.checkDocument( editor, true );
						if ( error ) {
							nbErrorFound++;
							if ( indiceFirstError == -1 ) {
								if ( editor != currentContainer )
									indiceFirstError = i;
							}
						}
						nbDocumentChecked++;
					} else
						System.out.println( 
								"Action " + checkerId + " should be a CheckableAction ?" );
				} else
					System.out.println( 
							"Can't find action " + checkerId );
			}
		}

		if ( indiceFirstError == -1 ) {
		
			EditixFactory.buildAndShowInformationDialog( 
					nbDocumentChecked + 
					" document(s) checked\n" + 
						nbErrorFound + 
							" error(s) found" );
		
		} else {
			
			if ( EditixFactory.buildAndShowConfirmDialog( nbErrorFound + " error(s) found\nGo to the first error ?") ) {
				EditixFrame.THIS.activeXMLContainer( indiceFirstError );
			}
			
		}
	}

}
