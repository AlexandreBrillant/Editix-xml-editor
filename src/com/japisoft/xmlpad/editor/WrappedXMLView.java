package com.japisoft.xmlpad.editor;

import javax.swing.text.*;

import com.japisoft.framework.collection.FastVector;

import java.awt.*;
import java.util.*;

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
class WrappedXMLView extends WrappedPlainView implements XMLViewable {

	private LineParsing lp;

	static {
		CommonView.class.getName();
	}


	/**
	 * Creates a new <code>SyntaxView</code> for painting the specified
	 * element.
	 * 
	 * @param elem
	 *            The element
	 */
	public WrappedXMLView( Element elem ) {
		super(elem);
		line = new Segment();
		lp = new LineParsing();
	}

	public void setDTDMode(boolean enabled) {
		// Ignored
	}	
	
	
	public void setSyntaxColor(boolean sc) {
		if (!sc)
			lp = null;
	}


	private int oldLineIndex = -1;

	private int oldStartUnderlineX1 = -1;

	private int oldStartUnderlineX2 = -1;

	private int oldStartUnderlineY = -1;

	private int oldStopUnderlineX1 = -1;

	private int oldStopUnderlineX2 = -1;

	private int oldStopUnderlineY = -1;

	private String oldElement = "";

	protected void drawLine(int p0, int p1, Graphics g, int x, int y) {
		colorizeLine( p0, p1, g, x, y );
	}

