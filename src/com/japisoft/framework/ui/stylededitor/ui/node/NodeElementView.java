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

package com.japisoft.framework.ui.stylededitor.ui.node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import org.w3c.dom.Node;

import com.japisoft.framework.css.CSSBorder;
import com.japisoft.framework.css.CSSDim;
import com.japisoft.framework.css.FontParser;
import com.japisoft.framework.ui.stylededitor.EditorByCSS;
import com.japisoft.framework.ui.stylededitor.model.NodeElement;
import com.japisoft.framework.ui.stylededitor.model.TextElement;

public class NodeElementView implements ElementView {

	public static final Rectangle NO_MARGIN = new Rectangle( 0, 0, 0, 0 );
	
	protected Rectangle getMargin( NodeElement element ) {
		
		// Cache result
		Rectangle margin = ( Rectangle )element.getData( "margin" );

		if ( margin == null ) {
			
			int marginLeft = 0;
			int marginRight = 0;
			int marginTop = 0;
			int marginBottom = 0;

			CSSDim tmpDim = ( CSSDim )element.getCSSProperty( "margin", CSSDim.ZERO );
			marginLeft = tmpDim.getValue();
			marginRight = tmpDim.getValue();
			marginTop = tmpDim.getValue();
			marginBottom = tmpDim.getValue();			

			marginLeft = ( ( CSSDim )element.getCSSProperty( "margin-left", new CSSDim( marginLeft, false ) ) ).getValue();
			marginRight = ( ( CSSDim )element.getCSSProperty( "margin-right", new CSSDim( marginRight, false ) ) ).getValue();
			marginTop = ( ( CSSDim )element.getCSSProperty( "margin-top", new CSSDim( marginTop, false ) ) ).getValue();
			marginBottom = ( ( CSSDim )element.getCSSProperty( "margin-bottom", new CSSDim( marginBottom, false ) ) ).getValue();			

			if ( marginLeft + marginRight + marginTop + marginBottom == 0 )
				element.setData( "margin", margin = NO_MARGIN );
			else
				element.setData( "margin", margin = new Rectangle( marginLeft, marginTop, marginRight, marginBottom ) );

		}

		return margin;

	}
	
	protected boolean isUnderline( NodeElement element ) {

		if ( element instanceof TextElement ) {
			NodeElement parent = ( NodeElement )element.getNode().getParentNode().getUserData( "element" );
			if ( parent != null )
				element = parent;
		}

		return "underline".equalsIgnoreCase( 
			( String )element.getCSSProperty( "text-decoration", null ) 
		);

	}

	protected Font getFont( NodeElement element ) {

		if ( element instanceof TextElement ) {
			NodeElement parent = ( NodeElement )element.getNode().getParentNode().getUserData( "element" );
			if ( parent != null )
				element = parent;
		}
		
		Font f = ( Font )element.getData( "font" );
		if ( f != null )
			return f;

		// Build the font
		f = ( Font )element.getCSSProperty( "font-family", FontParser.getInstance().getDefaultFont() );
		int fontSize = ( Integer )element.getCSSProperty( "font-size", 14 );
		f = f.deriveFont( ( float )fontSize );

		String style = ( String )element.getCSSProperty( "font-style", null );
		if ( style != null ) {
			if ( "italic".equalsIgnoreCase( style ) || 
					"oblique".equalsIgnoreCase( style )) {
				f = f.deriveFont( Font.ITALIC );
			}
		}

		String fontWeight = 
			( String )element.getCSSProperty( "font-weight", null );
		
		if ( "bold".equalsIgnoreCase( fontWeight ) ) {
			f = f.deriveFont( f.getStyle() | Font.BOLD );
		}

		element.setData( "font", f );
		
		return f;
	}

	public void paint( EditorByCSS editor, NodeElement element, int x, int y, Graphics gc ) {
		renderer( editor, element, x, y, gc, true );
	}

