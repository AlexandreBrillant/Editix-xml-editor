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

import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.xmlpad.SharedProperties;
import java.util.Hashtable;
import java.util.Map;

/**
 * Cut the line in drawable element
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.3
 */
class LineParsing2 {

	public LineParsing2() {
		super();
		current = new LineToken("", LineToken.TEXT);
		lastElementByLine = new Hashtable<Integer,LineToken>();
		vElement = new FastArrayList(500);
	}

	private FastArrayList vElement;
	private LineToken current;
	private StringBuffer buffer;

	private boolean inComment = false;

	public void dispose() {
		lastElementByLine.clear();
		current = new LineToken("", LineToken.TEXT);
	}

	private void addElement(LineToken e) {
		vElement.add(e);
		this.current = e;
	}

	private void addElement2(LineToken e) {
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
			current = new LineToken(buffer.toString(), LineToken.TEXT);
		else {
			if ( current != LineToken.BLANKELEMENT ) {
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

	private int majorLineElement = LineToken.TEXT;

	private Map<Integer,LineToken> lastElementByLine;

	private int oldLineLocation;

	private boolean dtdMode = false;

	void setDTDMode(boolean dtdMode) {
		this.dtdMode = dtdMode;
	}
	
	private LineToken oldElement = new LineToken( LineToken.TEXT );
	
	private LineToken parserState;
	
	public FastArrayList parse(char[] chars, int start, int end, int lineLocation, boolean startLine ) {

		vElement.removeAllElements();
		buffer = new StringBuffer();
		boolean bufferize;

		int tmpMajorLineElement = LineToken.TEXT;
		
		if ( inComment )
			tmpMajorLineElement = LineToken.COMMENT;

/*
		// LineElement previousElement = (LineElement) lastElementByLine.get( ( lineLocation - 1 ) );

//		LineElement previousElement = parserState;
		
		if (previousElement != null) {
			current = oldElement;
			current.type = previousElement.type;
			current.content = previousElement.content;
			
			if ( current.type == LineElement.TAG )
				current.type = LineElement.ATTRIBUTE;
			else
			if ((current.type == LineElement.TAG)
				|| (current.type == LineElement.TAG_DELIMITER_START)
				|| (current.type == LineElement.TAG_DELIMITER_END))
				current.type = LineElement.TEXT;
		} else
			current = new LineElement(LineElement.TEXT);
*/

		
		current = new LineToken( tmpMajorLineElement );

		LineToken previousStartingTagElement = null;

		if ( current.majorLineElement != 0 ) {
			majorLineElement = current.majorLineElement;
		} else
			if ( dtdMode )
				majorLineElement = LineToken.INNER_DTD;
		
		for (int i = start; i < end; i++) {
			char c = chars[i];			
			bufferize = true;

			if ( current.type == LineToken.DTD_INNER_COMMENT ) {

				if ( c == '-' ) {

					String tmp = buffer.toString();
					if ( tmp.endsWith( "-" ) ) {
						buffer.append( "-" );
						bufferize = false;
						flushBuffer();						
						addElement2( new LineToken( majorLineElement ) );
						current = new LineToken( majorLineElement = current.majorLineElement );
					}

				}

			} else
			if (current.type == LineToken.COMMENT) {

				if ( c == '>' ) {
					String tmp = buffer.toString();
					if ( tmp.endsWith( "--" ) ) {
						
						buffer.delete( buffer.length() - 2, buffer.length() );
						flushBuffer();
						
						addElement( new LineToken( "-->", LineToken.COMMENT_END ) );
						
						if ( current.majorLineElement != 0 )
							majorLineElement = current.majorLineElement;

						addElement2(new LineToken(majorLineElement));
						current = new LineToken(majorLineElement);
						bufferize = false;
					}
				} else {
					buffer.append( c );
					continue;
				}

			} else if ( current.type == LineToken.LITERAL
					|| current.type == LineToken.LITERAL2 ) {

				if ( ( current.type == LineToken.LITERAL && c == '"' )
							|| ( current.type == LineToken.LITERAL2 && c == '\'' ) ) {

					buffer.append( c );
					flushBuffer();
					bufferize = false;
					
					if ( current.majorLineElement != 0 ) {
						majorLineElement = current.majorLineElement;
					}
	
					current = new LineToken( majorLineElement );

				}

			} else {

				////////////////////////////////////////////////////////////////////
				// DTD
				////////////////////////////////////////////////////////////////////

				if ( majorLineElement == LineToken.INNER_DTD || 
						majorLineElement == LineToken.DTD_ENTITY_CONTENT || 
							majorLineElement == LineToken.DTD_ATTRIBUTE_CONTENT || 
								majorLineElement == LineToken.DTD_ELEMENT_CONTENT ) {
					
					switch ( c ) {

					case '%':						
						if ( current.type != LineToken.DTD_ENTITY_CONTENT ) {
							flushBuffer();
							current = new LineToken(
									LineToken.DTD_ENTITY_PARAMETER);							
							current.majorLineElement = majorLineElement;
						}
						break;
					case ';':
						if ( current.type == LineToken.DTD_ENTITY_PARAMETER ) {
							buffer.append(';');
							bufferize = false;
							flushBuffer();
							
							if ( current.majorLineElement != 0 )
								majorLineElement = current.majorLineElement;
							
							addElement2( new LineToken( majorLineElement ) );
							current = new LineToken( majorLineElement );
						}
						break;
					case '<':
						flushBuffer();
						addElement2(new LineToken( "<",
								LineToken.TAG_DELIMITER_START ) );
						current = new LineToken( LineToken.INNER_DTD );
						bufferize = false;
						break;
					case '-':
						if ( current.type == LineToken.DTD_ATTRIBUTE_CONTENT ||
								current.type == LineToken.DTD_ELEMENT_CONTENT ||
									current.type == LineToken.DTD_ENTITY_CONTENT ) {

							// -- sequence
							
							if ( buffer.length() > 0 && buffer.charAt( buffer.length() - 1 ) == '-' ) {
								buffer.delete( buffer.length() - 1, buffer.length() );
								flushBuffer();
								bufferize = false;
								majorLineElement = current.type;
								buffer.append( "--" );
								current = new LineToken( LineToken.DTD_INNER_COMMENT );
								current.majorLineElement = majorLineElement;
							}
							
						} else

						if ( vElement.size() > 0
								&&
									( ( LineToken ) vElement.lastElement() ).type == LineToken.TAG_DELIMITER_START ) {
							if ( buffer.toString().equals( "!-" ) ) {
								deleteLastElement();
								addElement( new LineToken( "<!--",
										LineToken.COMMENT_START ) );
								LineToken tmp = new LineToken( LineToken.COMMENT );
								addElement2( tmp );
								current = new LineToken( LineToken.COMMENT );
								majorLineElement = LineToken.INNER_DTD;
								tmp.majorLineElement = majorLineElement;
								current.majorLineElement = majorLineElement;
								bufferize = false;
								buffer = new StringBuffer();

							}
						}
						break;
					case '"':
						if (current.type == LineToken.LITERAL2) {
							bufferize = true;
							break;
						}
					case '\'':
						if (current != null) {
							if (current.type != LineToken.COMMENT) {

								if (current.type == LineToken.LITERAL
										&& c == '\'') {
									bufferize = true;
								} else if (current.type == LineToken.LITERAL2
										&& c == '"')
									bufferize = true;
								else {
									
									flushBuffer();

									majorLineElement = current.type;
									current = new LineToken(
											"",
											tmpMajorLineElement = (c == '"' ? LineToken.LITERAL
													: LineToken.LITERAL2));
									
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
						String __ = buffer.toString();
						if ( "!ELEMENT".equals( __ ) ) {
							current.type = LineToken.DTD_ELEMENT;
							flushBuffer();
							current = new LineToken(
									majorLineElement = LineToken.DTD_ELEMENT_CONTENT);
						} else if ( "!ATTLIST".equals( __ ) ) {
							current.type = LineToken.DTD_ATTRIBUTE;
							flushBuffer();
							current = new LineToken(
									majorLineElement = LineToken.DTD_ATTRIBUTE_CONTENT);
						} else if ( "!ENTITY".equals( __ ) ) {
							current.type = LineToken.DTD_ENTITY;
							flushBuffer();
							current = new LineToken(
									majorLineElement = LineToken.DTD_ENTITY_CONTENT);
						} else if ( "!NOTATION".equals( __ ) ) {
							current.type = LineToken.DTD_NOTATION;
							flushBuffer();
							current = new LineToken( LineToken.INNER_DTD );
						}
						break;
					case '>':
						flushBuffer();
						addElement(new LineToken( ">",
								LineToken.TAG_DELIMITER_END ) );
						bufferize = false;
						addElement2( new LineToken( LineToken.INNER_DTD ) );
						current = new LineToken( LineToken.INNER_DTD );
						break;
					case ']':
						if (current.type == LineToken.INNER_DTD && !dtdMode ) {
							majorLineElement = LineToken.DOCTYPE;
							flushBuffer();
							current = new LineToken(LineToken.DOCTYPE);
						}
						break;
					}

				} else {

					///////////////////////////////////////////////////////////////////////
					// XML
					///////////////////////////////////////////////////////////////////////

					if (current.type != LineToken.CDATA
							&& current.type != LineToken.DOCTYPE) {

						switch (c) {
						case ':':
							if (current.type == LineToken.TAG) {
								current.type = LineToken.NAMESPACE;
								flushBuffer();
								addElement(new LineToken(":",
										LineToken.TAG_ENDER));
								bufferize = false;
								current = new LineToken(LineToken.TAG);
							}
							break;
						case '<':
							flushBuffer();
							addElement(previousStartingTagElement = new LineToken(
									"<", LineToken.TAG_DELIMITER_START ) );
							current = new LineToken( LineToken.TAG );
							bufferize = false;
							break;
						case '>':
							if (majorLineElement == LineToken.DOCTYPE_END) {
								flushBuffer();
								addElement(new LineToken(">",
										LineToken.DOCTYPE_END));
								bufferize = false;
								majorLineElement = 0;
							} else {

								/*
								 * <!DOCTYPE r SYSTEM [ <!ELEMENT a>]> */
								if (current.type == LineToken.DECLARATION) {
									if (buffer != null && buffer.length() > 0) {
										if (buffer.charAt(buffer.length() - 1) == '?') {
											buffer
													.deleteCharAt(buffer
															.length() - 1);
											flushBuffer();
											addElement(new LineToken("?>",
													LineToken.DECLARATION_END));
											current = new LineToken(
													LineToken.TEXT);
											bufferize = false;
											break;
										}
									}
								}

								if (current.type != LineToken.COMMENT) {
									flushBuffer();

									if (previousStartingTagElement != null) {
										previousStartingTagElement.type = LineToken.TAG_DELIMITER_START;
									}

									addElement(new LineToken(">",
											LineToken.TAG_DELIMITER_END));
								} else {
									buffer.append(">");
									flushBuffer();
								}

								current = new LineToken(LineToken.TEXT);
								majorLineElement = LineToken.TEXT;
								bufferize = false;

							}

							break;
						case ' ':
						case '\t':
						case '\n':
							if (current != null) {
								if (current.type == LineToken.TAG) {
									flushBuffer();
									current = new LineToken(
											LineToken.ATTRIBUTE);
									bufferize = false;
									addElement2(c == ' ' ? LineToken.BLANKELEMENT
											: new LineToken("" + c,
													LineToken.TEXT));
									majorLineElement = LineToken.TAG;
								} else if (current.type == LineToken.ATTRIBUTE) {
									bufferize = false;
									addElement2(new LineToken("" + c,
											LineToken.TEXT));
								} else if (current.type == LineToken.ENTITY) {
									current.type = LineToken.TEXT;
								}
							}
							break;
						case '&':
							if (current != null)
								if (current.type == LineToken.TEXT
										|| current.type == LineToken.LITERAL
										|| current.type == LineToken.LITERAL2) {
									flushBuffer();
									current = new LineToken(
											LineToken.ENTITY);
								}
							break;
						case ';':
							if (current != null) {
								if (current.type == LineToken.ENTITY) {
									buffer.append(";");
									bufferize = false;
									flushBuffer();
/*									if (tmpMajorLineElement == LineElement.LITERAL
											|| tmpMajorLineElement == LineElement.LITERAL2)
										current = new LineElement(
												tmpMajorLineElement);
									else */
										current = new LineToken(
												LineToken.TEXT);
								}
								break;
							}
						case '/':
							
							if (current != null) {
								if (current.type == LineToken.TAG
										|| current.type == LineToken.ATTRIBUTE) {
									flushBuffer();
									addElement(new LineToken("/",
											LineToken.TAG_ENDER));
									if (current.type == LineToken.ATTRIBUTE)
										current = new LineToken(
												LineToken.TEXT);
									else {
										current = new LineToken(
												LineToken.TAG);
									}
									bufferize = false;
								}
							}
							
							break;
						case '"':
							
							if ( current.type == LineToken.LITERAL2 ) {
								bufferize = true;
								break;
							}
							
						case '\'':
							
							if ( current != null ) {
								
								if ( current.type != LineToken.COMMENT
										&& current.type != LineToken.CDATAORCOMMENTORDOCTYPE
											&& current.type != LineToken.TEXT ) {

									if ( ( current.type == LineToken.LITERAL && c == '"' )
											|| ( current.type == LineToken.LITERAL2 && c == '\'' ) ) {
										
										buffer.append( c );
										flushBuffer();
										bufferize = false;

										tmpMajorLineElement = LineToken.TEXT;

										if ( majorLineElement == LineToken.TAG )
											current = new LineToken(
													LineToken.ATTRIBUTE );
										else
											current = new LineToken(
													majorLineElement );

									} else {

										if ( current.type == LineToken.LITERAL
												&& c == '\'' ) {
											bufferize = true;
										} else if ( current.type == LineToken.LITERAL2
												&& c == '"' )
											bufferize = true;
										else {

											flushBuffer();
											current = new LineToken(
													"",
													tmpMajorLineElement = ( c == '"' ? LineToken.LITERAL
															: LineToken.LITERAL2 ) );

										}
									}

								} else {

									bufferize = true;

								}
							}
							break;

						case '=':
							if (current.type == LineToken.ATTRIBUTE) {
								flushBuffer();
								addElement(new LineToken("=",
										LineToken.ATTRIBUTE_SEPARATOR));
								bufferize = false;
								current = new LineToken(LineToken.ATTRIBUTE);
							}
							break;

						case 'E':
							if (current.type == LineToken.CDATAORCOMMENTORDOCTYPE) {
								if (buffer.toString().equals("!DOCTYP")) {
									deleteLastElement();
									flushBuffer(7);
									bufferize = false;
									addElement(new LineToken("<!DOCTYPE",
											LineToken.DOCTYPE_START));
									majorLineElement = LineToken.DOCTYPE;
									current = new LineToken(
											LineToken.DOCTYPE);
								}
							}
							break;
						case '!':
							if ( current.type == LineToken.TAG ) {
								current = new LineToken(
										LineToken.CDATAORCOMMENTORDOCTYPE);
								majorLineElement = LineToken.CDATAORCOMMENTORDOCTYPE;
							}
							break;
						case '-':
							if ( current.type == LineToken.CDATAORCOMMENTORDOCTYPE ) {
								if (buffer.toString().equals("!-")) {
									deleteLastElement();
									addElement( new LineToken("<!--",
											LineToken.COMMENT_START ) );
									addElement( new LineToken( LineToken.COMMENT ) );
									current = new LineToken(
											LineToken.COMMENT );
									majorLineElement = LineToken.TEXT;
									current.majorLineElement = LineToken.TEXT;
									bufferize = false;
									buffer = new StringBuffer();
								}
							}
							break;
						case '?':
							if (current.type == LineToken.TAG) {
								deleteLastElement();
								addElement(new LineToken("<?",
										LineToken.DECLARATION_START));
								current = new LineToken(
										LineToken.DECLARATION);
								majorLineElement = LineToken.DECLARATION;
								bufferize = false;
							}
							break;
						case '[':
							if (buffer.length() >= 7) {
								if (buffer.subSequence(0, 7).toString()
										.startsWith("![CDATA")) {
									deleteLastElement();
									addElement(new LineToken("<![CDATA[",
											LineToken.CDATA_START));
									majorLineElement = LineToken.CDATA;
									current = new LineToken(LineToken.CDATA);
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
							if (majorLineElement == LineToken.DOCTYPE) {
								majorLineElement = LineToken.INNER_DTD;
								buffer.append('[');
								bufferize = false;
								flushBuffer();
								current = new LineToken(LineToken.INNER_DTD);
								addElement(current);
							}
						}

						if (c == '>') {
							boolean commentEnd = false;
							boolean cdataEnd = false;
							String tmp = buffer.toString();

							if ((current.type == LineToken.COMMENT && (commentEnd = tmp
									.endsWith("--")))
									|| (current.type == LineToken.CDATA && (cdataEnd = tmp
											.endsWith("]]")))
									|| (current.type == LineToken.DOCTYPE)) {

								bufferize = false;
								flushBuffer(current.type != LineToken.DOCTYPE ? 2
										: -1);

								if (commentEnd)
									addElement(new LineToken("-->",
											LineToken.COMMENT_END));
								else if (cdataEnd)
									addElement(new LineToken("]]>",
											LineToken.CDATA_END));
								else if (current.type == LineToken.DOCTYPE) {
									addElement(new LineToken(">",
											LineToken.DOCTYPE_END));
								}

								current = new LineToken(LineToken.TEXT);
								vElement.add(new LineToken(
										LineToken.TEXT));
								majorLineElement = LineToken.TEXT;
							}
						}
					}

				}

			}

			if ( bufferize )
				buffer.append( ( char )  c );

		}
		
		flushBuffer();
		
		if (majorLineElement == LineToken.TAG ) {
			if ( current.type != LineToken.LITERAL2 && 
					current.type != LineToken.LITERAL )
				vElement.add( new LineToken( LineToken.TAG ) );
			else {
				current = new LineToken( current.type );
				vElement.add(new LineToken( current.type ));
			}
		}
		
		if ( vElement.size() > 0 ) {
			LineToken last = ( LineToken ) vElement.lastElement();
			if ( last.majorLineElement == 0 )
				last.majorLineElement = majorLineElement;
		
			lastElementByLine.put( lineLocation, last );

			if ( !SharedProperties.FULL_TEXT_VIEW ) {

				if (last.type == LineToken.TAG
						|| last.type == LineToken.ATTRIBUTE
						|| last.type == LineToken.CDATAORCOMMENTORDOCTYPE) {

					if (previousStartingTagElement != null) {
						previousStartingTagElement.type = LineToken.TAG_DELIMITER_START;
					}

					vElement.add(new LineToken(
							LineToken.TAG_DELIMITER_END));

				} else if (last.type == LineToken.COMMENT) {
					vElement
							.add(new LineToken(LineToken.COMMENT_END));
				} else if (last.type == LineToken.CDATA) {
					vElement.add(new LineToken(LineToken.CDATA_END));
				} else if (last.type == LineToken.DOCTYPE) {
					vElement
							.add(new LineToken(LineToken.DOCTYPE_END));
				}

				LineToken previousLine = (LineToken) lastElementByLine
						.get((lineLocation - 1));

				if ( previousLine != null ) {

					if (previousLine.type == LineToken.TAG
							|| previousLine.type == LineToken.ATTRIBUTE
							|| previousLine.type == LineToken.CDATAORCOMMENTORDOCTYPE) {
						vElement.insertElementAt(new LineToken(
								LineToken.TAG_DELIMITER_START), 0);
					} else if (previousLine.type == LineToken.COMMENT) {
						vElement.insertElementAt(new LineToken(
								LineToken.COMMENT_START), 0);
					} else if (previousLine.type == LineToken.CDATA) {
						vElement.insertElementAt(new LineToken(
								LineToken.CDATA_START), 0);
					} else if (previousLine.type == LineToken.DOCTYPE) {
						vElement.insertElementAt(new LineToken(
								LineToken.DOCTYPE_START), 0);
					}
				}

			}

		} else if (current != null) {
			if ( current.majorLineElement == 0 )
				current.majorLineElement = majorLineElement;
			lastElementByLine.put( lineLocation, current );
			
			parserState = current;
		}

		
		
		
		return vElement;
	}

	int getLastType(int line) {
		if (lastElementByLine == null)
			return LineToken.TEXT;
		LineToken le = (LineToken) lastElementByLine.get( line );
		if (le == null)
			return LineToken.TEXT;
		return le.type;
	}

}

