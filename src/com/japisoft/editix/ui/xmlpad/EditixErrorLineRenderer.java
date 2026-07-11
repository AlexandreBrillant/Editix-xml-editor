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

import java.awt.Color;
import java.awt.Graphics;

import com.japisoft.editix.main.steps.lookandfeel.EditiXLookAndFeel;
import com.japisoft.xmlpad.editor.renderer.LineRenderer;

/**
 * Renderer for Editix look
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class EditixErrorLineRenderer implements LineRenderer {

	static EditixErrorLineRenderer singleton = null;

	public static LineRenderer getSharedInstance() {
		if ( singleton == null ) {
			singleton = new EditixErrorLineRenderer();
		}
		return singleton;
	}

	private EditixErrorLineRenderer() {}

	private Color bgErrorColor = EditiXLookAndFeel.ERROR_COLOR_BACKGROUND;

	public void renderer(
		int code,
		Graphics gc,
		Color color,
		int x,
		int y,
		int width,
		int height) {
		gc.setColor( bgErrorColor );
		gc.fillRect( x, y, width, height );
	}

}
