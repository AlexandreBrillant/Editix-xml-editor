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

import java.util.HashMap;
import java.util.Map;

import com.japisoft.framework.collection.FastArrayList;

/**
 * Cut the line in drawable element
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 2.0
 */
class LineTokenizer {

	private FastArrayList elements;

	public LineTokenizer() {
		super();
		elements = new FastArrayList(500);
	}
	

	private char quoteChar;	
	boolean inComment = false;
	Map<Integer,Integer> stateByLine = null;
	
	public FastArrayList parse(char[] chars, int start, int end, int lineLocation, int screenLine ) {
		elements.removeAllElements();
		int len = end - start;
		if ( len <= 0 ) return elements;
		
		int state = LineToken.TEXT;

		if ( stateByLine != null ) {
			if ( stateByLine.containsKey( screenLine - 1 ) )
				state = stateByLine.get( screenLine - 1 );
		}
		
		if ( inComment )
			state = LineToken.COMMENT;
		int tokenStart = start;
		char currentQuote = quoteChar;
		
		for ( int i = start; i< end; i++ ) {
			char c = chars[ i ];
		
			switch( state ) {
				case LineToken.TEXT:
					if ( c == '<' ) {
						if ( i > tokenStart )
							elements.add( new LineToken( chars, tokenStart, i, LineToken.TEXT ) );
						if ( i + 3 < end && chars[ i + 1] == '!' && chars[i+2] == '-' && chars[i+3] == '-' ) {
							state = LineToken.COMMENT;
							tokenStart = i;
							i+= 3;
							inComment = true;
						} else {
							state = LineToken.TAG;
							tokenStart = i;
						}							
					}
				break;
				case LineToken.TAG:
					if ( c == '>' ) {
						if ( i > tokenStart )
							elements.add(new LineToken(chars, tokenStart, i + 1, LineToken.TAG));
						 tokenStart = i + 1;
						 state = LineToken.TEXT;
						 inComment = false;
					} 
					if ( Character.isWhitespace( c ) ) {
						if ( i > tokenStart ) {
							elements.add( new LineToken( chars, tokenStart, i, LineToken.TAG ) );
						}
						state = LineToken.ATTRIBUTE;
						tokenStart = i;
					}
				break;
				case LineToken.ATTRIBUTE_VALUE:
					if ( c == currentQuote ) {
						elements.add( new LineToken( chars, tokenStart, i + 1, LineToken.ATTRIBUTE_VALUE ));
						tokenStart = i + 1;
						state = LineToken.ATTRIBUTE;
					} else
					if ( c == '>' ) {
						if ( i > tokenStart ) {
							elements.add( new LineToken( chars, tokenStart, i + 1, LineToken.TAG_DELIMITER_END ) );
							tokenStart = i + 1;
							state = LineToken.TEXT;
						}						
					}
					break;
				case LineToken.ATTRIBUTE:
					if ( c == '=' ) {
						if ( i > tokenStart ) {
							elements.add( new LineToken( chars, tokenStart, i, LineToken.ATTRIBUTE ) );
							tokenStart = i;
							elements.add( new LineToken( chars, tokenStart, i + 1, LineToken.ATTRIBUTE_SEPARATOR ) );
						}
						tokenStart = i + 1;
					} else
					if ( c == '>' ) {
						if ( i > tokenStart ) {
							elements.add( new LineToken( chars, tokenStart, i + 1, LineToken.TAG ));
							tokenStart = i + 1;
						} else {
							elements.add( new LineToken( chars, tokenStart, i + 1, LineToken.TAG ));
							tokenStart = i + 1;
						}
						state= LineToken.TEXT;
						inComment = false;
					}
					else 
					if (c== '"' || c== '\'') {
						if ( i > tokenStart ) {
							elements.add( new LineToken( chars, tokenStart, i, LineToken.TAG ));
						}
						state = LineToken.ATTRIBUTE_VALUE;
						tokenStart = i;
						currentQuote = c;
					}
					
					break;
				case LineToken.COMMENT:
					if ( c == '-' && i + 2 < end && chars[ i + 1 ] == '-' && chars[ i + 2 ] == '>' ) {
						 elements.add(new LineToken(chars, tokenStart, i + 3, LineToken.COMMENT));
                        tokenStart = i + 3;
                        i += 2;
                        state = LineToken.TEXT;
                        inComment = false;
					}
					break;
			}
		}
		
		if ( end > tokenStart ) {
			elements.add( new LineToken( chars, tokenStart, end, state ));
		} else
		if ( elements.size() == 0 && len > 0 )
			elements.add( new LineToken( chars, start, end, state = LineToken.TEXT ));

		if ( stateByLine == null )
			stateByLine = new HashMap<Integer, Integer>();
		stateByLine.put( screenLine, state );
		
		this.quoteChar = currentQuote;
		return elements;
	}

	public static void main( String[] args ) {
		LineTokenizer lp = new LineTokenizer();
		String tst = "\n"
				+ "<mydoc id=\"hello\" greatjob=\"ok\">";
		System.out.println( lp.parse( tst.toCharArray(), 0, tst.length(), 0, 0 ) );
	}
	
}

