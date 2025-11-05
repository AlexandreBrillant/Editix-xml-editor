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

package com.japisoft.framework.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.UIManager;

public class LinkLabel extends FastLabel implements MouseListener {

	public LinkLabel( String label ) {
		super();
		setText( label );
		setForeground( getInitialColor() );
	}
	
	private Color getInitialColor() {
		Color _ = UIManager.getColor( "editix.linklabel" );
		if ( _ == null )
			_ = new java.awt.Color(51, 51, 255);
		return _;
	}
	
	public LinkLabel() {
		setForeground( getInitialColor() );
	}
	
	public void addNotify() {
		super.addNotify();
		addMouseListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeMouseListener( this );
	}
		
	public void mouseEntered(MouseEvent e) {
		setUnderlineMode( true );
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
		setUnderlineMode( false );
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}