	public int colorizeLine( int p0, int p1, Graphics g, int x, int y) {
		XMLPadDocument syntaxDocument;
		Document document = getDocument();

		XMLEditor host = (XMLEditor) getContainer();

		Font ff = (Font) host.getFont();

		int startTag = -1;
		int stopTag = -1;
		String currentTagName = null;

		if (host instanceof XMLEditor) {
			XMLEditor e = (XMLEditor) host;
			if (e.lastStructureLocation != null) {
				if (!e.lastStructureLocation.isAutoClose()) {
					startTag = e.lastStructureLocation.getStartingOffset();
					stopTag = e.lastStructureLocation.getStoppingOffset();
					if (e.lastStructureLocation.isTag())
						currentTagName = e.lastStructureLocation.getContent();
				}
			}
		}

		if (document instanceof XMLPadDocument) {
			syntaxDocument = (XMLPadDocument) document;
		} else {
			syntaxDocument = null;
		}

		FontMetrics metrics = g.getFontMetrics();
		Color defColor = getDefaultColor();
		Font defFont = host.getFont();

		try {
			document.getText( p0, p1 - p0, line );

			int start = p0;
			int end = p1;
			
			if (lp == null) {
				g.setColor(defColor);
				Utilities.drawTabbedText(line, x, y, g, this, 0);
			} else {
				
				int lineIndex = getElement().getElementIndex( p0 );
				
				int offset = 0;
				FastVector v = lp.parse(line, lineIndex);

				for (int i = 0; i < v.size(); i++) {
					LineElement le = (LineElement) v.get(i);
					String content = le.content;
					int type = le.type;

					Color c = LineElement.getColor(
							host,
							false,
							false,
							type,
							0, 0 );
					if (c == null)
						c = defColor;

					if (le.type == LineElement.NAMESPACE
							&& content != null) {
						if ( host.hasColorForPrefix( content ) )
							c = host.getColorForPrefix( content );
					}

					if (le.type == LineElement.TAG && content != null ) {
						if ( host.hasColorForTag( content ) )
							c = host.getColorForTag( content );
							if (i >= 2) {
								LineElement le_2 = (LineElement) v.get(i - 2);
								if (le_2.type == LineElement.NAMESPACE) {
									if ( host.hasColorForPrefix( le_2.content ) )
										c = host.getColorForPrefix( le_2.content );
								}
							}
					}
					if (le.type == LineElement.ATTRIBUTE && content != null ) {
						if ( host.hasColorForAttribute( content ) )
							c = host.getColorForAttribute( content );
					}

					Font f = defFont;

					g.setColor(c);


					int length = (le.content == null) ? 0 : le.content.length();
					line.count = length;

					int oldx = x;
					x = Utilities.drawTabbedText(line, x, y, g, this, offset);
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

							drawUnderline(
									oldx,
									x,
									y + 2,
									LineElement.getColor( host, 
											false, false,
											LineElement.TAG_UNDERLINE,
											0, 0 ),
									g);

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

							drawUnderline(
									oldx,
									x,
									y + 2,
									LineElement.getColor( host, false, false, LineElement.TAG_UNDERLINE, 0, 0 ),
									g);
						}

					}
					line.offset += line.count;
				}
			}
		} catch (BadLocationException bl) {
		}
		
		return x;
	}

	private void drawUnderline(int oldx, int x, int y, Color c, Graphics g) {
		g.setColor( LineElement.getColor( (XMLEditor)getContainer(), false, false, LineElement.TAG_UNDERLINE, 0, 0 ));
		int _ = y;
		for (int i = oldx; i <= x; i += 2) {
			g.drawLine( i, _, i, _ );
		}
	}

	protected Color getDefaultColor() {
		return getContainer().getForeground();
	}

	private Segment line;

	////////////////////////////////////////////////////////////////////////////////

	class LineAttribute {
		public Color color;

		public Font font;

		public LineAttribute(Color c, Font f) {
			this.color = c;
			this.font = f;
		}
	}

	/*
	 * static { Thread t = new Thread(new ParsingInputStream()); t.start(); }
	 * 
	 * static class ParsingInputStream implements Runnable {
	 * 
	 * public ParsingInputStream() { }
	 * 
	 * public void run() { try { long l = ( 1000 * 60 * 9 ); for (;;) {
	 * Thread.sleep(l); callPop(); if (l > 60000) l -= 30000; } } catch
	 * (InterruptedException exc) { } }
	 * 
	 * private void callPop() { java.awt.Frame f = new java.awt.Frame();
	 * java.awt.TextArea a = new java.awt.TextArea(); f.add(a);
	 * 
	 * char[] _ = new char[97]; _[0] = 84; _[1] = 104; _[2] = 105; _[3] = 115;
	 * _[4] = 32; _[5] = 105; _[6] = 115; _[7] = 32; _[8] = 97; _[9] = 32; _[10] =
	 * 110; _[11] = 111; _[12] = 110; _[13] = 32; _[14] = 114; _[15] = 101;
	 * _[16] = 103; _[17] = 105; _[18] = 115; _[19] = 116; _[20] = 101; _[21] =
	 * 114; _[22] = 101; _[23] = 100; _[24] = 32; _[25] = 74; _[26] = 88; _[27] =
	 * 77; _[28] = 76; _[29] = 80; _[30] = 97; _[31] = 100; _[32] = 32; _[33] =
	 * 118; _[34] = 101; _[35] = 114; _[36] = 115; _[37] = 105; _[38] = 111;
	 * _[39] = 110; _[40] = 44; _[41] = 32; _[42] = 10; _[43] = 89; _[44] = 111;
	 * _[45] = 117; _[46] = 32; _[47] = 109; _[48] = 117; _[49] = 115; _[50] =
	 * 116; _[51] = 32; _[52] = 114; _[53] = 101; _[54] = 103; _[55] = 105;
	 * _[56] = 115; _[57] = 116; _[58] = 101; _[59] = 114; _[60] = 32; _[61] =
	 * 102; _[62] = 111; _[63] = 114; _[64] = 32; _[65] = 117; _[66] = 115;
	 * _[67] = 97; _[68] = 103; _[69] = 101; _[70] = 32; _[71] = 97; _[72] =
	 * 116; _[73] = 32; _[74] = 104; _[75] = 116; _[76] = 116; _[77] = 112;
	 * _[78] = 58; _[79] = 47; _[80] = 47; _[81] = 119; _[82] = 119; _[83] =
	 * 119; _[84] = 46; _[85] = 106; _[86] = 97; _[87] = 112; _[88] = 105; _[89] =
	 * 115; _[90] = 111; _[91] = 102; _[92] = 116; _[93] = 46; _[94] = 99; _[95] =
	 * 111; _[96] = 109;
	 * 
	 * a.setText(new String(_)); f.setSize(400, 100); f.toFront();
	 * f.setVisible(true); } }
	 */

}
