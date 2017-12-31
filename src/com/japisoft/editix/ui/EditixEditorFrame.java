package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

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
