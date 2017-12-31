package com.japisoft.editix.editor.xquery.kit;

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

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class XQueryView extends PlainView {

	private SyntaxLexer sl;
	private Segment line;	
	
	public XQueryView(Element e) {
		super(e);
		line = new Segment();
		this.sl = new SyntaxLexer();
	}
	
	public void drawLine(int lineIndex, Graphics g, int x, int y) {
		( ( Graphics2D )g ).setRenderingHint( 
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
					d.getText(start, end - (start + 1)),
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