	private void renderer( EditorByCSS editor, NodeElement element, int x, int y, Graphics gc, boolean paintMode ) {
		
		Node n = element.getNode();		
		Node child = n.getFirstChild();
		if ( child == null ) {
			return;
		}

		int xi = 0, yi = 0;
		int width = 0, height = 0;

		Point p = ( Point )n.getUserData( "location" );
		if ( p != null ) {
			xi = p.x;
			yi = p.y;
		}
		
		do {

			NodeElement ne = ( NodeElement )child.getUserData( "element" );
			
			if ( ne != null ) {
				
				ElementView view = 
					ElementViewFactory.getInstance().getView( editor, ne );

				if ( view == null ) {
					continue;
				}

				Dimension dim = view.getSize( editor, ne, gc );
				if ( dim == null )
					dim = new Dimension(0,0);
				
				if ( isBlock( editor, ne ) ) {
					
					CSSDim cssWidth = ( CSSDim )ne.getCSSProperty( "width", null );
					
					if ( cssWidth != null ) {
						
						int physicalWidth = 0;
						
						if ( cssWidth.isPercent() ) {

							physicalWidth = ( editor.getWidth() * cssWidth.getValue() ) / 100;
							
						} else
							
							physicalWidth = cssWidth.getValue();

						dim.width = physicalWidth;						

					}

					CSSDim cssHeight = ( CSSDim )ne.getCSSProperty( "height", null );
					if ( cssHeight != null ) {
						
						int physicalHeight = 0;
						
						if ( cssHeight.isPercent() ) {

							physicalHeight = ( editor.getHeight() * cssHeight.getValue() ) / 100;
							
						} else
							
							physicalHeight = cssHeight.getValue();

						dim.height = physicalHeight;						

					}					

				} else {
					
					// Foce width for inline usage
					
					CSSDim cssWidth = ( CSSDim )ne.getCSSProperty( "width", null );
					
					if ( cssWidth != null ) {
						
						int physicalWidth = 0;
						
						if ( cssWidth.isPercent() ) {

							physicalWidth = ( editor.getWidth() * cssWidth.getValue() ) / 100;
							
						} else
							
							physicalWidth = cssWidth.getValue();

						dim.width = physicalWidth;						

					}					
					
					
				}
				
				int leftMargin = 0;
				int topMargin = 0;
				int rightMargin = 0;
				int bottomMargin = 0;
				
				if ( ne.isDOMElement() ) {
					Rectangle margin = getMargin( ne );
					leftMargin = margin.x;
					topMargin = margin.y;
					rightMargin = margin.width;
					bottomMargin = margin.height;					
				}

				ne.setData( 
					"location", 
					new Point(
						xi + x + leftMargin,
						yi + y + topMargin
					)
				);

				width = Math.max( dim.width, width ) + leftMargin + rightMargin;
				height = Math.max( dim.height, height ) + topMargin + bottomMargin;

				if ( paintMode ) {
					
					int gcWidth = dim.width + leftMargin + rightMargin;
					int gcHeight = dim.height + topMargin + bottomMargin;
					
					Graphics gc2 = gc.create(
						x + leftMargin,
						y + topMargin,
						gcWidth,
						gcHeight
					);

					Color background = 
						( Color )( element.getCSSProperty( "background-color", null ) );

					if ( background != null ) {
						gc2.setColor( background );
						gc2.fillRect( 0, 0, dim.width, dim.height );
					}

					paintBorder( element, gcWidth, gcHeight, gc );
					
					view.paint( editor, ne, 0, 0, gc2 );

				}

				if ( isBlock( editor, ne ) ) {
					y += dim.height + topMargin + bottomMargin;
				} else {
					x += dim.width + leftMargin + rightMargin;
				}

			}

			child = child.getNextSibling();

		} while ( child != null );

		element.setData( "size", new Dimension( Math.max( x, width ), Math.max( y, height ) ) );
	}

	private void paintBorder( NodeElement element, int width, int height, Graphics gc ) {
		
		width--;
		height--;	//?
		
		CSSBorder border = ( CSSBorder )element.getCSSProperty( "border", null );
		if ( border != null ) {
			CSSBorder leftBorder = border.getSide( "border-left" );
			CSSBorder rightBorder = border.getSide( "border-right" );
			CSSBorder topBorder = border.getSide( "border-top" );
			CSSBorder bottomBorder = border.getSide( "border-bottom" );
			
			if ( !leftBorder.isInvisible() ) {
				if ( leftBorder.getColor() != null )
					gc.setColor( leftBorder.getColor() );
				gc.drawLine( 0, 0, 0, height );
			}

			if ( !rightBorder.isInvisible() ) {
				if ( rightBorder.getColor() != null )
					gc.setColor( rightBorder.getColor() );
				gc.drawLine( width, 0, width, height );
			}

			if ( !topBorder.isInvisible() ) {
				if ( topBorder.getColor() != null )
					gc.setColor( topBorder.getColor() );
				gc.drawLine( 0, 0, width, 0 );
			}
			
			if ( !bottomBorder.isInvisible() ) {
				if ( bottomBorder.getColor() != null )
					gc.setColor( bottomBorder.getColor() );
				gc.drawLine( 0, height, width, height );
			}

		}
	}
	
	public Dimension getSize( EditorByCSS editor, NodeElement element, Graphics gc ) {
		renderer( editor, element, 0, 0, gc, false );
		return ( Dimension )element.getData( "size" );
	}
	
	public Rectangle getBounds( EditorByCSS editor, NodeElement element, int offset, Graphics gc ) {
		Dimension dim = getSize( editor, element, gc );
		return new Rectangle( 0, 0, dim.width, dim.height );
	}

	public int getOffset(EditorByCSS editor, NodeElement element, int x, int y, Graphics gc) {
		return 0;
	}
	
	public boolean isBlock( EditorByCSS editor, NodeElement element ) {
		return "block".equalsIgnoreCase( 
			( String )element.getCSSProperty( "display", "block" ) 
		);
	}

	public boolean isInside(EditorByCSS editor, NodeElement element, int x, int y, Graphics gc) {
		return false;
	}

}
