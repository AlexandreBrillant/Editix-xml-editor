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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.UIManager;

import com.japisoft.stylededitor.EditorByCSS;
import com.japisoft.stylededitor.model.NodeElement;

public class TextElementView extends NodeElementView {

	private Color defaultTextColor = Color.BLACK;
	
	public TextElementView() {
		super();
		if ( UIManager.getColor( "editor.css.foreground" ) != null )
			defaultTextColor = UIManager.getColor( "editor.css.foreground" );
	}
	
	
	private Dimension getSize( EditorByCSS editor, NodeElement element, String text, Graphics gc ) {
		Font f = getFont( element );
		FontMetrics fm = gc.getFontMetrics( f );
		int height = fm.getHeight();
		int width = 0;
		width = fm.stringWidth( text );
		Dimension dim = new Dimension( width, height );
		element.setData( "size", dim );
		return dim;
	}

	public Dimension getSize(EditorByCSS editor, NodeElement element, Graphics gc) {
		return getSize( editor, element, element.getName(), gc );		
	}

	@Override
	public void paint(EditorByCSS editor, NodeElement element, int x, int y, Graphics gc) {
		String text = element.getName();
		if ( text != null ) {
			Font f = getFont( element );
			FontMetrics fm = gc.getFontMetrics( f );
			gc.setFont( f );
			gc.setColor( ( Color )element.getCSSProperty( "color", defaultTextColor ) );
			( ( Graphics2D )gc ).setRenderingHint( 
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
			);
			gc.drawString( text, 0, fm.getAscent() );
			if ( isUnderline( element ) ) {
				gc.drawLine( 0, fm.getAscent(), fm.stringWidth( text ), fm.getAscent() );
			}
		}
	}

	@Override
	public Rectangle getBounds(EditorByCSS editor, NodeElement element, int offset, Graphics gc) {
		Point location = ( Point )( ( NodeElement )element ).getData( "location" );
		if ( location == null )
			return null;	// ?
		offset -= element.getStartOffset();
		Dimension dim = getSize( editor, element, element.getName().substring( 0, Math.min( offset, element.getName().length() ) ), gc );
		return new Rectangle( location, dim );
	}
	
	@Override
	public boolean isInside(EditorByCSS editor, NodeElement e, int x, int y, Graphics gc) {		
		if ( e instanceof NodeElement ) {
			NodeElement ne = ( NodeElement )e;
			Dimension dim = ( Dimension )ne.getData( "size" );
			Point loc = ( Point )ne.getData( "location" );
			if ( ( dim != null ) && 
					( loc != null ) ) {
				if ( x >= loc.x && 
						y >= loc.y &&  
								y <= loc.y + dim.height ) {
					if ( !isBlock( editor,e ) )
						if ( x > loc.x + dim.width )
							return false;
					return true;	
				}
			}
		}		
		return false;
	}

	@Override
	public int getOffset(
			EditorByCSS editor, 
			NodeElement element, 
			int x, 
			int y, 
			Graphics gc ) {
		
		String text = element.getName();
		Point location = ( Point )( ( NodeElement )element ).getData( "location" );
		if ( location == null )
			location = new Point( 0, 0 );
		
		for ( int i = 1; i <= text.length(); i++ ) {
			Dimension dim = getSize( editor, element, text.substring( 0, i ), gc );
			if ( location.x + dim.width >= x )
				return ( i - 1 );
		}

		return element.getEndOffset() - element.getStartOffset();
	}

}
