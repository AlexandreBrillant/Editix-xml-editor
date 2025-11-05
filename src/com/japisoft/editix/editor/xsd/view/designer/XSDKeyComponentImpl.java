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

package com.japisoft.editix.editor.xsd.view.designer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

public class XSDKeyComponentImpl extends XSDAbstractComponentImpl {

	static final ImageIcon KEY_ICON = new ImageIcon(
			XSDSequenceComponentImpl.class.getResource("key.png"));

	protected ImageIcon mainImage;
	
	public XSDKeyComponentImpl() {
		super();
		hasOpenIcon = false;
		mainImage = KEY_ICON;
		paintElementName = false;
		paintName = false;
	}	
	
	protected void paintSpecialContent( Graphics2D g ) {
		int w = getWidth();
		int h = getHeight();

		g.drawImage( mainImage.getImage(), 
				( ( w - mainImage.getIconWidth() ) / 2 ),
				( ( h - mainImage.getIconHeight() ) / 2 ), null );
	}	
	
	protected Shape createBorderShape() {

		return new Ellipse2D.Double(
			1,
			1,
			getWidth() - 2,
			getHeight() - 2
		);

	}

	public Dimension getPreferredSize() {
		return new Dimension( 30, 30 );
	}

	public Rectangle getVisibleBounds() {
		return new Rectangle( 0, 0, 30, 30 );
	}
	
}

