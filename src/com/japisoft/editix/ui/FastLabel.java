package com.japisoft.editix.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

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
public class FastLabel extends JComponent implements MouseListener {
	String text;
	private boolean border;

	public FastLabel( boolean border ) {
		this.border = border;
	}

	private boolean center;

	public FastLabel( boolean border, boolean center ) {
		this( border );
		this.center = center;
	}

	boolean error;
	private boolean message;
	
	public FastLabel( boolean border, boolean center, boolean error ) {
		this( border, center );
		this.error = error;
	}

	public void setMessage( String message ) {
		this.message = true;
		showText( message );
	}

	public void setMessageMode( boolean messageMode ) {
		this.message = messageMode;
	}

	public void setText( String text ) {
		message = false;
		showText( text );
	}

	private void showText( String text ) {
		this.text = text;
		if ( center  && text != null ) {
			x = 2 + ( getWidth() - getFontMetrics( getFont() ).stringWidth( text ) ) / 2;
		}	
		if ( !error ) {
			setToolTipText( text );
			
			if ( text != null ) {
				FontMetrics fm = getFontMetrics( getFont() );		
				int width = fm.stringWidth( text );
				int limit = (int)( getWidth() * 0.9 );
				if ( width > limit ) {
					int nbchr = (int)( ( (double)limit / (double)width ) * text.length() );
					try {
						this.text = text.substring( 0, nbchr - 3 ) + "...";
					} catch( StringIndexOutOfBoundsException exc ) {

					}
				}
			}
		}
		
		repaint();
	}
	
	public String getText() {
		return text;
	}

	private Action action;
	
	public void setAction( Action action ) {
		this.action = action;
	}
	
	int x = 4;
	int hc = 0;
	boolean popupMode;
	private Polygon polygon = null;
	int lastSize = 0;	
	
	public void setPopupMode( boolean popupMode ) {
		this.popupMode = popupMode;
	}
	
	private Color DARKRED = Color.RED.darker();
	
	private Icon icon;

	public void setIcon( Icon icon ) {
		this.icon = icon;
		repaint();
	}

	private String errorNumber = null;
	
	public void setErrorNumber( String errorNumber ) {
		this.errorNumber = errorNumber;
	}
	
	
	private static Color DEF = new Color( 160, 160, 160 );
	
	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );
		
		Graphics2D g2 = ( Graphics2D )gc;

		g2.setRenderingHint( 
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
			);		
		
		if ( border ) {
			gc.setColor( DEF );
			gc.drawRect( 2, 2, getWidth() - 4, getHeight() - 4 );
		}
		if ( icon != null ) {
			icon.paintIcon( this, gc, 3, 3 );
 		}

		if ( text != null ) {
			if ( message ) 
				gc.setColor( Color.BLUE );
			else
				gc.setColor( error ? Color.RED : Color.BLACK );

			FontMetrics fm = getFontMetrics( getFont() );		
			
			gc.drawString( text, x,  hc == 0 ? ( hc = ( getHeight() - fm.getHeight() ) / 2 + fm.getAscent() ) : hc );

			if ( popupMode ) {
				int s = (int)( getWidth() * 0.9 );
				if ( getWidth() != lastSize || polygon == null ) {
					polygon = new Polygon(
					 new int[] {
					 		s,
							s + 4,
							s + 8 
					 },
					 new int[] {
					 		getHeight() - 5,
							5,
							getHeight() - 5
					 }, 3
					);
									
				}
				gc.setColor( DARKRED );
				gc.fillPolygon( polygon );
				
				if ( errorNumber != null ) {
					gc.drawString( errorNumber, s + 10, hc == 0 ? ( hc = fm.getHeight() ) : hc );
				}
				
				lastSize = getWidth();
			}
		}
	}
	
	public void invalidate() {
	}
	public void validate() {
	}
	public void layout() {
	}	

	/////////////////////////////////////////

	public void addNotify() {
		super.addNotify();
		addMouseListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		removeMouseListener( this );
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ( action != null )
			action.actionPerformed( new ActionEvent(this,0, "" ) );
	}

	public void mouseReleased(MouseEvent e) {
	}
	
}
