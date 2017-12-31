package com.japisoft.xmlpad.editor;

import javax.swing.text.*;

import com.japisoft.framework.collection.FastVector;

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
class XMLView extends CommonView {

	public XMLView( Element element ) {
		super( element );
	}
	
	public void drawLine(int lineIndex, Graphics g, int x, int y) {
		Document document = getDocument();

		XMLEditor host = ( XMLEditor )getContainer();

		int startTag = -1;
		int stopTag = -1;
		boolean lineSelected = false;
		boolean lineError = false;

		if ( host instanceof XMLEditor ) {
			XMLEditor e = ( XMLEditor ) host;
			if ( e.getXMLContainer().getCurrentElementNode() != null) {
				if (!e.getXMLContainer().getCurrentElementNode().isAutoClose()) {
					startTag = e.getXMLContainer().getCurrentElementNode().getStartingOffset();
					stopTag = e.getXMLContainer().getCurrentElementNode().getStoppingOffset();
				}
			}

			if (e.errorHighlightTag != null) {
				try {
					int highlightFrom = e.getHighlighter().getHighlights()[0]
							.getStartOffset();
					lineError = document.getDefaultRootElement()
							.getElementIndex(highlightFrom) == lineIndex;
				} catch (Throwable th) {
				}
			} else if (e.selectionHighlight != null) {
				try {
					int highlightFrom = e.getHighlighter().getHighlights()[0]
							.getStartOffset();
					lineSelected = document.getDefaultRootElement()
							.getElementIndex(highlightFrom) == lineIndex;
				} catch (Throwable th) {
				}
			}
		}

		Font defFont = host.getFont();

		try {
			Element lineElement = getElement().getElement(lineIndex);
			int start = lineElement.getStartOffset();
			int end = lineElement.getEndOffset();
			document.getText(start, end - (start + 1), line);

			if (lp == null) {
				g.setColor(getDefaultColor());
				Utilities.drawTabbedText(line, x, y, g, this, 0);
			} else {

				int offset = 0;
				FastVector v = lp.parse(line, lineIndex);

				int p0 = -1, p1 = -1;
				boolean drawn = true;
				boolean endFlag = false;
				int oldOffset = 0;

				// Draw tag

				int x2 = x;
				int oldx = 0;
				int startingOffset = 0;
				int currentOffset = 0;
				int relativeOffset = 0;

				if (document != null)
					currentOffset = document.getDefaultRootElement()
							.getElement(lineIndex).getStartOffset();

				boolean declarationFlag = false;
				boolean dtdFlag = false;
				boolean trueCommentStart = false;
				boolean trueCdataStart = false;
				boolean trueDocTypeStart = false;
				String prefixName = null;
				
				for (int i = 0; i < v.size(); i++) {
					LineElement le = (LineElement) v.get(i);
					int type = le.type;

					if (le.type == LineElement.TAG_DELIMITER_START) { // <
						p0 = x2;
						endFlag = (le.nextType == LineElement.TAG_ENDER);
						startingOffset = currentOffset;
						declarationFlag = ( le.nextType == LineElement.DOCTYPE );
						dtdFlag = ( le.nextType == LineElement.CDATAORCOMMENTORDOCTYPE );
					}

					oldx = x2;
					char _;
					int cpt = 0;

					if (le.content != null) {
						for (int j = 0; j < le.content.length(); j++) {
							_ = le.content.charAt(j);
							if (_ == '\t') {
								x2 = (int) nextTabStop((float) x2, 0);
							} else {
								x2 += metrics.charWidth(_);
							}
						}
						if ( type == LineElement.NAMESPACE )
							prefixName = le.content;
					}

					if (le.type == LineElement.ENTITY)
						startingOffset = currentOffset;

					int length = (le.content == null) ? 0 : le.content.length();

					currentOffset += length;
					relativeOffset += length;

					//////////////// DOCTYPE

					if (le.type == LineElement.DOCTYPE_START) {
						p0 = oldx;
						startingOffset = (currentOffset - length);
						trueDocTypeStart = le.hasContent();
					} else if (le.type == LineElement.DOCTYPE_END) {
						p1 = x2;
						int yh = y - metrics.getAscent() + 1;
						int yb = y + metrics.getDescent() - 2;
						int c1 = metrics.stringWidth("<");
						int c2 = metrics.charWidth('>');
						int[] xp, yp;

						boolean trueDocTypeEnd = le.hasContent();

						xp = new int[] { p0, trueDocTypeStart ? p0 + c1 : p0,
								trueDocTypeEnd ? p1 - c2 : p1, p1,
								trueDocTypeEnd ? p1 - c2 : p1,
								trueDocTypeStart ? p0 + c1 : p0 };

						yp = new int[] { 
								(yb + yh) / 2, 
								yh, 
								yh, 
								(yb + yh) / 2,
								yb, 
								yb };

						Color c = LineElement.getColor(
								host,
								lineError,
								lineSelected,
								LineElement.DOCTYPE_BACKGROUND,
								startingOffset, 
								currentOffset );

						if ( c != null ) {
							g.setColor( c );
							g.fillPolygon(xp, yp, 6);
						}

						g.setColor(
								LineElement.getColor(
										host,
										lineError,
										lineSelected,
										LineElement.DOCTYPE,
										startingOffset, 
										currentOffset ) );								

						g.drawPolygon(xp, yp, 6);

					}

					//////////////// CDATA

					if (le.type == LineElement.CDATA_START) {
						p0 = oldx;
						startingOffset = (currentOffset - length);
						trueCdataStart = le.hasContent();
					} else if (le.type == LineElement.CDATA_END) {
						p1 = x2;
						int yh = y - metrics.getAscent() + 1;
						int yb = y + metrics.getDescent() - 2;
						int c1 = metrics.stringWidth("<");
						int c2 = metrics.charWidth('>');
						int[] xp, yp;

						boolean trueCdataEnd = le.hasContent();

						xp = new int[] { p0, trueCdataStart ? p0 + c1 : p0,
								trueCdataEnd ? p1 - c2 : p1, p1,
								trueCdataEnd ? p1 - c2 : p1,
								trueCdataStart ? p0 + c1 : p0 };

						yp = new int[] { (yb + yh) / 2, yh, yh, (yb + yh) / 2,
								yb, yb };

						Color c = 
								LineElement.getColor(
										host,
										lineError,
										lineSelected,
										LineElement.CDATA_BACKGROUND,
										startingOffset, 
										currentOffset );

						if (c != null) {
							g.setColor(c);

							g.fillPolygon(xp, yp, 6);
						}

						g.setColor(
								LineElement.getColor( host,lineError,
								lineSelected, LineElement.TAG_DELIMITER_START,
								startingOffset, currentOffset));

						g.drawPolygon(xp, yp, 6);

					}

					//////////////// DECLARATION

					if (le.type == LineElement.DECLARATION_START) {
						p0 = oldx;
						startingOffset = (currentOffset - length);
					} else if (le.type == LineElement.DECLARATION_END) {
						p1 = x2;
						int yh = y - metrics.getAscent() + 1;
						int yb = y + metrics.getDescent() - 2;
						int c1 = metrics.charWidth('<');
						int c2 = metrics.charWidth('>');
						int[] xp, yp;

						boolean trueCommentEnd = le.hasContent();

						xp = new int[] { p0, p0, p1, p1, p1, p0 };

						yp = new int[] { (yb + yh) / 2, yh, yh, (yb + yh) / 2,
								yb, yb };

						Color c = LineElement.getColor(host,lineError,
								lineSelected, LineElement.DEC_BACKGROUND,
								startingOffset, currentOffset);

						if (c != null) {
							g.setColor(c);

							g.fillPolygon(xp, yp, 6);
						}

						g.setColor(LineElement.getColor(host,lineError,
								lineSelected, LineElement.TAG_DELIMITER_START,
								startingOffset, currentOffset));

						g.drawPolygon(xp, yp, 6);

					}

					//////////////// COMMENT

					if (le.type == LineElement.COMMENT_START) {
						p0 = oldx;
						startingOffset = (currentOffset - length);
						trueCommentStart = le.hasContent();
					} else if (le.type == LineElement.COMMENT_END) {
						p1 = x2;
						int yh = y - metrics.getAscent() + 1;
						int yb = y + metrics.getDescent() - 2;
						int c1 = metrics.charWidth('<');
						int c2 = metrics.charWidth('>');
						int[] xp, yp;

						boolean trueCommentEnd = le.hasContent();

						xp = new int[] { p0, trueCommentStart ? p0 + c1 : p0,
								trueCommentEnd ? p1 - c2 : p1, p1,
								trueCommentEnd ? p1 - c2 : p1,
								trueCommentStart ? p0 + c1 : p0 };

						yp = new int[] { (yb + yh) / 2, yh, yh, (yb + yh) / 2,
								yb, yb };

						Color c = LineElement.getColor(host,lineError,
								lineSelected, LineElement.COMMENT_BACKGROUND,
								startingOffset, currentOffset);

						if (c != null) {
							g.setColor(c);

							g.fillPolygon(xp, yp, 6);
						}

						g.setColor(LineElement.getColor(host,lineError,
								lineSelected, LineElement.TAG_DELIMITER_START,
								startingOffset, currentOffset));

						g.drawPolygon(xp, yp, 6);

					} else if (le.type == LineElement.ENTITY) { // & ;

						int yh = y - metrics.getAscent() + 1;
						int yb = y + metrics.getDescent() - 2;
						int c1 = metrics.charWidth('&');
						int c2 = metrics.charWidth(';');
						int[] xp, yp;

						xp = new int[] { oldx, oldx + c1, x2 - c2, x2, x2 - c2,
								oldx + c1 };

						yp = new int[] { (yb + yh) / 2, yh, yh, (yb + yh) / 2,
								yb, yb };

						Color c = LineElement.getColor(host,lineError,
								lineSelected, LineElement.ENTITY_BACKGROUND,
								startingOffset, currentOffset);

						if (c != null) {
							g.setColor(c);

							g.fillPolygon(xp, yp, 6);
						}

						g.setColor(LineElement.getColor(host,lineError,
								lineSelected, LineElement.TAG_DELIMITER_START,
								startingOffset, currentOffset));

						g.drawPolygon(xp, yp, 6);

					} else if (le.type == LineElement.TAG_DELIMITER_END) { // >

						p1 = oldx;
						if (p0 != -1) {

							if (le.previousType == LineElement.TAG_ENDER
									|| le.previousType == LineElement.DECLARATION
									|| declarationFlag || dtdFlag ) { // <tag/>

								p1 = x2;

								int yh = y - metrics.getAscent() + 1;
								int yb = y + metrics.getDescent() - 2;

								int[] xp, yp;

								xp = new int[] { p0, p1, p1, p0 };

								yp = new int[] { yh, yh, yb, yb };

								_ = LineElement.TAG_BACKGROUND;
								if (le.previousType == LineElement.DECLARATION
										|| declarationFlag || dtdFlag )
									_ = LineElement.DEC_BACKGROUND;
								Color c = LineElement.getColor(host,lineError,
										lineSelected, _, startingOffset,
										currentOffset);

								if (c != null) {
									
									if ( prefixName != null ) {
										if ( host.hasBackgroundColorForPrefix( prefixName ) )
											c = host.getBackgroundColorForPrefix( prefixName );
									}
									
									g.setColor(c);

									g.fillPolygon(xp, yp, 4);
								}

								g.setColor(LineElement.getColor(host,
										lineError, lineSelected,
										LineElement.TAG_DELIMITER_START,
										startingOffset, currentOffset));

								g.drawPolygon(xp, yp, 4);

							} else {

								if (!endFlag) { // <tag>

									int yh = y - metrics.getAscent() + 1;
									int yb = y + metrics.getDescent() - 2;

									int[] xp, yp;

									xp = new int[] { p0, p1, p1 + (x2 - oldx),
											p1, p0 };

									yp = new int[] { yh, yh, (yb + yh) / 2, yb,
											yb };

									Color c = LineElement.getColor(host,
											lineError, lineSelected,
											LineElement.TAG_BACKGROUND,
											startingOffset, currentOffset);

									if (c != null) {

										if ( prefixName != null ) {
											if ( host.hasBackgroundColorForPrefix( prefixName ) )
												c = host.getBackgroundColorForPrefix( prefixName );
										}
										
										if (startingOffset == startTag)
											c = darker( c );

										g.setColor(c);
										g.fillPolygon(xp, yp, 5);
									}

									g.setColor(LineElement.getColor(host,
											lineError, lineSelected,
											LineElement.TAG_DELIMITER_START,
											startingOffset, currentOffset));

									g.drawPolygon(xp, yp, 5);

								} else { // </tag>

									int yh = y - metrics.getAscent() + 1;
									int yb = y + metrics.getDescent() - 2;

									int[] xp, yp;

									xp = new int[] { p0, p0 + (x2 - oldx),
											p1 + (x2 - oldx), p1 + (x2 - oldx),
											p0 + (x2 - oldx) };

									yp = new int[] { (yb + yh) / 2, yh, yh, yb,
											yb };

									Color c = LineElement.getColor(host,
											lineError, lineSelected,
											LineElement.TAG_BACKGROUND,
											startingOffset, currentOffset);
									if (c != null) {

										if ( prefixName != null ) {
											if ( host.hasBackgroundColorForPrefix( prefixName ) )
												c = host.getBackgroundColorForPrefix( prefixName );
										}
										
										if ((currentOffset - 1) == stopTag) {
											c = darker( c );
										}
																				
										g.setColor(c);

										g.fillPolygon(xp, yp, 5);
									}

									g.setColor(LineElement.getColor(host,
											lineError, lineSelected,
											LineElement.TAG_DELIMITER_START,
											startingOffset, currentOffset));

									g.drawPolygon(xp, yp, 5);

								}

							}
							p0 = -1;

						}
					}

				}

				// Draw text

				for (int i = 0; i < v.size(); i++) {
					LineElement le = (LineElement) v.get(i);
					int type = le.type;

					Color c = LineElement.getColor(host,lineError,
							lineSelected, type, 0, 0);
					if (c == null)
						c = getDefaultColor();

					if (le.type == LineElement.NAMESPACE && le.content != null ) {
						if ( host.hasColorForPrefix( le.content ) )
							c = host.getColorForPrefix(le.content);
					}

					// Start tag

					if ( le.type == LineElement.TAG_DELIMITER_START ) { // <
						p0 = x;
						drawn = false;
					} else if ( le.type == LineElement.TAG_ENDER ) { // /
						if ( p0 > -1 && 
								( le.nextType == LineElement.TAG_DELIMITER_END || 
										le.previousType == LineElement.TEXT ) )
							drawn = false;
						
					} else if ( le.type == LineElement.TAG_DELIMITER_END ) { // >
						drawn = false;
					}

					if ( le.type == LineElement.DECLARATION_START ||
							le.type == LineElement.DECLARATION_END || 
								le.type == LineElement.COMMENT_START || 
									le.type == LineElement.COMMENT_END || 
										le.type == LineElement.DOCTYPE_END || 
											le.type == LineElement.DOCTYPE_START )
						drawn = false;

					if (le.type == LineElement.TAG && le.content != null ) {
						if ( host.hasColorForTag( le.content ) )
							c = host.getColorForTag( le.content );
							if ( i >= 2 ) {
								LineElement le_2 = (LineElement) v.get(i - 2);
								if (le_2.type == LineElement.NAMESPACE) {
									if ( host.hasColorForPrefix( le_2.content ) )
										c = host.getColorForPrefix( le_2.content );
								}
							}
					}

					if (le.type == LineElement.ATTRIBUTE && le.content != null) {
						if ( host.hasColorForAttribute(le.content ) ) {
							c = host.getColorForAttribute( le.content );
						}
					}

					Font f = defFont;
					g.setColor(c);

					line.count = (le.content == null) ? 0 : le.content.length();

					oldx = x;

					if ( drawn ) {

						if (le.type == LineElement.ENTITY) {
							line.count = 1;
							line.offset += line.count;
							offset += line.count;
							x = x + metrics.charWidth( '&' );
							line.count = (le.content.length() - 2);
							x = Utilities.drawTabbedText(line, x, y, g, this,
									offset);
							line.count += 1;
							x = x + metrics.charWidth( ';' );
						} else {
							
							if ( le.type == LineElement.COMMENT_END || 
									le.type == LineElement.CDATA_END || 
									 	le.type == LineElement.DOCTYPE_END || 
											le.type == LineElement.DECLARATION_END ) {
								g.setColor( darker( c ) );
								g.drawLine( 
										x, 
										y - metrics.getAscent() + 2, 
										x, 
										y - 1 );
								g.setColor( c );
							}
														
							x = Utilities.drawTabbedText(line, x, y, g, this,
									offset);
							
							if ( le.type == LineElement.COMMENT_START ||
									le.type == LineElement.CDATA_START || 
										le.type == LineElement.DOCTYPE_START || 
											le.type == LineElement.DECLARATION_START ) {
								g.setColor( darker( c ) );
								g.drawLine( 
										x, 
										y - metrics.getAscent() + 2, 
										x, 
										y - 1 );								
							}

						}

					} else {
						if ( le.content != null )
							x += metrics.stringWidth( le.content );

						drawn = true;
					}

					oldOffset = offset;
					offset += line.count;
					line.offset += line.count;

				}

			}

		} catch ( BadLocationException bl ) {
			// ?
		}
	}

	protected Color getDefaultColor() {
		return getContainer().getForeground();
	}

	////////////////////////////////////////////////////////////////////////////////

	class LineAttribute {
		public Color color;

		public Font font;

		public LineAttribute(Color c, Font f) {
			this.color = c;
			this.font = f;
		}
	}

}
