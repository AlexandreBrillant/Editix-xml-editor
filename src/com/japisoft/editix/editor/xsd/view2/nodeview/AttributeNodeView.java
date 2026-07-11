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

package com.japisoft.editix.editor.xsd.view2.nodeview;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.UIManager;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.editix.editor.xsd.view2.node.XSDNode;

public class AttributeNodeView extends AbstractXSDNodeView {

	public AttributeNodeView( XSDNode node ) {
		super( node );
	}

	@Override
	protected boolean isOptional() {
		Element e = node.getDOM();
		if ( e.hasAttribute( "use" ) ) {
			if ( "optional".equals( 
					e.getAttribute( "use" ) ) ) {
				return true;
			}
		}
		return false;
	}	
	
	protected void createBuffer( View designer ) {
		
		Graphics2D gc = ( Graphics2D )designer.getView().getGraphics();
		int width = gc.getFontMetrics().stringWidth( "@ " + node.toString() );
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

		bgc.fillRoundRect( 0, 0, buffer.getWidth(), buffer.getHeight(), 10, 10 );		

		bgc.setColor( Color.BLACK );
		bgc.setFont( gc.getFont() );
		bgc.drawString( "@ " + node.toString(), hpadding, buffer.getHeight() - vpadding - gc.getFontMetrics().getDescent() );
		
		setBorder( bgc );
		bgc.setColor( BORDER_COLOR );
		bgc.drawRoundRect( 0, 0, buffer.getWidth() - 1, buffer.getHeight() - 1, 10, 10 );
		
		
	}
	
}
