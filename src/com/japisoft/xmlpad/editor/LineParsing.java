package com.japisoft.xmlpad.editor;

import javax.swing.text.Segment;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.xmlpad.SharedProperties;

import java.util.Arrays;
import java.util.Vector;
import java.util.Hashtable;

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
class LineParsing {

	public LineParsing() {
		super();
		current = new LineElement("", LineElement.TEXT);
		htLineLastAttributes = new Hashtable();
		vElement = new FastVector();
	}

	private FastVector vElement;
	private LineElement current;
	private StringBuffer buffer;

	public void dispose() {
		htLineLastAttributes.clear();
		current = new LineElement("", LineElement.TEXT);
	}

	private void addElement(LineElement e) {
		vElement.add(e);
		this.current = e;
	}

	private void addElement2(LineElement e) {
		vElement.add(e);
	}

	private void deleteLastElement() {
		if (vElement.size() > 0)
			vElement.removeElementAt(vElement.size() - 1);
	}

	private void flushBuffer() {
		flushBuffer(-1);
	}

	private void flushBuffer(int removeLast) {
		if (current == null)
			current = new LineElement(buffer.toString(), LineElement.TEXT);
		else {
			if ( current != LineElement.BLANKELEMENT ) {
				if (removeLast == -1  )
					current.content = buffer.toString();
				else
					current.content = buffer.substring(0, buffer.length()
							- removeLast);
			}
		}
		if (current.content.length() > 0) {
			addElement(current);
		}
		buffer = new StringBuffer();
	}

	private int majorLineElement = LineElement.TEXT;

	private Hashtable htLineLastAttributes;

	private int oldLineLocation;

	private boolean dtdMode = false;

	void setDTDMode(boolean dtdMode) {
		this.dtdMode = dtdMode;
	}
	
	private Vector vTmp;
	
