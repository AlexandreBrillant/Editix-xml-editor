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

package com.japisoft.editix.ui.xslt;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class CurrentHighlighter implements HighlightPainter {

	static Color lineColor = new Color( 150, 220, 150 );
	
	public CurrentHighlighter() {
		if ( UIManager.getColor( "editix.xslt.currentline" ) != null ) {
			lineColor = UIManager.getColor( "editix.xslt.currentline" );
		}
	}

	public void paint(Graphics g, int p0, int p1, Shape bounds,
			JTextComponent textComponent) {

		FontMetrics metrics = g.getFontMetrics();
		Document doc = textComponent.getDocument();
		int lineNo = doc.getDefaultRootElement().getElementIndex(p0);

		Rectangle rect = (Rectangle) bounds;
		int height = metrics.getHeight();
		int x = rect.x;
		int y = rect.y + height * lineNo;
		int width = textComponent.getWidth();

		g.setColor(lineColor);
		g.fillRect( x, y, width, height );
		g.draw3DRect( x, y, width, height, true );		

	}
	
	
}
