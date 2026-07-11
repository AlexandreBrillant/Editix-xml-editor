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
