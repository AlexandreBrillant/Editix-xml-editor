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

package com.japisoft.stylededitor.ui.node;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.japisoft.stylededitor.EditorByCSS;
import com.japisoft.stylededitor.model.DocumentElement;
import com.japisoft.stylededitor.model.NodeElement;

public class DocumentElementView extends NodeElementView {

	@Override
	public void paint(EditorByCSS editor, NodeElement element, int x, int y, Graphics gc) {
		super.paint( editor, ( ( DocumentElement )element ).getRootElement(), x, y, gc );
	}
	
	@Override
	public Rectangle getBounds(EditorByCSS editor, NodeElement element, int offset, Graphics gc) {
		return super.getBounds( editor, ( ( DocumentElement )element ).getRootElement(), offset, gc);
	}

	@Override
	public Dimension getSize(EditorByCSS editor, NodeElement element, Graphics gc) {
		return super.getSize( editor, ( ( DocumentElement )element ).getRootElement(), gc);
	}

}
