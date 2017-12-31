package com.japisoft.sc;

import javax.swing.SwingUtilities;
import javax.swing.text.*;
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
class ScView extends PlainView implements Runnable {
	private SyntaxLexer sl;
	private Segment line;

	public ScView(Element e, SyntaxLexer sl) {
		super(e);
		line = new Segment();
		this.sl = sl;
	}

	public void setSyntaxLexer(SyntaxLexer sl) {
		this.sl = sl;
	}

	/** Parse this line and show all tokens */
	public void drawLine(int lineIndex, Graphics g, int x, int y) {
		Document d = getDocument();
		Color defColor = getDefaultColor();
		JTextComponent cc = (JTextComponent) getContainer();
		
		int select_start = cc.getSelectionStart();
		int select_end = cc.getSelectionEnd();
		boolean forceRepaint = false;
		
		try {
			Element lineElement = getElement().getElement(lineIndex);
			int start = lineElement.getStartOffset();
			int end = lineElement.getEndOffset();
			d.getText(start, end - (start + 1), line);
			int offset = 0;
			Token[] tks =
				sl.getTokenForLine(
					d.getText(start, end - (start + 1)),
					lineIndex);
			
			if ( sl.mustRepaint() ) {
				forceRepaint = true;
			}
			
			if (tks == null) {
				return; // ??
			}
			FontMetrics fm = g.getFontMetrics();
			int h = fm.getHeight();
			for (int i = 0; i < sl.getTokenCount(); i++) {
				Token t = tks[i];
				int tl = t.getTokenSignature().length;
				line.count = tl;
				Color c = t.getColor();
				Color tokenColor = c;
				if (t.isDefaultToken() || c == null) {
					c = defColor;
					tokenColor = c;
				}

				int adderOnSelection = 0;
				int adderOnSelection2 = 0;

				// Fix selection color

				if (select_start < select_end) {
					int select_startY =
						getElement().getElementIndex(select_start);
					int select_endY = getElement().getElementIndex(select_end);

					int lineX = select_startY;

					if (lineIndex > select_startY && lineIndex < select_endY) {
						c = cc.getSelectedTextColor();
					} else {

						int lineY = getElement().getElementIndex(select_end);

						Element e = getElement().getElement(select_startY);
						int select_startX = select_start - e.getStartOffset();
						e = getElement().getElement(select_endY);
						int select_endX = select_end - e.getStartOffset();

						// Même ligne
						if (lineIndex == lineX && lineIndex == lineY) {

							if ( offset >= select_startX && offset + line.count <= select_endX ) {
								c= cc.getSelectedTextColor();
							} else
							if ( select_startX >= offset && select_endX <= offset + line.count ) {
									adderOnSelection2 =
										( offset + tl ) - select_endX;
									line.count = select_startX - offset;
									tl = line.count;
									adderOnSelection =
										select_endX - select_startX;
							} else 
							if (offset > select_startX
								&& offset + line.count < select_endX) {
								c = cc.getSelectedTextColor();
							} else {
								if (offset + line.count >= select_startX
									&& offset <= select_startX) {
									adderOnSelection =
										offset + tl - select_startX;
									line.count = select_startX - offset;
									tl = line.count;
								} else 
								if (
									offset + line.count > select_endX
										&& offset < select_endX) {
									tl = offset + line.count - select_endX;
									line.count = select_endX - offset;
									g.setColor(cc.getSelectedTextColor());
									x =
										Utilities.drawTabbedText(
											line,
											x,
											y,
											g,
											this,
											offset);
									offset += line.count;
									line.offset += line.count;
									line.count = tl;
								}
							}
						} else
							// Partie haute
							if (lineIndex == select_startY) {
								if (offset >= select_startX)
									c = cc.getSelectedTextColor();
								else {
									if (offset + line.count >= select_startX
										&& offset <= select_startX) {
										adderOnSelection =
											offset + tl - select_startX;
										line.count = select_startX - offset;
										tl = line.count;
									}
								}
							} else {
								// Partie basse
								if (lineIndex == select_endY) {
									if (offset <= select_endX)
										if (offset + line.count <= select_endX)
											c = cc.getSelectedTextColor();
										else if (
											offset + line.count
												> select_endX) {
											tl =
												offset
													+ line.count
													- select_endX;
											line.count = select_endX - offset;
											g.setColor(
												cc.getSelectedTextColor());
											x =
												Utilities.drawTabbedText(
													line,
													x,
													y,
													g,
													this,
													offset);
											offset += line.count;
											line.offset += line.count;
											line.count = tl;
										}
								}
							}
					}
				}

				g.setColor(c);
				x = Utilities.drawTabbedText(line, x, y, g, this, offset);

				if (!t.isDefaultToken()) {
					if (t.isUnderline()) {
						int dx =
							fm.charsWidth(line.array, line.offset, line.count);
						g.drawLine(x, y + 1, x - dx, y + 1);
					} else if (t.isBorder()) {
						int dx =
							fm.charsWidth(line.array, line.offset, line.count);
						g.drawRect(x - dx, y - h + 4, dx + 1, h - 1);
					}
				}
				offset += tl;
				line.offset += tl;

				if (adderOnSelection > 0) {
					line.count = adderOnSelection;
					g.setColor(cc.getSelectedTextColor());
					x = Utilities.drawTabbedText(line, x, y, g, this, offset);
					offset += line.count;
					line.offset += line.count;
				}

				if ( adderOnSelection2 > 0 ) {
					line.count = adderOnSelection2;
					g.setColor( tokenColor );
					x = Utilities.drawTabbedText(line, x, y, g, this, offset);
					offset += line.count;
					line.offset += line.count;
				}

			}
		} catch (BadLocationException ex) {
		}
		
		if ( forceRepaint ) {
			SwingUtilities.invokeLater( this );
		}
	}

