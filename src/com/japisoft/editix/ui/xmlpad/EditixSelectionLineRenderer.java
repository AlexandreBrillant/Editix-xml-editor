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

package com.japisoft.editix.ui.xmlpad;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.UIManager;
import com.japisoft.xmlpad.editor.renderer.LineRenderer;

/**
 * Renderer for Editix Look
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class EditixSelectionLineRenderer implements LineRenderer {

	static EditixSelectionLineRenderer singleton = null;
	
	public static LineRenderer getSharedInstance() {
		if ( singleton == null ) {
			singleton = new EditixSelectionLineRenderer();
		}
		return singleton;
	}

	private EditixSelectionLineRenderer() {
		bgLineColor = new Color( 220, 220, 220 );
	}

	private Color bgLineColor = null;

	public void renderer(
		int code,
		Graphics gc,
		Color color,
		int x,
		int y,
		int width,
		int height) {

		gc.setColor( bgLineColor );
		gc.fillRect( x, y, width, height );        
	}

}

