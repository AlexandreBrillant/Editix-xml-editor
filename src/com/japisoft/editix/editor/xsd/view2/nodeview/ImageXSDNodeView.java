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

package com.japisoft.editix.editor.xsd.view2.nodeview;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.editix.editor.xsd.view2.node.XSDNode;

public class ImageXSDNodeView extends AbstractXSDNodeView {
	
	private String urlImage;
	
	public ImageXSDNodeView( XSDNode node, String urlImage ) {
		super( node );
		this.urlImage = urlImage;
		hpadding = 10;
		vpadding = 8;
	}
	
	@Override
	protected void createBuffer(View designer) {
		ImageIcon ii = new ImageIcon(
			getClass().getResource( urlImage )
		);
		buffer = createBufferedImage( 
			ii.getIconWidth(), 
			ii.getIconHeight() 
		);
		Graphics2D g2d = ( Graphics2D )buffer.getGraphics();
		
		if ( !node.isEnabled() || node.isReference() ) {
			g2d.setColor( DISABLED_COLOR );
		} else		
		if ( node.isSelected() ) {
			g2d.setColor( getSelectionColor() );
		} else {		
			g2d.setColor( getEnabledColor() );
		}

		g2d.fillRoundRect( 0, 0, buffer.getWidth(), buffer.getHeight(), 10, 10 );
		g2d.setColor( Color.BLACK );
		
		g2d.drawImage( 
			ii.getImage(), 
			hpadding, 
			vpadding, 
			null 
		);
		
		setBorder( g2d );
		g2d.setColor( BORDER_COLOR );
		g2d.drawRoundRect( 0, 0, buffer.getWidth() - 1, buffer.getHeight() - 1, 10, 10 );
		
	}

}

