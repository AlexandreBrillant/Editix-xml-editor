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

package com.japisoft.editix.action.xsl;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.xml.transform.ErrorListener;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.xslt.debug.XSLTManager;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

public class XSLProfilerAction extends AbstractAction {

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

		if ( !"true".equals( container.getProperty( "xslt.ok" ) ) ) {
			XSLTDialog dialog = new XSLTDialog();
			dialog.init( panel );
			dialog.setVisible( true );
			dialog.dispose();
			if ( dialog.isOk() ) {
				dialog.store( panel );
			} else
				return;
		}

		ErrorListener xsltAction = ( ErrorListener )ActionModel.restoreAction( "transformWithXSLT" );
		XSLTAction.applyTransformation( panel, true, true, true, xsltAction );
		XSLTManager.endProfiler();

	}

}

