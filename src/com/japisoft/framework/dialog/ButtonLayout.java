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

package com.japisoft.framework.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import javax.swing.JComponent;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
class ButtonLayout implements LayoutManager2 {

	public ButtonLayout() {
		super();
	}
	
	public ButtonLayout( int buttonSpace ) {
		super();
		setButtonSpace( buttonSpace );
	}
	
	public void addLayoutComponent(Component comp, Object constraints) {}

	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	public void invalidateLayout(Container target) {}
	public void addLayoutComponent(String name, Component comp) {}
	public void removeLayoutComponent(Component comp) {}

	public Dimension preferredLayoutSize( Container parent ) {
		Dimension ref = null;
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			JComponent c = ( JComponent )parent.getComponent( i );
			if ( ref == null )
				ref = c.getPreferredSize();
			else {
				Dimension d = c.getPreferredSize();
				if ( d.width > ref.width || 
						d.height > ref.height )
					ref = d;
			}
		}
		if ( ref != null )
			ref.height += 10;
		else
			return new Dimension( 0, 0 );
		return ref;
	}

	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize( parent );
	}

	private int buttonSpace = 0;
	
	public void setButtonSpace( int buttonSpace ) {
		this.buttonSpace = buttonSpace;
	}

	public void layoutContainer(Container parent) {
		int startX = parent.getWidth() - 5 - buttonSpace;
		int startY = 0;
		int startX2 = 5 + buttonSpace;

		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			JComponent comp = ( JComponent )parent.getComponent( i );
			Dimension ps = comp.getPreferredSize();
			startY = ( parent.getHeight() - ps.height ) / 2;
			if ( !( comp instanceof LeftOrientedButton ) ) {
				startX -= ( ps.getWidth() + buttonSpace );
				comp.setBounds( startX, startY + 1, ps.width, ps.height );
			} else {
				comp.setBounds( startX2, startY + 1, ps.width, ps.height );				
				startX2 += ps.getWidth() + buttonSpace;
			}
		}
	}

}
