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

package com.japisoft.editix.editor.json.kit;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.text.Segment;

import com.japisoft.editix.editor.js.JSSyntaxDescription;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.preferences.Preferences;

public class LineParser {

	private Color valueOfProperty = new Color( 150, 0, 0 );
	private Color defaultColor = Color.BLACK;
	private Color propertyColor = Color.ORANGE.darker();
	private Color idColor = propertyColor;
	private Color commentColor = null;
	private Color stringColor = null;
	private JSSyntaxDescription syntax;
	
	LineParser( String preferenceGroupe, JSSyntaxDescription syntax ) {
		this.syntax = syntax;
		initColorTable( preferenceGroupe );
	}

	private void initColorTable( String preferenceGroupe ) {

		commentColor = Preferences.getPreference( preferenceGroupe, "commentColor", Color.DARK_GRAY );
		defaultColor = Preferences.getPreference( preferenceGroupe, "defaultColor", defaultColor );
		
		if ( EditixApplicationModel.DARK_MODE ) {
			defaultColor = Preferences.getPreference( "editor", "dark-text", Color.WHITE );
			commentColor = Preferences.getPreference( "editor", "dark-comment", Color.WHITE );
		}		

		Color c = new Color( 0, 150, 0 );
		c = Preferences.getPreference( preferenceGroupe, "delimitersColor", c );

		for ( int i = 0; i < syntax.getDelimiters().length; i++ )
			mapOfColor.put( Character.toString( syntax.getDelimiters()[ i ] ), c );

		c = new Color( 0, 0, 150 );
		c = Preferences.getPreference( preferenceGroupe, "keywordsColor", c );
		for ( int i = 0; i < syntax.getKeywords().length; i++ ) {
			mapOfColor.put( syntax.getKeywords()[ i ], c );
		}

		mapOfColor.put( "/*", commentColor );
		mapOfColor.put( "//", commentColor );
		
		propertyColor = Preferences.getPreference( preferenceGroupe, "propertyColor", propertyColor );
		stringColor = Preferences.getPreference( preferenceGroupe, "stringColor", Color.RED );

		if ( EditixApplicationModel.DARK_MODE ) {
			stringColor = Preferences.getPreference( "editor", "dark-litteral", Color.WHITE );
		}

	}
	
	private int tokenCount = -1;
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
	
	boolean propertyFlip = false;
	
	public Token[] getTokenForLine(Segment str, int line) {
		tokenCount = 0;

		Token previousLastToken = ( Token )mapOfLastTokens.get(
				new Integer( line - 1 ) 
		);

		resetBuffer();
		boolean commentMode = false;
		char stringMode = 0;
		
		// Restore comment color
		if ( previousLastToken != null ) {
			if ( previousLastToken.color == commentColor ) {
				commentMode = true;	
			} else
			if ( previousLastToken.color == stringColor ) {
				stringMode = previousLastToken.text.charAt( 0 );
			}
		}
		
		char oldc = 0;
		boolean lineCommentMode = false;
		char c = 0;

		for ( int i = 0; i < str.length(); i++ ) {

			oldc = c;
			c = str.charAt( i );

			if ( c == ':' ) {
				/*
				// Change the previous stringColor as a propertyColor
				for ( int ii = tokenCount - 1; ii >= 0; ii-- ) {
					if ( maxTokens[ ii ].color == stringColor ) {
						maxTokens[ ii ].color = propertyColor;
						break;
					}
				}*/
				if ( lastStringToken != null ) {
					lastStringToken.color = propertyColor;
					lastStringToken = null;
				}
			}
			
			if ( stringMode == 0 ) {
				if ( syntax.isStringDelimiter( c ) ) {
					addToken( sb.toString() );
					resetBuffer();
					sb.append( c );
					stringMode = c;
					continue;
				}
			} else
			if ( stringMode != 0 ) {
				if ( syntax.isStringDelimiter( c ) && ( c == stringMode ) ) {
					sb.append( c );
					addToken( sb.toString(),stringColor );
					resetBuffer();					
					stringMode = 0;
				} else {
					sb.append( c );
				}
				continue;
			}

			if (  ( c == ' ' ) ||
						( c == '\t' ) ||
							syntax.isDelimiter( c ) ) {
				if ( sb.length() > 0 ) {
					addToken( sb.toString() );
				}
				addToken( "" + c );
				resetBuffer();
				continue;
			}

			sb.append( c );	
		}

		Color col = null;
		if ( commentMode )	// Force a gray
			col = commentColor;
		if ( stringMode != 0 )
			col = stringColor;

		addToken( sb.toString(), col );

		if ( ( commentMode && !lineCommentMode ) || stringMode != 0 ) {
			// For next usage
			mapOfLastTokens.put( 
					line, 
					new Token( "" + stringMode, col ) );			
		} else {
			// Remove the last comment
			if ( mapOfLastTokens.containsKey( line ) ) {
				mapOfLastTokens.remove( line );
			}
		}
		return maxTokens;
	}

	Token addToken( String token ) {
		return addToken( token, null );
	}
	
	private Token lastStringToken = null;
	
	Token addToken( String token, Color forceColor ) {
		Color c = forceColor;
		if ( c == null )
			c = ( Color )mapOfColor.get( token );
		
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
		}
			
		t = new Token( token, c );
		
		if ( c == stringColor ) {
			lastStringToken = t;
		}
		
		maxTokens[ tokenCount++ ] = t;
		return t;
	}
	
	Token lastToken() {
		if ( tokenCount > -1 ) {
			return maxTokens[ tokenCount - 1 ];
		}
		return null;
	}

	void dump() {
		System.out.println();
		for ( int i = 0; i < tokenCount; i++ ) {
			System.out.print( "[" + maxTokens[ i ] + "]" );
		}
	}

	public static void main( String[] args ) {
		String s = "//a";
		LineParser sl = new LineParser( "JSEditor", new JSSyntaxDescription() );
		String[] tmp = s.split( "\n" );
		for ( int i = 0; i < tmp.length; i++ ) {
			sl.getTokenForLine( new Segment( tmp[ i ].toCharArray(), 0, tmp[ i ].length() ), i );
			sl.dump();
		}
	}
	
}

