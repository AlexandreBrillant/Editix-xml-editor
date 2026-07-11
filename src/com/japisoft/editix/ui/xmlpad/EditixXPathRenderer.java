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

package com.japisoft.editix.ui.xmlpad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import com.japisoft.xmlpad.editor.renderer.LineRenderer;

/**
 * Renderer for editix look
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class EditixXPathRenderer implements LineRenderer {

	static EditixXPathRenderer singleton = null;
	
	public static LineRenderer getSharedInstance() {
		if ( singleton == null )
			singleton = new EditixXPathRenderer();
		return singleton;
	}

	private EditixXPathRenderer() {}

	private static Stroke STROKE = new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4, new float[] { 4 }, 0 ); 

	private Color bgXPathColor = new Color( Integer.parseInt( "BBFFBB", 16 ) );

	public void renderer(
		int code,
		Graphics gc,
		Color color,
		int x,
		int y,
		int width,
		int height) {
		
		gc.setColor( bgXPathColor );
		gc.fillRect( x, y, width, height );

		gc.setColor( color );
		
		Graphics2D g2d = ( Graphics2D )gc;
        g2d.setPaint( color );   
        
        g2d.setStroke( STROKE );                  
        g2d.draw( new Rectangle2D.Double( x, y, width, height ) );		

	}

}
