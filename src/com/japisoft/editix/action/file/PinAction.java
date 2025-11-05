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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.xmlpad.XMLContainer;

public class PinAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent evt) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container != null ) {
			String documentLocation = container.getCurrentDocumentLocation();
			if ( documentLocation == null ) {
				EditixFactory.buildAndShowErrorDialog("You must save your document before");
			} else {
				if ( PinManager.Instance().contains( documentLocation ) ) {
					PinManager.Instance().removeFile( documentLocation );
					EditixFactory.buildAndShowInformationDialog( "Unpin " + documentLocation );
				} else {
					PinManager.Instance().addFile( documentLocation, container.getDocumentInfo().getType(), container.getPropertiesMap() );
					EditixFactory.buildAndShowInformationDialog( "Pin " + documentLocation );
				}
				ApplicationModel.INTERFACE_BUILDER.rebuild( "pinFiles" );
			}
		}
	}
	
}

