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

package com.japisoft.editix.action.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import com.japisoft.editix.ui.windows.EditixDialog;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class SystemView extends AbstractAction {

	public void actionPerformed(ActionEvent e) {		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		if ( container.getCurrentDocumentLocation() != null ) {
			BrowserCaller.displayURL(
					container.getCurrentDocumentLocation() );
		} else {
			String __ = container.getText();
			if ( __.startsWith( "<?") ) {
				int i = __.indexOf( "\n" );
				if ( i > -1 )
					__ = __.substring( i );
			}
			HTMLDialog dialog = new HTMLDialog( __ );
	
			Point p = ( Point )container.getProperty( "view.html.location" );
			Dimension d =( Dimension )container.getProperty( "view.html.dimension" );
	
			if ( p != null ) 
				dialog.setLocation( p );
			if ( d != null )
				dialog.setSize( d );
	
			if ( p != null )
				dialog.setVisible( true );
			else
					dialog.setVisible( true );
	
			if ( dialog.isOk() ) {
				container.setProperty( "view.html.location", dialog.getLocation() );
				container.setProperty( "view.html.dimension", dialog.getSize() );
			}
		}
	}

	class HTMLDialog extends EditixDialog {
		HTMLDialog( String html ) {
			super( "XHTML", "HTML View", "This is a mini browser.\nSave your document for displaying it inside a system viewer" );
			JEditorPane ed = new JEditorPane( "text/html", html );	
			ed.setEditable( false );	
			getContentPane().add( new JScrollPane( ed ) );
		}
		protected Dimension getDefaultSize() {
			return new Dimension( 400, 400 );
		}
	}

}
