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

/*
 * Created on Jul 9, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.japisoft.xmlpad.editor.renderer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Simple line renderer
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class SimpleLineRenderer implements LineRenderer {

	private static SimpleLineRenderer singleton = null;

	public static SimpleLineRenderer getSharedInstance() {
		if ( singleton == null )
			singleton = new SimpleLineRenderer();
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
		gc.drawLine( x, y + height, x, y + height + 1 );
		gc.drawLine( x + width, y + height, x + width, y + height + 1 );
		gc.drawLine( x, y + height + 1, x + width, y + height + 1 );

	}

}

