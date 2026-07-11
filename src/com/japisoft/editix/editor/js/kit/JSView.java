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

package com.japisoft.editix.editor.js.kit;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import com.japisoft.editix.editor.js.JSSyntaxDescription;

public class JSView extends PlainView {

	private LineParser sl;
	private Segment line;	

	public JSView(String preferenceGroupe,Element e) {
		super(e);
		line = new Segment();
		this.sl = new LineParser( preferenceGroupe, new JSSyntaxDescription() );
	}

	public void drawLine(int lineIndex, Graphics g, int x, int y) {
		Graphics2D g2d = ( Graphics2D )g;
		g2d.setRenderingHint( 
			RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);
		Document d = getDocument();
		Color defColor = getContainer().getForeground();
		JTextComponent cc = (JTextComponent) getContainer();

		Element lineElement = getElement().getElement(lineIndex);
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		try {
			d.getText(start, end - (start + 1), line);
			int offset = 0;
			Token[] tks =
				sl.getTokenForLine(
					line,
					lineIndex);

			if (tks == null) {
				return; // ??
			}
			FontMetrics fm = g.getFontMetrics();
			int h = fm.getHeight();
			boolean mustRepaint = false;
			for (int i = 0; i < sl.getTokenCount(); i++) {
				Token t = tks[i];
				mustRepaint = mustRepaint || t.mustRepaint;
				line.count = t.text.length();
				Color c = t.color;
				
				g.setColor(c);
				x = Utilities.drawTabbedText(line, x, y, g, this, offset);

				offset += line.count;
				line.offset += line.count;

			}
			if ( mustRepaint )
				getContainer().repaint();
		} catch (BadLocationException e) {
		}
	}

}
