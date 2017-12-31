package com.japisoft.framework.dockable;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

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
class MonoLayout implements LayoutManager {

	public void addLayoutComponent( String name, Component comp ) {
	}

	public void removeLayoutComponent( Component comp ) {
	}

	public Dimension preferredLayoutSize( Container parent ) {
		if ( parent.getComponentCount() > 0 )
			return parent.getComponent( 0 ).getPreferredSize();
		return parent.getPreferredSize();
	}

	public Dimension minimumLayoutSize( Container parent ) {
		if ( parent.getComponentCount() > 0 )
			return parent.getComponent( 0 ).getMinimumSize();
		return parent.getMinimumSize();
	}

	public void layoutContainer( Container parent ) {
		if ( parent.getComponentCount() > 0 ) {
			parent.getComponent( 0 ).setBounds(
					1, 
					1,
					parent.getWidth() - 2,
					parent.getHeight() - 2 );
		}
	}

}
