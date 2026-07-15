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

package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

public class EditixEditorFrame extends JFrame 
		implements WindowListener {
	private IXMLPanel container;

	public EditixEditorFrame( IXMLPanel panel ) {
		this.container = panel;		

		XMLContainer c = panel.getMainContainer();		
		if ( c.getCurrentDocumentLocation() != null )
			setTitle( c.getCurrentDocumentLocation() );
		else
			setTitle( "New document" );
		
		getContentPane().setLayout( new BorderLayout(0,0) );
		getContentPane().add( panel.getView() );
		setSize( 600, 400 );
	}
	
	public void addNotify() {
		super.addNotify();
		addWindowListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeWindowListener( this );
	}

	public XMLContainer getXMLContainer() { return container.getMainContainer(); }

	public IXMLPanel getIXMLPanel() { return container; }
	
	public void windowActivated(WindowEvent e) {
		EditixEditorFrameModel.active( this );
	}

	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		EditixEditorFrameModel.removeEditixEditorFrame( this );		
		getContentPane().remove( container.getView() );
		EditixFrame.THIS.addContainer( container );
	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {
		EditixEditorFrameModel.active( this );
	}

}
