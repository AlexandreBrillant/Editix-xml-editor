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

package com.japisoft.editix.editor.css.kit;

import java.awt.Color;
import java.util.HashMap;

import com.japisoft.editix.editor.css.helper.Keywords;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.preferences.Preferences;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class SyntaxLexer {

	Color valueOfProperty = new Color( 150, 0, 0 );
	Color defaultColor = Color.BLACK;
	Color classColor = Color.ORANGE.darker();
	Color idColor = classColor;
	Color commentColor = Color.DARK_GRAY;
	
	SyntaxLexer( String preferenceGroup ) {

		commentColor = Preferences.getPreference( preferenceGroup, "commentColor", commentColor );		
		defaultColor = Preferences.getPreference( preferenceGroup, "defaultColor", defaultColor );

		Color kw = new Color( 0, 0, 150 );
		
		if ( EditixApplicationModel.DARK_MODE ) {
			defaultColor = Preferences.getPreference( "editor", "dark-text", Color.WHITE );
			commentColor = Preferences.getPreference( "editor", "dark-comment", Color.WHITE );
			kw = Preferences.getPreference( "editor", "dark-tag", Color.WHITE );
		}		
						
		for ( int i = 0; i < Keywords.properties.length; i++ ) {
			mapOfColor.put( Keywords.properties[ i ], kw );
		}
		
		Color c = new Color( 0, 150, 0 );
		c = Preferences.getPreference( preferenceGroup, "keywordsColor", c );		
		
		mapOfColor.put( "{", c );
		mapOfColor.put( "}", c );
		mapOfColor.put( ":", c );
		mapOfColor.put( ";", c );		
		mapOfColor.put( "/*", commentColor );
		mapOfColor.put( "*/", commentColor );

		valueOfProperty = Preferences.getPreference( preferenceGroup, "valueColor", valueOfProperty );
		
		if ( EditixApplicationModel.DARK_MODE ) {
			valueOfProperty = Preferences.getPreference( "editor", "dark-litteral", Color.WHITE );
		}
		
		classColor = Preferences.getPreference( preferenceGroup, "classColor", classColor );
		idColor = Preferences.getPreference( preferenceGroup, "idColor", idColor );
	}

	private int tokenCount;
	private Token[] maxTokens = new Token[ 100 ];

	public int getTokenCount() {
		return tokenCount;
	}

	private HashMap mapOfLastTokens = new HashMap();
	private HashMap mapOfColor = new HashMap();
	private StringBuffer sb = new StringBuffer();

	private void resetBuffer() {
		sb.delete( 0, sb.length() + 1 );
	}
	
	public Token[] getTokenForLine(String str, int line) {
		tokenCount = 0;
		
		Token previousLastToken = ( Token )mapOfLastTokens.get(
				new Integer( line - 1 ) 
		);
		Token currentLastToken = ( Token )mapOfLastTokens.get(
				new Integer( line ) 
		);

		resetBuffer();
		
		Token couldbeLastToken = null;
		Token mustBeLastToken = null;
		boolean checkForWord = false;
		boolean commentMode = false;
		boolean cssValueMode = false;
		
		if ( previousLastToken != null && 
				previousLastToken.color == Color.DARK_GRAY )
			commentMode = true;

		for ( int i = 0; i < str.length(); i++ ) {

			char c = str.charAt( i );

			if ( !commentMode && i > 0 ) {
				// Comment opening flag
				if ( c == '*' && 
						str.charAt( i - 1 ) == '/' ) {

					sb.deleteCharAt( sb.length() - 1 );
					addToken( sb.toString() );
					resetBuffer();
					addToken( "/*" );
					commentMode = true;
					continue;
					
				}
			}

			if ( commentMode ) {

				if ( c == '/' && i > 0 &&
					str.charAt( i - 1 ) == '*' ) {
					// Comment closing flag
					sb.append( c );
					addToken( sb.toString(), commentColor );
					// For a non commment closing at end
					mustBeLastToken = new Token( "", null ); 
					resetBuffer();
					commentMode = false;
				} else
					sb.append( c );
					
				continue;
			}
			
			
			if ( !cssValueMode ) {
			
				if ( c == ':' ) {
					cssValueMode = true;
				}

			} else {
				
				if ( c == '{' ) {
					
					cssValueMode = false;
					
				} else {
				
					if ( c == ';' ) {
						cssValueMode = false;
						addToken( sb.toString(), valueOfProperty );
						resetBuffer();
						addToken( ";" );
						cssValueMode = false;				
					} else
						sb.append( c );
	
					continue;
					
				}
			}
			
			if ( !checkForWord ) {

				if ( c != ' ' && 
						c != '\t' && 
							c!= '\n' && 
								c != '\r' && 
									c!= ';' && 
										c != '{' && 
											c != ':' && 
												c != '}' && 
													c != '/' ) {

					addToken( sb.toString() );
					resetBuffer();

					sb.append( c );

					checkForWord = true;

				} else {
					
					if ( c == '{' || c == '}' || c == ':' || c == ';' ) {
						addToken( sb.toString() );
						resetBuffer();
						addToken( "" + c );
					}
					else
						sb.append( c );
				}

			} else {

				if ( c == ' ' || 
						c == '\t' ||
							c == '\n' ||
								c == '\t' ||
									c == '{' ||
										c == ':' ||
											c == ';' ||
												c == '}' || 
													c == ',' ) {

					couldbeLastToken = addToken( sb.toString() );
					resetBuffer();
					sb.append( c );
					checkForWord = false;

				} else
					sb.append( c );

			}

			if ( couldbeLastToken != null )
				mustBeLastToken = couldbeLastToken;
		}

		Color c = null;
		if ( commentMode )	// Force a gray
			c = commentColor;

		couldbeLastToken = addToken( sb.toString(), c );

		if ( couldbeLastToken != null )
			mustBeLastToken = couldbeLastToken;

		if ( currentLastToken != null && 
				currentLastToken.color == commentColor && 
					!commentMode ) {
			mustBeLastToken = new Token( "", null );
			/*mustBeLastToken.mustRepaint = true;
			System.out.println( "FORCE REPAINT !" + line ); */
		}

		if ( mustBeLastToken != null )
			mapOfLastTokens.put( 
					new Integer( line ), 
					mustBeLastToken );

		return maxTokens;
	}

	Token addToken( String token ) {
		return addToken( token, null );
	}
	
	Token addToken( String token, Color forceColor ) {
		Color c = forceColor;
		if ( c == null )
			c = ( Color )mapOfColor.get( token.toLowerCase() );
		
		if ( tokenCount == maxTokens.length ) {
			Token[] tmp = new Token[ maxTokens.length * 2 ];
			System.arraycopy(
					maxTokens,0,tmp,0,maxTokens.length
			);
			maxTokens = tmp;
		}

		Token t = null;

		// Not a keyword
		if ( c == null ) {
			
			c = defaultColor;

			// Class
			if ( token.length() > 1 ) {
				if ( token.startsWith( "." ) )
					c = classColor;
				else
				if ( token.startsWith( "#" ) )
					c = idColor;
			}
				
			
			t = new Token( token, c );
			maxTokens[ tokenCount++ ] = t;
			return null;
		}
		else {
			t = new Token( token, c );
			maxTokens[ tokenCount++ ] = t;
		}
		return t;
	}

	void dump() {
		System.out.println();
		for ( int i = 0; i < tokenCount; i++ ) {
			System.out.print( "[" + maxTokens[ i ] + "]" );
		}
	}

	public static void main( String[] args ) {
		String s = "a color : flsfksldfkjsdlkj;dddd";
		SyntaxLexer sl = new SyntaxLexer( "preferenceGroup" );
		sl.getTokenForLine( s, 0 );
		sl.dump();
	}
	
}
