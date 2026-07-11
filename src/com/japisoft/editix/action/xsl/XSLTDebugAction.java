// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.xsl;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.xslt.debug.XSLTManager;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class XSLTDebugAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();

		if ( container == null )
			return;

		if ( EditixFactory.mustSaveDialog( container ) ) {
			return;
		}	
				
		boolean ok = com.japisoft.xmlpad.action.ActionModel.activeActionByName(
			com.japisoft.xmlpad.action.ActionModel.SAVE_ACTION,
			container,
			container.getEditor() );
		
		if ( !ok )
			return;
		
		if ( !XSLTManager.hasBreakpoint( panel ) ) {
			EditixFactory.buildAndShowErrorDialog( "No breakpoint found for debugging !\nClick on the left column of the editor for adding/removing a breakpoint" );
			return;
		}

		if ( !"true".equals( container.getProperty( "xslt.ok" ) ) ) {
			XSLTDialog dialog = new XSLTDialog();
			dialog.init( panel );
			dialog.setVisible( true );
			dialog.dispose();
			if ( dialog.isOk() ) {
				dialog.store( panel );
				XSLTManager.startDebug( panel );
			}
		} else {
			XSLTManager.startDebug( panel );
		}
		
	}

}
