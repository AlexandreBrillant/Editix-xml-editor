package com.japisoft.xmlpad.editor;

import javax.swing.text.*;
import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.preferences.Preferences;

import java.awt.*;

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
class XMLTextView extends CommonView implements XMLViewable {

	public XMLTextView(Element element, boolean visibleSpace ) {
		super(element);
		this.visibleSpace = visibleSpace;
	}

	public void setDisplaySpace( boolean space ) {
		this.visibleSpace = space;
	}
	
	public void setSyntaxColor(boolean sc) {
		if ( !sc )
			lp = null;
	}

	public void setDTDMode(boolean dtdMode) {
		if ( lp != null )
			lp.setDTDMode(dtdMode);
	}

	private int oldLineIndex = -1;
	private int oldStartUnderlineX1 = -1;
	private int oldStartUnderlineX2 = -1;
	private int oldStartUnderlineY = -1;
	private int oldStopUnderlineX1 = -1;
	private int oldStopUnderlineX2 = -1;
	private int oldStopUnderlineY = -1;
	private String oldElement = "";
	
	/*
	public void paint(Graphics g, Shape a) {
	  if ( validFontMetrics == null ) {
		  validFontMetrics = metrics;	
	  }
	  super.paint( g, a );
	  if ( metrics == MinimalFontMetrics.getInstance() ) {
		  metrics = validFontMetrics; 
	  }
	}
	*/

	// private FontMetrics validFontMetrics = null;
	
