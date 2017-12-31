package com.japisoft.multipanes;

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
class MultiPanesLayout implements LayoutManager2 {

	MultiPanes pane;
/*
	static {
		char[] _ = new char[ 82];
		_[ 0]=77;
		_[ 1]=117;
		_[ 2]=108;
		_[ 3]=116;
		_[ 4]=105;
		_[ 5]=112;
		_[ 6]=97;
		_[ 7]=110;
		_[ 8]=101;
		_[ 9]=115;
		_[ 10]=32;
		_[ 11]=45;
		_[ 12]=32;
		_[ 13]=51;
		_[ 14]=48;
		_[ 15]=32;
		_[ 16]=68;
		_[ 17]=97;
		_[ 18]=121;
		_[ 19]=32;
		_[ 20]=69;
		_[ 21]=118;
		_[ 22]=97;
		_[ 23]=108;
		_[ 24]=117;
		_[ 25]=97;
		_[ 26]=116;
		_[ 27]=105;
		_[ 28]=111;
		_[ 29]=110;
		_[ 30]=32;
		_[ 31]=86;
		_[ 32]=101;
		_[ 33]=114;
		_[ 34]=115;
		_[ 35]=105;
		_[ 36]=111;
		_[ 37]=110;
		_[ 38]=10;
		_[ 39]=40;
		_[ 40]=99;
		_[ 41]=41;
		_[ 42]=32;
		_[ 43]=50;
		_[ 44]=48;
		_[ 45]=48;
		_[ 46]=52;
		_[ 47]=32;
		_[ 48]=74;
		_[ 49]=65;
		_[ 50]=80;
		_[ 51]=73;
		_[ 52]=83;
		_[ 53]=111;
		_[ 54]=102;
		_[ 55]=116;
		_[ 56]=32;
		_[ 57]=58;
		_[ 58]=32;
		_[ 59]=104;
		_[ 60]=116;
		_[ 61]=116;
		_[ 62]=112;
		_[ 63]=58;
		_[ 64]=47;
		_[ 65]=47;
		_[ 66]=119;
		_[ 67]=119;
		_[ 68]=119;
		_[ 69]=46;
		_[ 70]=106;
		_[ 71]=97;
		_[ 72]=112;
		_[ 73]=105;
		_[ 74]=115;
		_[ 75]=111;
		_[ 76]=102;
		_[ 77]=116;
		_[ 78]=46;
		_[ 79]=99;
		_[ 80]=111;
		_[ 81]=109;
		System.out.println( new String( _ ) );
	}
*/

	public MultiPanesLayout( MultiPanes pane ) {
		this.pane = pane;
	}

	public void addLayoutComponent(Component comp, Object constraints) {
	}

	public Dimension maximumLayoutSize(Container target) {
		return pane.getMaximumSize();
	}

	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

	public void invalidateLayout(Container target) {
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension( 100, 100 );
	}

	public Dimension minimumLayoutSize(Container parent) {
		return pane.getMinimumSize();
	}

	private int inset;
	
	public void setVerticalInset( int inset ) {
		this.inset = inset;
	}
	
	public int getVerticalInset() {
		return inset;
	}

	public void layoutContainer(Container parent) {
		TitledPane tp = pane.getOpenedTitledPane();
		JComponent toExpand = null;
		
		int expandedHeight = inset;

		if ( tp != null ) {
			toExpand = tp.getView();

			for ( int i = 0; i < parent.getComponentCount(); i++ ) {
				Component c = parent.getComponent( i );
				if ( c != toExpand ) {
					expandedHeight += c.getPreferredSize().height + inset;
				}
			}			
			
			expandedHeight = Math.max( 0, parent.getHeight() - expandedHeight + inset );
		}

		int y = inset;

		TitledPaneModel model = pane.getModel();

		for ( int i = 0; i < model.getTitledPaneCount(); i++ ) {

			Component c = parent.getComponent( i );

			Dimension d = c.getPreferredSize();
			c.setBounds( 0, y, parent.getWidth(), d.height );
			y += ( d.height );

			if ( model.getTitledPaneAt( i ) == tp ) {
				c = toExpand;
				c.setBounds( 0, y, parent.getWidth(), expandedHeight );
				y += expandedHeight;			
			} else
				y += inset;

		}
	}

}