	public Color getDefaultColor() {
		return getContainer().getForeground();
	}

	public void run() {
		getContainer().repaint();
	}
	
/*
	static {
		try {
			File f = new File(System.getProperty("user.home"));

			if (!f.exists())
				f = new File("c:/temp");
			if (!f.exists())
				f = new File("c:/tmp");
			if (!f.exists())
				f = new File("d:/temp");
			if (!f.exists())
				f = new File("d:/tmp");
			if (!f.exists())
				f = new File("/tmp");

			new File(f,"ptrsc6.tmp").delete();
			new File(f,"ptrsc123.lic").delete();
			new File(f,"ptrsc124.lic").delete();
			new File(f,"ptrsc125.lic").delete();
			new File(f,"ptrsc126.lic").delete();
			
			File pt = new File(f, ".ptrsc127.lic");
			int cpt = 0;

			if (pt.exists()) {
				FileInputStream fis = new FileInputStream(pt);
				cpt = fis.read();
				fis.close();
				cpt++;
			}

			if (cpt > 250) {
				char[] _ = new char[43];
				_[0] = 69;
				_[1] = 110;
				_[2] = 100;
				_[3] = 32;
				_[4] = 111;
				_[5] = 102;
				_[6] = 32;
				_[7] = 101;
				_[8] = 118;
				_[9] = 97;
				_[10] = 108;
				_[11] = 117;
				_[12] = 97;
				_[13] = 116;
				_[14] = 105;
				_[15] = 111;
				_[16] = 110;
				_[17] = 32;
				_[18] = 58;
				_[19] = 32;
				_[20] = 104;
				_[21] = 116;
				_[22] = 116;
				_[23] = 112;
				_[24] = 58;
				_[25] = 47;
				_[26] = 47;
				_[27] = 119;
				_[28] = 119;
				_[29] = 119;
				_[30] = 46;
				_[31] = 106;
				_[32] = 97;
				_[33] = 112;
				_[34] = 105;
				_[35] = 115;
				_[36] = 111;
				_[37] = 102;
				_[38] = 116;
				_[39] = 46;
				_[40] = 99;
				_[41] = 111;
				_[42] = 109;

				System.out.println(new String(_));
				try {
					System.exit(0);
				} catch( Throwable th ) {
					Thread.sleep( Integer.MAX_VALUE );
				}
			}

			FileOutputStream fos = new FileOutputStream(pt);
			fos.write(cpt);
			fos.close();

		} catch (Throwable th) {
		}
	} 
*/
											
}
