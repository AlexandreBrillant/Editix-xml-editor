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

package com.japisoft.stylededitor.ui.node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.japisoft.stylededitor.EditorByCSS;
import com.japisoft.stylededitor.model.NodeElement;

public class NodeElementViewProxy implements ElementView {

	private ElementView source = null;
	
	public NodeElementViewProxy( ElementView source ) {
		this.source = source;
	}
	
	public Rectangle getBounds(EditorByCSS editor, NodeElement element,
			int offset, Graphics gc) {
		return source.getBounds(editor, element, offset, gc );
	}

	public int getOffset(EditorByCSS editor, NodeElement element, int x, int y,
			Graphics gc) {
		return source.getOffset(editor, element, x, y, gc);
	}

	private Dimension getTagDimension( NodeElement element, Graphics gc ) {
		String tagName = element.getNode().getNodeName();
		FontMetrics fm = gc.getFontMetrics();		
		int width = fm.stringWidth( tagName ) + 2;
		int height= fm.getHeight() + 2;		
		return new Dimension( width, height );		
	}
	
	public Dimension getSize(EditorByCSS editor, NodeElement element,
			Graphics gc) {
		Dimension dim = source.getSize(editor, element, gc);
		
		Dimension tagDim = getTagDimension( element, gc );

		if ( dim == null ) {
			element.setData( "size", tagDim );
			return tagDim;
		}

		return new Dimension( dim.width + tagDim.width, dim.height + tagDim.height );
	}

	public boolean isInside(EditorByCSS editor, NodeElement element, int x,
			int y, Graphics gc) {
		return source.isInside(editor, element, x, y, gc);
	}
	
	private Color TAG_COLOR = new Color( 255, 204, 102 );

	public void paint(EditorByCSS editor, NodeElement element, int x, int y, Graphics gc) {
		Dimension tagDim = getTagDimension( element, gc );
		
		source.paint(editor, element, x, y + tagDim.height + 1, gc);
		
		element.setData( "tag.x", x );
		element.setData( "tag.y", y );
		element.setData( "tag.width", tagDim.width );
		element.setData( "tag.height", tagDim.height );

		gc.setColor( TAG_COLOR );
		gc.fill3DRect( 0, 0, tagDim.width, tagDim.height, true );
		gc.setColor( Color.BLACK );
		( ( Graphics2D )gc ).setRenderingHint( 
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);

		gc.drawString( element.getNode().getNodeName(), 1, gc.getFontMetrics().getAscent() + 1 );
	}

}
