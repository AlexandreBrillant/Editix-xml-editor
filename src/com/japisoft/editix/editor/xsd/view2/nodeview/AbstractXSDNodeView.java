package com.japisoft.editix.editor.xsd.view2.nodeview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.editix.editor.xsd.view2.node.XSDNode;
import com.japisoft.framework.preferences.Preferences;

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
public class AbstractXSDNodeView implements XSDNodeView {

	protected XSDNode node;
	protected BufferedImage buffer = null;
	protected int hpadding = 10;
	protected int vpadding = 5;

	private final static Color DISABLED_COLOR = new Color( 220,220,220 );
	
	public AbstractXSDNodeView( XSDNode node ) {
		this.node = node;
	}

	@Override
	public int getFullHeight( View designer ) {
		int height = getBuffer( designer ).getHeight();
		if ( node.getChildCount() > 0 ) {
			int childHeight = 0;
			for ( int i = 0; i < node.getChildCount(); i++ ) {
				if ( i > 0 ) {
					childHeight += 10;
				}
				childHeight += node.getChildAt( i ).getView().getFullHeight( designer );
			}			
			height = Math.max( height, childHeight );
		}
		return height;
	}

	@Override
	public int getHeight(View designer) {
		return getBuffer( designer ).getHeight();
	}
	
	@Override
	public int getWidth( View designer ) {
		return getBuffer( designer ).getWidth();
	}
	
	@Override
	public void paint( Graphics2D gc ) {
		if ( buffer != null )
			gc.drawImage( buffer, 0, 0, null );
	}

	@Override
	public void invalidateBuffer() {
		buffer = null;
	}

	private BufferedImage getBuffer( View designer ) {
		if ( buffer == null ) {
			createBuffer( designer );
		}
		return buffer;
	}
	
	protected BufferedImage createBufferedImage( int width, int height ) {
		return new BufferedImage(
			width + ( 2 * hpadding ),
			height + ( 2 * vpadding ),
			BufferedImage.TRANSLUCENT
		);
	}

	protected boolean isOptional() {
		Element e = node.getDOM();
		if ( e.hasAttribute( "minOccurs" ) ) {
			if ( "0".equals( e.getAttribute( "minOccurs" ) ) ) {
				return true;
			}	
		}
		return false;
	}

	// For occurences
	public void setBorder( Graphics2D gc ) {
		if ( isOptional() ) {
			// Dotted
			gc.setStroke(
				new BasicStroke(
					1, 
					BasicStroke.CAP_BUTT, 
					BasicStroke.JOIN_MITER, 
					3, 
					new float[]{3}, 
					0
				) 
			);
		}
	}
	
	protected Color getEnabledColor() {
		if ( node.isEnabled() ) {
			return ( Color.WHITE );
		} else {
			return ( DISABLED_COLOR );
		}		
	}
	
	public Color getSelectionColor() {
		return Preferences.getPreference( 
			"xsdEditor", 
			"selectionBackground", 
			new Color( 134, 150, 232 ) 
		);
	}

	protected void createBuffer( View designer ) {
		Graphics2D gc = ( Graphics2D )designer.getView().getGraphics();
		int width = gc.getFontMetrics().stringWidth( node.toString() );
		int height = gc.getFontMetrics().getHeight();

		buffer = createBufferedImage(
			width,
			height 
		);

		Graphics2D bgc = ( Graphics2D )buffer.getGraphics();

		if ( node.isSelected() ) {
			bgc.setColor( getSelectionColor() );
		} else {				
			bgc.setColor( getEnabledColor() );
		}

		bgc.fillRect( 0, 0, buffer.getWidth(), buffer.getHeight() );		

		bgc.setColor( Color.BLACK );
		bgc.setFont( gc.getFont() );
		bgc.drawString( node.toString(), hpadding, buffer.getHeight() - vpadding - gc.getFontMetrics().getDescent() );

		setBorder( bgc );

		bgc.drawRect( 0, 0, buffer.getWidth() - 2, buffer.getHeight() - 1 );
	}

}
