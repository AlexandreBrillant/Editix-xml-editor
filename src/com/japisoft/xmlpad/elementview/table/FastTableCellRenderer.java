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

package com.japisoft.xmlpad.elementview.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.*;

/**
 * Renderer for the element view table
 * <pre>UIManager properties :
 *  -xmlpad.tableElementView.font
 *  -xmlpad.tableElementView.prefixNameColor
 *  -xmlpad.tableElementView.highlightColor
 *  -xmlpad.tableElementView.lowlightColor</pre>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
class FastTableCellRenderer extends JComponent implements TableCellRenderer {


	private int textY;
	FontMetrics fm;

	public FastTableCellRenderer() {
		super();
		setFont( UIManager.getFont( "xmlpad.tableElementView.font" ) );		
		setColorForPrefixName( UIManager.getColor( "xmlpad.tableElementView.prefixNameColor" ) );
		setHighlightColor( UIManager.getColor( "xmlpad.tableElementView.highlightColor" ) );
		setLowlightColor( UIManager.getColor( "xmlpad.tableElementView.lowlightColor" ) );
		setDefaultTextColor();
	}

	private Font defaultFont = null;
	private Font boldFont = null;
	
	public void setFont( Font font ) {
		if ( font == null )
			font = new Font( null, 0, 14 );
		this.defaultFont = font;
		super.setFont( font );
		fm = getFontMetrics( font );
		textY = fm.getAscent();
		boldFont = font.deriveFont( Font.BOLD );
	}
	
	private boolean boldMode = false;
	
	public void setBoldMode( boolean boldMode ) {
		this.boldMode = boldMode;
		if ( boldMode )
			super.setFont( boldFont );
		else
			super.setFont( defaultFont );
	}

	private Color prefixNameColor;

	/** Reset the color for the prefix and name part */
	public void setColorForPrefixName( Color color ) {
		if ( color == null )
			color = new Color( 230, 230, 230 );
		this.prefixNameColor = color;
	}

	private Color highlightColor = null;

	public void setHighlightColor( Color color ) {
		if  (color == null )
			color = new Color( 255, 255, 255 );
		this.highlightColor = color;
	}

	private Color lowlightColor = null;

	public void setLowlightColor( Color color ) {
		if ( color == null )
			color = new Color( 240, 240, 240 );
		this.lowlightColor = color;
	}

	String content = null;
	int row = 0;
  
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column) {
		content = ( String )value;
		this.row = row;
		
		setBoldMode(  column == 0 );
		
		if  ( row < 2 )
			setBackground( prefixNameColor );
		else
			setBackground( row % 2 == 0 ? highlightColor : lowlightColor );
		
		return this;
	}

	private Color textColor = null;
	
	public void setTextColor( Color c ) {
		this.textColor = c;
	}
	
	public void setDefaultTextColor() {
		setTextColor( UIManager.getColor( "xmlpad.tableElementView.foreground" ) );
	}

	public Color getTextColor() {
		if ( this.textColor == null )
			this.textColor = Color.black;
		return this.textColor;
	}

	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );

/*

		Graphics2D g2 = ( Graphics2D )gc;
		g2.setRenderingHint( 
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
			);				
*/

		gc.setColor( getBackground() );
		gc.fillRect( 0, 0, getWidth(), getHeight() );

		if ( content != null ) {
			gc.setColor( getTextColor() );			
			gc.drawString( content, 0, textY );
		}
	}
	
	// For optimizing rendering
	public boolean isDoubleBuffered() {
		return false;
	}
	// For optimizing rendering		
	public void invalidate() {
	}
	// For optimizing rendering		
	public void validate() {
	}
	// For optimizing rendering		
	public void repaint() {
	}

}

