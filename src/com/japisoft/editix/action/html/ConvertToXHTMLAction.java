package com.japisoft.editix.action.html;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;

import javax.swing.AbstractAction;

import com.japisoft.editix.action.file.imp.HTMLImport;
import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;
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
public class ConvertToXHTMLAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent arg0) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		
		if (EditixFactory.mustSaveDialog(container)) {
			return;
		}
		
		try {
			String location = container.getCurrentDocumentLocation();
			byte[] data = HTMLImport.convertHTMLInputStream( 
					new FileInputStream( location ) );
			if ( data != null ) {
				IXMLPanel panel = EditixFactory.buildNewContainer("XHTML", (String)null);
				XMLContainer newContainer = panel.getMainContainer();
				newContainer.setText(Toolkit.getEncodedString(data).getContent());
				EditixFrame.THIS.addContainer(panel);
			}
		} catch( Throwable exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't convert : " + exc.getMessage() );
		}

	}

}
