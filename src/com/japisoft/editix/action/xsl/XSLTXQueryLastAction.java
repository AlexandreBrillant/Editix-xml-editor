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
import javax.xml.transform.ErrorListener;

import com.japisoft.editix.action.xquery.XQueryAction;
import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class XSLTXQueryLastAction extends AbstractAction {

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

		ErrorListener xsltAction = ( ErrorListener )ActionModel.restoreAction( "transformWithXSLT" );		
		ErrorListener xqueryAction = ( ErrorListener )ActionModel.restoreAction( "transformWithXQuery" );		
		
		if ( !"true".equals( container.getProperty( "xslt.ok" ) ) && 
				!"true".equals( container.getProperty( "xquery.ok" ) ) ) {
			
			XSLTDialog dialog = EditixFactory.getConfigDialog( 
					"XQR".equals( panel.getMainContainer().getDocumentInfo().getType() )		
			);

			// Show the configure dialog
			if ( "XQR".equals( panel.getMainContainer().getDocumentInfo().getType() ) ) {
				
			} else {
				
				if ( panel.getMainContainer().getDocumentInfo().getType().startsWith( "XSLT" ) ) {
					panel.setProperty( "xslt.xslt.file", container.getCurrentDocumentLocation() );
				}
								
			}
			
			dialog.init( panel );
			dialog.setVisible( true );
			dialog.dispose();
			
			if ( dialog.isOk() ) {
				dialog.store( panel );
			}
		}

		if ( "true".equals( container.getProperty( "xslt.ok" ) ) ) {
			// Repeat the last XSLT config
			XSLTAction.applyTransformation( panel, true, false, false, xsltAction );
		}

		if ( "true".equals( container.getProperty( "xquery.ok" ) ) ) {
			( (XQueryAction)xqueryAction ).transform(
					panel,
					true,
					false,
					false,
					xqueryAction );
		}

	}

}
