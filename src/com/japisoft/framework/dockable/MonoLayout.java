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

package com.japisoft.framework.dockable;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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

