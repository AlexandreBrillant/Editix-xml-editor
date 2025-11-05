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

