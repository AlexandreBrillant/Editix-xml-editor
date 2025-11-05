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

package com.japisoft.xmlpad.editor.renderer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Basic line renderer with a rectangle
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class BasicLineRenderer implements LineRenderer {

	private static BasicLineRenderer singleton = null;

	public static BasicLineRenderer getSharedInstance() {
		if ( singleton == null )
			singleton = new BasicLineRenderer();
		return singleton;
	}
		
	public void renderer(
		int type,
		Graphics gc,
		Color color,
		int x,
		int y,
		int width,
		int height) {
		gc.setColor( color );
		if ( type == 1 ) {
			boolean flip = false;
			for ( int i = 0; i < width; i+= 4 ) {
				if ( flip )
					gc.drawRect(x + i, y + height - 2, 4, 2 );
				else
					gc.drawRect(x + i, y + height - 3, 4, 2 );
				flip = !flip;
			}
		} else {
			
			gc.drawRect(x, y + height - 2, width, 2 );
		}
	}

}

