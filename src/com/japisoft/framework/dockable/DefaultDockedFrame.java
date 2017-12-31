package com.japisoft.framework.dockable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.dockable.action.common.CommonAction;

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
class DefaultDockedFrame extends JFrame implements WindowListener {

	private Windowable panel;
	private Action source;
	private JToolBar tb;
	
	public DefaultDockedFrame( Action source, Windowable panel ) {
		this.source = source;
		this.panel = panel;
		JComponent compo = ( JComponent )panel.getContentPane();
		panel.getView().remove( compo );
		getContentPane().add( compo );
		setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		setTitle( panel.getTitle() );
		setSize( new Dimension( 400, 400 ) );
		addWindowListener( this );
		panel.repaint();
		DockManager.storeDefaultDockedFrame( this );
		if ( panel.getFrameBounds() != null )
			setBounds( panel.getFrameBounds() );
		
		// Insert toolbar ?
		boolean toolbar = false;
		ActionModel model = panel.getActionModel();
		for ( int i = 0; i < model.getActionCount(); i++ ) {
			if  ( !( model.getAction( i ) instanceof CommonAction ) ) {
				toolbar = true;
				break;
			}
		}
		if ( toolbar ) {
			tb = new JToolBar();
			for ( int i = 0; i < model.getActionCount(); i++ ) {
				if ( !( model.getAction( i ) instanceof CommonAction ) ) 
					tb.add( ComponentFactory.getComponentFactory().buildButton( model.getAction( i ) ) );
			}
			getContentPane().add( tb, BorderLayout.NORTH );
		}
	}

	public void windowActivated(WindowEvent e) {
	}

	public void undock() {
		removeWindowListener( this );
		JComponent compo = ( JComponent )getContentPane().getComponent( 0 );
		getContentPane().remove( compo );
		panel.setContentPane( compo );
		if ( source != null )
			source.setEnabled( true );		

		panel.setFrameBounds( getBounds() );		
		DockManager.unstoreDefaultDockedFrame( this );
		dispose();
	}

	public void windowClosing(WindowEvent e) {
		undock();
	}
	
	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void dispose() {
		super.dispose();
		panel = null;
		source = null;
		if ( tb != null ) {
			tb.removeAll();
			tb = null;
		}
	}

}
