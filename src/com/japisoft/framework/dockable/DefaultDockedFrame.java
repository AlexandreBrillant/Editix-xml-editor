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
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
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
