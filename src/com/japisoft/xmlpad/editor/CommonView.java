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

package com.japisoft.xmlpad.editor;

import java.awt.Color;

import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class CommonView extends PlainView implements XMLViewable {

	protected LineTokenizer lp;

	protected Segment line;

	public CommonView(Element elem) {
		super(elem);
		line = new Segment();
		lp = new LineTokenizer();
	}

	protected ViewPainterListener painterListener;
	
	public void setViewPainterListener( ViewPainterListener listener ) {
		this.painterListener = listener;
	}

	protected boolean visibleSpace = false; 
	
	public void setDisplaySpace( boolean visibleSpace ) {
		this.visibleSpace = visibleSpace;
	}
	
	protected Color darker(Color c) {
		return new Color(Math.max((int) (c.getRed() * 0.8), 0), Math.max(
				(int) (c.getGreen() * 0.8), 0), Math.max(
				(int) (c.getBlue() * 0.8), 0));
	}

	public void setSyntaxColor(boolean enabled) {
		if (!enabled)
			lp = null;
	}
	
	public void setDTDMode(boolean enabled) {
		// IGNORED
	}	

	protected Color getDefaultColor() {
		return getContainer().getForeground();
	}
	
}