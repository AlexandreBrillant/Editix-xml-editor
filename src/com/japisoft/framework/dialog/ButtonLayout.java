package com.japisoft.framework.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import javax.swing.JComponent;

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
