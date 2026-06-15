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

package com.japisoft.xmlpad.editor;

import javax.swing.text.*;
import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.preferences.Preferences;

import java.awt.*;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.2
 */
final class XMLTextView extends CommonView implements XMLViewable {

	private int tabSize = 2;
	
	public XMLTextView(Element element, boolean visibleSpace ) {
		super(element);
		this.visibleSpace = visibleSpace;
		tabSize = Preferences.getPreference( "editor", "tabSize", 2 );
	}

	protected int getTabSize() {
		return tabSize;
	}
	
	public void setDisplaySpace( boolean space ) {
		this.visibleSpace = space;
	}
	
	public void setSyntaxColor(boolean sc) {
		if ( !sc )
			lp = null;
	}

	public void setDTDMode(boolean dtdMode) {
		/*
		if ( lp != null )
			lp.setDTDMode(dtdMode);
		*/
	}

	private float oldStartUnderlineX1 = -1;
	private float oldStartUnderlineX2 = -1;
	private float oldStartUnderlineY = -1;
	private float oldStopUnderlineX1 = -1;
	private float oldStopUnderlineX2 = -1;
	private float oldStopUnderlineY = -1;
	
	public final void drawLine( TabExpander expander, Document document, XMLEditor host, Color defaultColor, int lineIndex, int start, int end, Graphics2D g, float x, float y, boolean startLine ) {
		
		int startTag = -1;
		int stopTag = -1;
		String currentTagName = null;

		if (host instanceof XMLEditor) {
			XMLEditor e = (XMLEditor) host;
			if (e.getXMLContainer() != null)
				if (e.getXMLContainer().getCurrentElementNode() != null) {
					if (!e.getXMLContainer().getCurrentElementNode()
							.isAutoClose()) {
						startTag = e.getXMLContainer().getCurrentElementNode()
								.getStartingOffset();
						stopTag = e.getXMLContainer().getCurrentElementNode()
								.getStoppingOffset();
						if (e.getXMLContainer().getCurrentElementNode().isTag())
							currentTagName = e.getXMLContainer()
									.getCurrentElementNode().getContent();
					}
				}
		}

		metrics = g.getFontMetrics();
		Color defColor = defaultColor;
		Font defFont = host.getFont();
		int fontHeight = metrics.getHeight();
		int screenLine = (int)y / fontHeight;

		g.setRenderingHint( 
			RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);

		g.setFont( defFont );
			
		// Very long line case
		if ( end > ( start + 5000 ) ) {	
			if ( Preferences.getPreference( 
					"editor", 
					"longLineOptimization", 
					true 
				) ) {				
				end = start + 5000;	
			}
		}

		try {
			document.getText( start, end - start, line );
		} catch( BadLocationException exc ) {
			return;
		}

		if ( painterListener != null )
			painterListener.reset( y );
		
		if (lp == null ) {	// Optimization
			g.setColor( defColor );
			Utilities.drawTabbedText(line, x, y, g, this, 0);
		} else {
						
			int offset = 0;
			FastArrayList v = lp.parse( line.array, line.offset, line.offset + line.count, lineIndex, screenLine );
			
			int size = v.size();
			
			for (int i = 0; i < size; i++) {
				LineToken le = (LineToken) v.get(i);
				String content = le.content;
				int type = le.type;

				Color c = LineToken.getColor(host, false, false, type, 0, 0);
				if (c == null)
					c = defColor;

				if (le.type == LineToken.NAMESPACE && content != null) {
					if (host.hasColorForPrefix(content))
						c = (Color) host.getColorForPrefix(content);
				}

				// Start tag

				if (le.type == LineToken.TAG && content != null) {
					if (host.hasColorForTag(content))
						c = (Color) host.getColorForTag(content);
					if (i >= 2) {
						LineToken le_2 = (LineToken) v.get(i - 2);
						if (le_2.type == LineToken.NAMESPACE) {
							if (host.hasColorForPrefix(le_2.content))
								c = host.getColorForPrefix(le_2.content);
						}
					}
					if ( painterListener != null )
						painterListener.paintElement( x, y );
				}

				if (le.type == LineToken.ATTRIBUTE && content != null) {
					if (host.hasColorForAttribute(content))
						c = host.getColorForAttribute(content);
				}

				g.setColor(c);

				if (content != null)
					line.count = content.length();
				else
					line.count = 0;

				float oldx = x;
				try {
					
					x = Utilities.drawTabbedText(
							line, 
							x, 
							y, 
							g, 
							expander,
							0
					);
					
					if ( visibleSpace ) {
						float delta = oldx;
						for ( int j = 0; j < line.count; j++ ) {
							char cc = line.array[ line.offset + j ];
							if ( ( cc == ' ' ) || ( cc == '\t' ) || ( cc == 160 ) ) {
								g.setColor( Color.GRAY );
								g.drawLine( (int) delta, (int)y, (int)delta + 2, (int)y );
								g.drawLine( (int)delta + 2, (int)y, (int)delta +2, (int)y + 2 );
							}
							if ( cc == '\t' ) {
								delta = ( int )nextTabStop( delta, line.offset + j );		
							} else
								delta += metrics.charWidth( cc );
						}
					}

				} catch (ArrayIndexOutOfBoundsException exc) {
					// ? ?
				}
				int oldOffset = offset;
				offset += line.count;

				line.offset += line.count;
			}
		}		
	}
		
	@Override
	public final void drawLine(int lineIndex, Graphics2D g, float x, float y) {		
		Element lineElement = getElement().getElement(lineIndex);
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		drawLine( this, getDocument(), (XMLEditor)getContainer(), getDefaultColor(), lineIndex, start, end, g, x, y, true );
	}

	private void drawUnderline(XMLEditor host, int oldx, int x, int y, Color c, Graphics g) {
		g.setColor( LineToken.getColor( host, false, false, LineToken.TAG_UNDERLINE, 0, 0 ) );
		int __ = y;
		for (int i = oldx; i <= x; i += 2) {
			g.drawLine(i, __, i, __);
		}
	}


	////////////////////////////////////////////////////////////////////////////
	
	class LineAttribute {
		public Color color;

		public Font font;

		public LineAttribute(Color c, Font f) {
			this.color = c;
			this.font = f;
		}
	}


}