	public FastVector parse(Segment line, int lineLocation) {	
/*		
 * No performance gain and added bug while refreshing !
 * vTmp = LineParsingCache.getParsedLine( line, lineLocation );
		if ( vTmp != null )
			return vTmp;*/

		vElement.removeAllElements();
		buffer = new StringBuffer();
		int start = line.offset;
		int end = (start + line.count);
		boolean bufferize;

		int tmpMajorLineElement = LineElement.TEXT;

		LineElement le = (LineElement) htLineLastAttributes.get(""
				+ (lineLocation - 1));

		if (le != null) {
			current = le;
			
			if ( le.type == LineElement.TAG )
				le.type = LineElement.ATTRIBUTE;
			else
			if ((le.type == LineElement.TAG)
				|| (le.type == LineElement.TAG_DELIMITER_START)
				|| (le.type == LineElement.TAG_DELIMITER_END))
				le.type = LineElement.TEXT;
		} else
			current = new LineElement(LineElement.TEXT);

		LineElement previousStartingTagElement = null;

		if ( current.majorLineElement != 0 ) {
			majorLineElement = current.majorLineElement;
		} else
			if ( dtdMode )
				majorLineElement = LineElement.INNER_DTD;
		
		for (int i = start; i < end; i++) {
			char c = line.array[i];			
			bufferize = true;

			////////////////////////////////////////////////////////////////////////////////////
			/// COMMON PART
			////////////////////////////////////////////////////////////////////////////////////

			if ( current.type == LineElement.DTD_INNER_COMMENT ) {

				if ( c == '-' ) {

					String tmp = buffer.toString();
					if ( tmp.endsWith( "-" ) ) {
						buffer.append( "-" );
						bufferize = false;
						flushBuffer();						
						addElement2( new LineElement( majorLineElement ) );
						current = new LineElement( majorLineElement = current.majorLineElement );
					}

				}

			} else
			if (current.type == LineElement.COMMENT) {

				if ( c == '>' ) {
					String tmp = buffer.toString();
					if ( tmp.endsWith( "--" ) ) {
						
						buffer.delete( buffer.length() - 2, buffer.length() );
						flushBuffer();
						
						addElement( new LineElement( "-->", LineElement.COMMENT_END ) );
						
						if ( current.majorLineElement != 0 )
							majorLineElement = current.majorLineElement;

						addElement2(new LineElement(majorLineElement));
						current = new LineElement(majorLineElement);
						bufferize = false;
					}
				} else {
					buffer.append( c );
					continue;
				}

			} else if ( current.type == LineElement.LITERAL
					|| current.type == LineElement.LITERAL2 ) {

				if ( ( current.type == LineElement.LITERAL && c == '"' )
							|| ( current.type == LineElement.LITERAL2 && c == '\'' ) ) {

					buffer.append( c );
					flushBuffer();
					bufferize = false;
					
					if ( current.majorLineElement != 0 ) {
						majorLineElement = current.majorLineElement;
					}
					
					//LineElement tmpElement = new LineElement( majorLineElement );
					//tmpElement.majorLineElement = majorLineElement;
					//addElement2( tmpElement );
					current = new LineElement( majorLineElement );

				}

			} else {

				////////////////////////////////////////////////////////////////////
				// DTD
				////////////////////////////////////////////////////////////////////

				if ( majorLineElement == LineElement.INNER_DTD || 
						majorLineElement == LineElement.DTD_ENTITY_CONTENT || 
							majorLineElement == LineElement.DTD_ATTRIBUTE_CONTENT || 
								majorLineElement == LineElement.DTD_ELEMENT_CONTENT ) {
					
					switch ( c ) {

					case '%':						
						if ( current.type != LineElement.DTD_ENTITY_CONTENT ) {
							flushBuffer();
							current = new LineElement(
									LineElement.DTD_ENTITY_PARAMETER);							
							current.majorLineElement = majorLineElement;
						}
						break;
					case ';':
						if ( current.type == LineElement.DTD_ENTITY_PARAMETER ) {
							buffer.append(';');
							bufferize = false;
							flushBuffer();
							
							if ( current.majorLineElement != 0 )
								majorLineElement = current.majorLineElement;
							
							addElement2( new LineElement( majorLineElement ) );
							current = new LineElement( majorLineElement );
						}
						break;
					case '<':
						flushBuffer();
						addElement2(new LineElement( "<",
								LineElement.TAG_DELIMITER_START ) );
						current = new LineElement( LineElement.INNER_DTD );
						bufferize = false;
						break;
					case '-':
						if ( current.type == LineElement.DTD_ATTRIBUTE_CONTENT ||
								current.type == LineElement.DTD_ELEMENT_CONTENT ||
									current.type == LineElement.DTD_ENTITY_CONTENT ) {

							// -- sequence
							
							if ( buffer.length() > 0 && buffer.charAt( buffer.length() - 1 ) == '-' ) {
								buffer.delete( buffer.length() - 1, buffer.length() );
								flushBuffer();
								bufferize = false;
								majorLineElement = current.type;
								buffer.append( "--" );
								current = new LineElement( LineElement.DTD_INNER_COMMENT );
								current.majorLineElement = majorLineElement;
							}
							
						} else

						if ( vElement.size() > 0
								&&
									( ( LineElement ) vElement.lastElement() ).type == LineElement.TAG_DELIMITER_START ) {
							if ( buffer.toString().equals( "!-" ) ) {
								deleteLastElement();
								addElement( new LineElement( "<!--",
										LineElement.COMMENT_START ) );
								LineElement tmp = new LineElement( LineElement.COMMENT );
								addElement2( tmp );
								current = new LineElement( LineElement.COMMENT );
								majorLineElement = LineElement.INNER_DTD;
								tmp.majorLineElement = majorLineElement;
								current.majorLineElement = majorLineElement;
								bufferize = false;
								buffer = new StringBuffer();
							}
						}
						break;
					case '"':
						if (current.type == LineElement.LITERAL2) {
							bufferize = true;
							break;
						}
					case '\'':
						if (current != null) {
							if (current.type != LineElement.COMMENT) {

								if (current.type == LineElement.LITERAL
										&& c == '\'') {
									bufferize = true;
								} else if (current.type == LineElement.LITERAL2
										&& c == '"')
									bufferize = true;
								else {
									
									flushBuffer();

									majorLineElement = current.type;
									current = new LineElement(
											"",
											tmpMajorLineElement = (c == '"' ? LineElement.LITERAL
													: LineElement.LITERAL2));
									
									current.majorLineElement = majorLineElement;
								}
							} else {
								bufferize = true;
							}
						}
						break;
					case '\t':
					case '\n':
					case ' ':
						String _ = buffer.toString();
						if ( "!ELEMENT".equals( _ ) ) {
							current.type = LineElement.DTD_ELEMENT;
							flushBuffer();
							current = new LineElement(
									majorLineElement = LineElement.DTD_ELEMENT_CONTENT);
						} else if ( "!ATTLIST".equals( _ ) ) {
							current.type = LineElement.DTD_ATTRIBUTE;
							flushBuffer();
							current = new LineElement(
									majorLineElement = LineElement.DTD_ATTRIBUTE_CONTENT);
						} else if ( "!ENTITY".equals( _ ) ) {
							current.type = LineElement.DTD_ENTITY;
							flushBuffer();
							current = new LineElement(
									majorLineElement = LineElement.DTD_ENTITY_CONTENT);
						} else if ( "!NOTATION".equals( _ ) ) {
							current.type = LineElement.DTD_NOTATION;
							flushBuffer();
							current = new LineElement( LineElement.INNER_DTD );
						}
						break;
					case '>':
						flushBuffer();
						addElement(new LineElement( ">",
								LineElement.TAG_DELIMITER_END ) );
						bufferize = false;
						addElement2( new LineElement( LineElement.INNER_DTD ) );
						current = new LineElement( LineElement.INNER_DTD );
						break;
					case ']':
						if (current.type == LineElement.INNER_DTD && !dtdMode ) {
							majorLineElement = LineElement.DOCTYPE;
							flushBuffer();
							current = new LineElement(LineElement.DOCTYPE);
						}
						break;
					}

				} else {

					///////////////////////////////////////////////////////////////////////
					// XML
					///////////////////////////////////////////////////////////////////////

					if (current.type != LineElement.CDATA
							&& current.type != LineElement.DOCTYPE) {

						switch (c) {
						case ':':
							if (current.type == LineElement.TAG) {
								current.type = LineElement.NAMESPACE;
								flushBuffer();
								addElement(new LineElement(":",
										LineElement.TAG_ENDER));
								bufferize = false;
								current = new LineElement(LineElement.TAG);
							}
							break;
						case '<':
							flushBuffer();
							addElement(previousStartingTagElement = new LineElement(
									"<", LineElement.TAG_DELIMITER_START ) );
							current = new LineElement( LineElement.TAG );
							bufferize = false;
							break;
						case '>':
							if (majorLineElement == LineElement.DOCTYPE_END) {
								flushBuffer();
								addElement(new LineElement(">",
										LineElement.DOCTYPE_END));
								bufferize = false;
								majorLineElement = 0;
							} else {

								/*
								 * <!DOCTYPE r SYSTEM [ <!ELEMENT a>]> */
								if (current.type == LineElement.DECLARATION) {
									if (buffer != null && buffer.length() > 0) {
										if (buffer.charAt(buffer.length() - 1) == '?') {
											buffer
													.deleteCharAt(buffer
															.length() - 1);
											flushBuffer();
											addElement(new LineElement("?>",
													LineElement.DECLARATION_END));
											current = new LineElement(
													LineElement.TEXT);
											bufferize = false;
											break;
										}
									}
								}

								if (current.type != LineElement.COMMENT) {
									flushBuffer();

									if (previousStartingTagElement != null) {
										previousStartingTagElement.type = LineElement.TAG_DELIMITER_START;
									}

									addElement(new LineElement(">",
											LineElement.TAG_DELIMITER_END));
								} else {
									buffer.append(">");
									flushBuffer();
								}

								current = new LineElement(LineElement.TEXT);
								majorLineElement = LineElement.TEXT;
								bufferize = false;

							}

							break;
						case ' ':
						case '\t':
						case '\n':
							if (current != null) {
								if (current.type == LineElement.TAG) {
									flushBuffer();
									current = new LineElement(
											LineElement.ATTRIBUTE);
									bufferize = false;
									addElement2(c == ' ' ? LineElement.BLANKELEMENT
											: new LineElement("" + c,
													LineElement.TEXT));
									majorLineElement = LineElement.TAG;
								} else if (current.type == LineElement.ATTRIBUTE) {
									bufferize = false;
									addElement2(new LineElement("" + c,
											LineElement.TEXT));
								} else if (current.type == LineElement.ENTITY) {
									current.type = LineElement.TEXT;
								}
							}
							break;
						case '&':
							if (current != null)
								if (current.type == LineElement.TEXT
										|| current.type == LineElement.LITERAL
										|| current.type == LineElement.LITERAL2) {
									flushBuffer();
									current = new LineElement(
											LineElement.ENTITY);
								}
							break;
						case ';':
							if (current != null) {
								if (current.type == LineElement.ENTITY) {
									buffer.append(";");
									bufferize = false;
									flushBuffer();
/*									if (tmpMajorLineElement == LineElement.LITERAL
											|| tmpMajorLineElement == LineElement.LITERAL2)
										current = new LineElement(
												tmpMajorLineElement);
									else */
										current = new LineElement(
												LineElement.TEXT);
								}
								break;
							}
						case '/':
							
							if (current != null) {
								if (current.type == LineElement.TAG
										|| current.type == LineElement.ATTRIBUTE) {
									flushBuffer();
									addElement(new LineElement("/",
											LineElement.TAG_ENDER));
									if (current.type == LineElement.ATTRIBUTE)
										current = new LineElement(
												LineElement.TEXT);
									else {
										current = new LineElement(
												LineElement.TAG);
									}
									bufferize = false;
								}
							}
							
							break;
						case '"':
							
							if ( current.type == LineElement.LITERAL2 ) {
								bufferize = true;
								break;
							}
							
						case '\'':
							
							if ( current != null ) {
								
								if ( current.type != LineElement.COMMENT
										&& current.type != LineElement.CDATAORCOMMENTORDOCTYPE
											&& current.type != LineElement.TEXT ) {

									if ( ( current.type == LineElement.LITERAL && c == '"' )
											|| ( current.type == LineElement.LITERAL2 && c == '\'' ) ) {
										
										buffer.append( c );
										flushBuffer();
										bufferize = false;

										tmpMajorLineElement = LineElement.TEXT;

										if ( majorLineElement == LineElement.TAG )
											current = new LineElement(
													LineElement.ATTRIBUTE );
										else
											current = new LineElement(
													majorLineElement );

									} else {

										if ( current.type == LineElement.LITERAL
												&& c == '\'' ) {
											bufferize = true;
										} else if ( current.type == LineElement.LITERAL2
												&& c == '"' )
											bufferize = true;
										else {

											flushBuffer();
											current = new LineElement(
													"",
													tmpMajorLineElement = ( c == '"' ? LineElement.LITERAL
															: LineElement.LITERAL2 ) );

										}
									}

								} else {

									bufferize = true;

								}
							}
							break;

						case '=':
							if (current.type == LineElement.ATTRIBUTE) {
								flushBuffer();
								addElement(new LineElement("=",
										LineElement.ATTRIBUTE_SEPARATOR));
								bufferize = false;
								current = new LineElement(LineElement.ATTRIBUTE);
							}
							break;

						case 'E':
							if (current.type == LineElement.CDATAORCOMMENTORDOCTYPE) {
								if (buffer.toString().equals("!DOCTYP")) {
									deleteLastElement();
									flushBuffer(7);
									bufferize = false;
									addElement(new LineElement("<!DOCTYPE",
											LineElement.DOCTYPE_START));
									majorLineElement = LineElement.DOCTYPE;
									current = new LineElement(
											LineElement.DOCTYPE);
								}
							}
							break;
						case '!':
							if ( current.type == LineElement.TAG ) {
								current = new LineElement(
										LineElement.CDATAORCOMMENTORDOCTYPE);
								majorLineElement = LineElement.CDATAORCOMMENTORDOCTYPE;
							}
							break;
						case '-':
							if ( current.type == LineElement.CDATAORCOMMENTORDOCTYPE ) {
								if (buffer.toString().equals("!-")) {
									deleteLastElement();
									addElement( new LineElement("<!--",
											LineElement.COMMENT_START ) );
									addElement( new LineElement( LineElement.COMMENT ) );
									current = new LineElement(
											LineElement.COMMENT );
									majorLineElement = LineElement.TEXT;
									current.majorLineElement = LineElement.TEXT;
									bufferize = false;
									buffer = new StringBuffer();
								}
							}
							break;
						case '?':
							if (current.type == LineElement.TAG) {
								deleteLastElement();
								addElement(new LineElement("<?",
										LineElement.DECLARATION_START));
								current = new LineElement(
										LineElement.DECLARATION);
								majorLineElement = LineElement.DECLARATION;
								bufferize = false;
							}
							break;
						case '[':
							if (buffer.length() >= 7) {
								if (buffer.subSequence(0, 7).toString()
										.startsWith("![CDATA")) {
									deleteLastElement();
									addElement(new LineElement("<![CDATA[",
											LineElement.CDATA_START));
									majorLineElement = LineElement.CDATA;
									current = new LineElement(LineElement.CDATA);
									buffer.delete(0, 8);
									bufferize = false;
								}
							}

							break;
						}
					} else {

						////////////////////////////////////////////////////////////////////
						// COMMENT , DOCTYPE, CDATA
						////////////////////////////////////////////////////////////////////

						if (c == '[') {
							if (majorLineElement == LineElement.DOCTYPE) {
								majorLineElement = LineElement.INNER_DTD;
								buffer.append('[');
								bufferize = false;
								flushBuffer();
								current = new LineElement(LineElement.INNER_DTD);
								addElement(current);
							}
						}

						if (c == '>') {
							boolean commentEnd = false;
							boolean cdataEnd = false;
							String tmp = buffer.toString();

							if ((current.type == LineElement.COMMENT && (commentEnd = tmp
									.endsWith("--")))
									|| (current.type == LineElement.CDATA && (cdataEnd = tmp
											.endsWith("]]")))
									|| (current.type == LineElement.DOCTYPE)) {

								bufferize = false;
								flushBuffer(current.type != LineElement.DOCTYPE ? 2
										: -1);

								if (commentEnd)
									addElement(new LineElement("-->",
											LineElement.COMMENT_END));
								else if (cdataEnd)
									addElement(new LineElement("]]>",
											LineElement.CDATA_END));
								else if (current.type == LineElement.DOCTYPE) {
									addElement(new LineElement(">",
											LineElement.DOCTYPE_END));
								}

								current = new LineElement(LineElement.TEXT);
								vElement.add(new LineElement(
										LineElement.TEXT));
								majorLineElement = LineElement.TEXT;
							}
						}
					}

				}

			}

			if ( bufferize )
				buffer.append( ( char )  c );

		}
		
		flushBuffer();
		
		if (majorLineElement == LineElement.TAG ) {
			if ( current.type != LineElement.LITERAL2 && 
					current.type != LineElement.LITERAL )
				vElement.add( new LineElement( LineElement.TAG ) );
			else {
				current = new LineElement( current.type );
				vElement.add(new LineElement( current.type ));
			}
		}
		
		if ( vElement.size() > 0 ) {
			LineElement last = ( LineElement ) vElement.lastElement();
			if ( last.majorLineElement == 0 )
				last.majorLineElement = majorLineElement;
			
			htLineLastAttributes.put( "" + lineLocation, last );

			if ( le != null )
				if ( le.type == last.type )
					last.majorLineElement = le.majorLineElement;
			
			if ( !SharedProperties.FULL_TEXT_VIEW ) {

				if (last.type == LineElement.TAG
						|| last.type == LineElement.ATTRIBUTE
						|| last.type == LineElement.CDATAORCOMMENTORDOCTYPE) {

					if (previousStartingTagElement != null) {
						previousStartingTagElement.type = LineElement.TAG_DELIMITER_START;
					}

					vElement.add(new LineElement(
							LineElement.TAG_DELIMITER_END));

				} else if (last.type == LineElement.COMMENT) {
					vElement
							.add(new LineElement(LineElement.COMMENT_END));
				} else if (last.type == LineElement.CDATA) {
					vElement.add(new LineElement(LineElement.CDATA_END));
				} else if (last.type == LineElement.DOCTYPE) {
					vElement
							.add(new LineElement(LineElement.DOCTYPE_END));
				}

				LineElement previousLine = (LineElement) htLineLastAttributes
						.get("" + (lineLocation - 1));

				if ( previousLine != null ) {

					if (previousLine.type == LineElement.TAG
							|| previousLine.type == LineElement.ATTRIBUTE
							|| previousLine.type == LineElement.CDATAORCOMMENTORDOCTYPE) {
						vElement.insertElementAt(new LineElement(
								LineElement.TAG_DELIMITER_START), 0);
					} else if (previousLine.type == LineElement.COMMENT) {
						vElement.insertElementAt(new LineElement(
								LineElement.COMMENT_START), 0);
					} else if (previousLine.type == LineElement.CDATA) {
						vElement.insertElementAt(new LineElement(
								LineElement.CDATA_START), 0);
					} else if (previousLine.type == LineElement.DOCTYPE) {
						vElement.insertElementAt(new LineElement(
								LineElement.DOCTYPE_START), 0);
					}
				}

			}

		} else if (current != null) {
			if ( current.majorLineElement == 0 )
				current.majorLineElement = majorLineElement;
			htLineLastAttributes.put("" + lineLocation, current);
		}

		// System.out.println( vElement );

/*
 * No performance gain
 * LineParsingCache.updateCache( line, lineLocation, vElement );
 */		

		return vElement;
	}