	public void drawLine(int lineIndex, Graphics g, int x, int y) {
		
		XMLPadDocument syntaxDocument;
		Document document = getDocument();

		XMLEditor host = (XMLEditor) getContainer();
		
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

		if (document instanceof XMLPadDocument) {
			syntaxDocument = (XMLPadDocument) document;
		} else {
			syntaxDocument = null;
			// tokenMarker = null;
		}

		metrics = g.getFontMetrics();
		Color defColor = getDefaultColor();
		Font defFont = host.getFont();

		Graphics2D g2d = ( Graphics2D )g;
		g2d.setRenderingHint( 
			RenderingHints.KEY_TEXT_ANTIALIASING, 
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
		);

		g2d.setFont( defFont );
		
		try {
			Element lineElement = getElement().getElement(lineIndex);
			int start = lineElement.getStartOffset();
			int end = lineElement.getEndOffset();

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

			document.getText( start, end - ( start + 1 ), line );

			if ( painterListener != null )
				painterListener.reset( y );
			
			if (lp == null ) {	// Optimization
				g.setColor( defColor );
				Utilities.drawTabbedText(line, x, y, g, this, 0);
			} else {
				int offset = 0;
				FastVector v = lp.parse(line, lineIndex);

				int p0 = 0, p1 = 0;

				int size = v.size();
				
				for (int i = 0; i < size; i++) {
					LineElement le = (LineElement) v.get(i);
					String content = le.content;
					int type = le.type;

					Color c = LineElement.getColor(host, false, false, type, 0,
							0);
					if (c == null)
						c = defColor;

					if (le.type == LineElement.NAMESPACE && content != null) {
						if (host.hasColorForPrefix(content))
							c = (Color) host.getColorForPrefix(content);
					}

					// Start tag

					if (le.type == LineElement.TAG && content != null) {
						if (host.hasColorForTag(content))
							c = (Color) host.getColorForTag(content);
						if (i >= 2) {
							LineElement le_2 = (LineElement) v.get(i - 2);
							if (le_2.type == LineElement.NAMESPACE) {
								if (host.hasColorForPrefix(le_2.content))
									c = host.getColorForPrefix(le_2.content);
							}
						}
						if ( painterListener != null )
							painterListener.paintElement( x, y );
					}

					if (le.type == LineElement.ATTRIBUTE && content != null) {
						if (host.hasColorForAttribute(content))
							c = host.getColorForAttribute(content);
					}

					Font f = defFont;

					g.setColor(c);

					if (content != null)
						line.count = content.length();
					else
						line.count = 0;

					int oldx = x;
					try {

						x = Utilities.drawTabbedText(line, x, y, g, this, offset);
						
						if ( visibleSpace ) {
							int delta = oldx;
							for ( int j = 0; j < line.count; j++ ) {
								char cc = line.array[ line.offset + j ];
								if ( ( cc == ' ' ) || ( cc == '\t' ) || ( cc == 160 ) ) {
									g.setColor( Color.GRAY );
									g.drawLine( delta, y, delta + 2, y );
									g.drawLine( delta + 2, y, delta +2, y + 2 );
								}
								if ( cc == '\t' ) {
									delta = ( int )nextTabStop( delta, line.offset + j );		
								} else
									delta += metrics.charWidth( cc );
							}
						}

						// x = Utilities.drawTabbedText(line, x, y, g, this,
						// offset);

					} catch (ArrayIndexOutOfBoundsException exc) {
						// ? ?
					}
					int oldOffset = offset;
					offset += line.count;

					if (le.type == LineElement.TAG) {
						boolean ok = false;
						boolean storeLastUnderline = false;
						boolean paintIt = false;

						if (currentTagName != null
								&& (currentTagName.equals(le.content))) {
							g.setColor(host.getBackground());
							g.drawLine(oldStartUnderlineX1, oldStartUnderlineY,
									oldStartUnderlineX2, oldStartUnderlineY);
							g.drawLine(oldStopUnderlineX1, oldStopUnderlineY,
									oldStopUnderlineX2, oldStopUnderlineY);
							paintIt = true;
						} else {
							storeLastUnderline = true;
						}

						if (paintIt && startTag > -1
								&& (start + oldOffset) >= startTag
								&& (start + oldOffset) <= startTag + line.count) {

							if (storeLastUnderline) {
								oldStartUnderlineX1 = oldx;
								oldStartUnderlineX2 = x;
								oldStartUnderlineY = y + 2;
							}

							drawUnderline(oldx, x, y + 2, LineElement.getColor(
									host, false, false,
									LineElement.TAG_UNDERLINE, 0, 0), g);

							ok = true;
						}

						if (paintIt && !ok && stopTag > -1
								&& (start + offset) >= stopTag - line.count
								&& (start + offset) <= stopTag) {

							if (storeLastUnderline) {
								oldStopUnderlineX1 = oldx;
								oldStopUnderlineX2 = x;
								oldStopUnderlineY = y + 2;
							}

							drawUnderline(oldx, x, y + 2, LineElement.getColor(
									host, false, false,
									LineElement.TAG_UNDERLINE, 0, 0), g);
						}

					}
					line.offset += line.count;
				}
			}
			
/*
			if ( host.isClosedElement( lineIndex ) ) {
				
				g.setColor( host.getColorOpenCloseTipBackground() );
				g.fillRect(
					x,
					y,
					10,
					3
				);

				g.setColor( host.getColorOpenCloseTip() );				

				g.drawRect(
					x,
					y,
					10,
					3
				);
				
			}
*/

		} catch (BadLocationException bl) {
			bl.printStackTrace();
		}
	}
	
	private void drawUnderline(int oldx, int x, int y, Color c, Graphics g) {
		g.setColor(LineElement.getColor((XMLEditor) getContainer(), false,
				false, LineElement.TAG_UNDERLINE, 0, 0));

		int _ = y;
		for (int i = oldx; i <= x; i += 2) {
			g.drawLine(i, _, i, _);
		}
	}

	protected Color getDefaultColor() {
		return getContainer().getForeground();
	}

	////////////////////////////////////////////////////////////////////////////
	// ////
	
	class LineAttribute {
		public Color color;

		public Font font;

		public LineAttribute(Color c, Font f) {
			this.color = c;
			this.font = f;
		}
	}


}
