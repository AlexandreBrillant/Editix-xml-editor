package com.japisoft.framework.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;

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
public class FastLabel extends JComponent implements PropertyChangeListener {
	private String text;
	private boolean border;

	public FastLabel() {
		super();
		setForeground( UIManager.getColor( "Label.foreground" ) );
		setBackground( UIManager.getColor( "Label.background" ) );
		setFont( UIManager.getFont( "Label.font" ) );
		setOpaque( true );
		addPropertyChangeListener( this );
	}

	public FastLabel( boolean withBorder ) {
		this();
		this.border = withBorder;
	}

	private boolean center;

	public FastLabel( boolean withBorder, 
			boolean center ) {
		this( withBorder );
		this.center = center;
	}

	private boolean error;

	public FastLabel( boolean withBorder, 
			boolean center, 
			boolean error ) {
		this( withBorder, center );
		this.error = error;
	}

	public void removeNotify() {
		super.removeNotify();
		removePropertyChangeListener( this );
	}	

	public void propertyChange(PropertyChangeEvent evt) {
		if ( "font".equals( evt.getPropertyName() ) )
			computeInnerSize();
	}	

	public void setText( String text ) {
		this.text = text;
		computeInnerSize();
		if ( error )
			setToolTipText( text );	
		repaint();
	}

	public String getText() {
		return text;
	}

	private Icon icon;
	
	public void setIcon( Icon icon ) {
		this.icon = icon;
		computeInnerSize();
	}

	public Icon getIcon() {
		return icon;
	}

	int xtext = 4;
	int xicon = 0;
	int hc = 0;

	boolean underlineMode = false;
	
	public void setUnderlineMode( boolean underline ) {
		this.underlineMode = underline;
	}

	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );
		

		Graphics2D g2d = ( Graphics2D )gc;
		g2d.setRenderingHint( 
			RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);		
		
		
		if ( isOpaque() ) {
			gc.setColor( getBackground() );
			gc.fillRect( 0, 0, getWidth(), getHeight() );
		}

		if ( border ) {
			gc.setColor( Color.GRAY );
			gc.draw3DRect( 2, 2, getWidth() - 4, getHeight() - 4, true );
		}
		if ( icon != null ) {
			icon.paintIcon( this, gc, 0, 0 );
		}
		if ( text != null ) {
			gc.setColor( error ? Color.RED : getForeground() );
			gc.drawString( text, xtext,  hc );
		}

		if ( underlineMode ) {
			if ( underlineColor != null )
				gc.setColor( underlineColor );
			gc.drawLine( xtext, hc + 2, preferredSize.width, hc + 2 );
		}
	}

	private Color underlineColor = null;
	
	public void setUnderlineColor( Color color ) {
		this.underlineColor = color;
	}

	Dimension preferredSize = null;

	public Dimension getPreferredSize() {
		if ( preferredSize == null )
			return super.getPreferredSize();
		return preferredSize;
	}

	private void computeInnerSize() {
		if ( getFont() == null )
			return;
		FontMetrics metrics = getFontMetrics( getFont() );		
		int fw = 0;
		xtext = 0;
		
		if ( text != null )
			fw = metrics.stringWidth( text );

		if ( center && 
				text != null ) {
			xtext = 2 + ( getWidth() - ( fw / 2 ) );
		}	
		
		if ( icon != null )
			xtext += ( icon.getIconWidth() + 2 );

		hc = Math.max( metrics.getAscent(), icon != null ? icon.getIconHeight() : 0 ) + 2;
		int width = fw;
		
		preferredSize = new Dimension( xtext + fw, hc );

		hc = ( icon != null ? hc - ( icon.getIconHeight() - metrics.getDescent() ) / 2 : hc );
	}

	public void invalidate() {
	}
	public void validate() {
	}
	public void layout() {
	}

}
