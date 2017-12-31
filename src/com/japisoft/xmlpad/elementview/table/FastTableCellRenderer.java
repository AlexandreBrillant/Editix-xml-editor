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
class FastTableCellRenderer extends JComponent implements TableCellRenderer {

	private boolean selected;
	private Dimension d = new Dimension(100, 25);
	private int textY;
	private int fmHeight;
	FontMetrics fm;

	public FastTableCellRenderer() {
		super();
		setFont(
			UIManager.getFont( "xmlpad.tableElementView.font" ) );		
		setColorForPrefixName(
			UIManager.getColor( "xmlpad.tableElementView.prefixNameColor" ) );
		setHighlightColor( 
			UIManager.getColor( "xmlpad.tableElementView.highlightColor" ) );
		setLowlightColor( 
			UIManager.getColor( "xmlpad.tableElementView.lowlightColor" ) );
	}

	public void setFont( Font font ) {
		if ( font == null )
			font = new Font( null, 0, 14 );
		super.setFont( font );
		fm = getFontMetrics( font );
		fmHeight = fm.getHeight() + 2;
		textY = fm.getAscent();
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
		this.selected = isSelected;
		this.row = row;
		
		if  ( row < 2 )
			setBackground( prefixNameColor );
		else
			setBackground( row % 2 == 0 ? highlightColor : lowlightColor );
		
		return this;
	}

	private Color textColor = Color.black;
	
	public void setTextColor( Color c ) {
		this.textColor = c;
	}

	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );

		Graphics2D g2 = ( Graphics2D )gc;

		g2.setRenderingHint( 
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
			);				

		gc.setColor( getBackground() );
		gc.fillRect( 0, 0, getWidth(), getHeight() );

		if ( content != null ) {
			gc.setColor( textColor );
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