	int getLastType(int line) {
		if (htLineLastAttributes == null)
			return LineElement.TEXT;
		LineElement le = (LineElement) htLineLastAttributes.get("" + line);
		if (le == null)
			return LineElement.TEXT;
		return le.type;
	}

	//////////////////////////////////////////////////////////////////////////////////////

	// We needn't to garbage the content of the vector when
	// Removing all the content

	class FastReusableVector extends Vector {

		public void removeAllElements() {
			modCount++;
			elementCount = 0;
			oldElement = null;
		}

		private LineElement oldElement = null;

		public void addLineElement(LineElement element) {
			if (oldElement != null) {
				element.previousType = oldElement.type;
				oldElement.nextType = element.type;
			}
			super.addElement(element);
			oldElement = element;
		}

		// Check nothing
		
		@Override
		public Object get(int index) {
			return elementData[index];
		}
		
		@Override
		public void addElement(Object obj) {
			modCount++;
			int oldCapacity = elementData.length;
			if (elementCount + 1 > oldCapacity) {
			    Object[] oldData = elementData;
			    int newCapacity = (capacityIncrement > 0) ?
				(oldCapacity + capacityIncrement) : (oldCapacity * 2);
		    	    if (newCapacity < elementCount + 1) {
				newCapacity = elementCount + 1;
			    }
		    	elementData = Arrays.copyOf(elementData, newCapacity);
			}
			elementData[elementCount++] = obj;
		}
		
	}

	public static void main(String[] args) {		
//		String[] ss = { 
//				"<a v='2.0'", "\na='3'/>" };				

		String[] ss = {				
				"<a ii=\"1 ",
				"oooooooooppppppjj\"/>"				
		};
						
		LineParsing parsing = new LineParsing();
		parsing.setDTDMode( false );

		for (int i = 0; i < ss.length; i++) {
			Segment s = new Segment(ss[i].toCharArray(), 0, ss[i].length());
			System.out.println(parsing.parse(s, i));
		}
	}

}

// LineParsing ends here
