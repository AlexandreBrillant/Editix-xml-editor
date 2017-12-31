package com.japisoft.framework.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

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
public class VerticalCenterLayout implements LayoutManager2 {

	public VerticalCenterLayout() {}

	public VerticalCenterLayout( int dividerLayout ) {
		setDividerSize( dividerLayout );
	}

	public void addLayoutComponent(
			Component comp, 
			Object constraints ) {
	}

	public void addLayoutComponent(
			String name,
			Component comp ) {
	}

	public float getLayoutAlignmentX( Container target ) {
		return 0;
	}

	public float getLayoutAlignmentY( Container target ) {
		return 0;
	}

	public void invalidateLayout( Container target ) {
	}

	public void layoutContainer( Container parent ) {
		int width = parent.getWidth();
				
		int y = dividerSize;

		Insets insets = parent.getInsets();
		if ( insets != null )
			y += insets.top;
		
		if ( inset != null )
			y = inset.bottom;

		int maxWidth = 0;
		
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c = parent.getComponent( i );
			Dimension dim = c.getPreferredSize();
			maxWidth = Math.max( dim.width, maxWidth );
		}

		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c = parent.getComponent( i );
			Dimension dim = c.getPreferredSize();
			c.setBounds(
					( ( width - maxWidth ) / 2 ) + ( insets != null ? insets.left : 0 ),
					y,
					maxWidth - ( insets != null ? ( insets.left + insets.right ) : 0 ),
					dim.height );
			y += ( dim.height ) + dividerSize;
		}
	}

	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	public Dimension minimumLayoutSize(Container parent) {
		return null;
	}

	public Dimension preferredLayoutSize(Container parent) {
		int width = parent.getWidth();
		int y = dividerSize;
		if ( inset != null )
			y = inset.bottom;

		Insets insets = parent.getInsets();
		if ( insets != null )
			y += insets.top;
		
		int maxwidth = 0;
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c = parent.getComponent( i );
			Dimension dim = c.getPreferredSize();
			maxwidth = Math.max( maxwidth, dim.width + ( insets != null ? ( insets.left + insets.right ) : 0 ) );
			y += ( dim.height ) + dividerSize;
		}
		if ( inset == null )
			return new Dimension( maxwidth, y - dividerSize );
		else
			return new Dimension( maxwidth + inset.left + inset.right, y + inset.top - dividerSize );
	}

	private Insets inset;

	public void setInset( Insets inset ) {
		this.inset = inset;
	}

	private int dividerSize = 0;

	public void setDividerSize( int size ) {
		this.dividerSize = size;
	}
	
	public void removeLayoutComponent(Component comp) {}

	public static void main( String[] args ) {
		JFrame fr = new JFrame();
		VerticalCenterLayout layout = new VerticalCenterLayout();
		JPanel panel = new JPanel( layout );
		//layout.setInset( new Insets( 10, 10, 10, 30 ) );
		layout.setDividerSize( 20 );
		fr.getContentPane().add( new JScrollPane( panel ) );
		ButtonGroup bg = new ButtonGroup();
		ImageIcon icon = new ImageIcon( ClassLoader.getSystemResource( "images/bug_red.png" ) );
		for ( int i = 0; i < 10; i++ ) {
			JToggleButton btn = new JToggleButton( "OK" + i );
			btn.setVerticalAlignment( SwingConstants.BOTTOM );
			btn.setHorizontalAlignment( SwingConstants.CENTER );
			btn.setVerticalTextPosition( SwingConstants.BOTTOM );
			btn.setHorizontalTextPosition( SwingConstants.CENTER );
			btn.setIcon( icon );
			bg.add( ( AbstractButton )panel.add( btn ) );
		}
		fr.setBounds( 0, 0, 50, 150 );
		fr.setVisible( true );
	}

}
