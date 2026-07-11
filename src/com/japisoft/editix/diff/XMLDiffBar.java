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

package com.japisoft.editix.diff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class XMLDiffBar extends JComponent implements MouseListener, MouseMotionListener {

	@Override
	public void addNotify() {
		super.addNotify();
		addMouseListener( this );
		addMouseMotionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeMouseListener( this );
		removeMouseMotionListener( this );
		listener = null;
	}

	public void addRange(
		boolean left,
		int startLine,
		Rectangle startView,
		Rectangle stopView,
		int maxHeight,
		Color col,
		String message ) {

		if ( items == null ) {
			items = new ArrayList<XMLDiffBar.RangeItem>();
		}

		items.add( 
			new RangeItem( 
				left, 
				startLine, 
				startView, 
				stopView, 
				maxHeight, 
				col, 
				message )
		);
		repaint();
	}

	List<RangeItem> items = null;

	public void clear() {
		items = null;
		repaint();
	}

	class RangeItem {
		boolean left;		
		int startLine; 
		Rectangle startView; 
		Rectangle stopView; 
		int maxHeight; 
		Color col; 
		String message;
		Rectangle r;

		RangeItem(
			boolean left,
			int startLine, 
			Rectangle startView, 
			Rectangle stopView, 
			int maxHeight, 
			Color col, 
			String message ) {
			this.left = left;			
			this.startLine = startLine;
			this.startView = startView;
			this.stopView = stopView;
			this.maxHeight = maxHeight;
			this.col = col;
			this.message = message;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent( g );

		if ( items != null ) {
			for ( int i = 0; i < items.size(); i++ ) {
				RangeItem ri = items.get( i );
				int startRange = ( int )( ( ( double )ri.startView.y / ( double )ri.maxHeight ) * ( double )getHeight() );
				int stopRange = ( int )( ( ( double )ri.stopView.y / ( double )ri.maxHeight ) * ( double )getHeight() );

				if ( ri.left ) {				
					g.setColor( ri.col );
					g.fillRect( 0, startRange, getWidth() / 2, stopRange - startRange );
					if ( ri.r == null ) {
						ri.r = new Rectangle( 0, startRange, getWidth() / 2, stopRange - startRange );
					}
				} else {
					g.setColor( ri.col );
					g.fillRect( getWidth() / 2, startRange, getWidth() / 2, stopRange - startRange );
					if ( ri.r == null ) {
						ri.r = new Rectangle( getWidth() / 2, startRange, getWidth() / 2, stopRange - startRange );
					}
				}
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		for ( int i = 0; i < items.size(); i++ ) {
			RangeItem ri = items.get( i );
			if ( ri.r != null && ri.r.contains( e.getPoint() ) ) {
				if ( listener != null ) {
					listener.notifyLocation( ri.left, ri.startLine );
				}
			}
		}
	}
	
	XMLDiffBarListener listener = null;
	
	void setXMLDiffBarListener( XMLDiffBarListener listener ) {
		this.listener = listener;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		setToolTipText( null );
		
		if ( items != null )
		for ( int i = 0; i < items.size(); i++ ) {
			RangeItem ri = items.get( i );
			int startRange = ( int )( ( ( double )ri.startView.y / ( double )ri.maxHeight ) * ( double )getHeight() );
			int stopRange = ( int )( ( ( double )ri.stopView.y / ( double )ri.maxHeight ) * ( double )getHeight() );

			if ( ri.r != null && ri.r.contains( e.getPoint() ) ) {
				setToolTipText( ri.message );
			}
		}
		
	}

}
