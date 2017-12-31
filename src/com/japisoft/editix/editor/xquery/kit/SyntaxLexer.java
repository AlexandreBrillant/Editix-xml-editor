package com.japisoft.editix.editor.xquery.kit;

import java.awt.Color;
import java.util.HashMap;

import com.japisoft.editix.editor.xquery.helper.Keywords;
import com.japisoft.framework.preferences.Preferences;

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
public class SyntaxLexer {

	private static final Token EMPTY_TOKEN = new Token( "", null );
	Color defaultColor = Color.BLACK;
	Color string1Color = Color.RED;
	Color string2Color = Color.ORANGE.darker();
	Color variableColor = Color.PINK.darker();
	Color commentColor = Color.DARK_GRAY;
	Color tagColor = new Color( 0, 0, 178 );
	Color axisColor = Color.MAGENTA.darker();
	Color attributeColor = new Color( 100, 50, 200 );
	
	SyntaxLexer() {
		Color c = new Color( 0, 0, 200 );
		defaultColor = Preferences.getPreference( "xqueryEditor", "defaultColor", defaultColor );
		string1Color = Preferences.getPreference( "xqueryEditor", "string1Color", string1Color );		
		string2Color = Preferences.getPreference( "xqueryEditor", "string2Color", string2Color );		
		variableColor = Preferences.getPreference( "xqueryEditor", "variableColor", variableColor );		
		commentColor = Preferences.getPreference( "xqueryEditor", "commentColor", commentColor );		
		tagColor = Preferences.getPreference( "xqueryEditor", "tagColor", tagColor );
		axisColor = Preferences.getPreference( "xqueryEditor", "axisColor", axisColor );
		attributeColor = Preferences.getPreference( "xqueryEditor", "attributeColor", attributeColor );		
		c = Preferences.getPreference( "xqueryEditor", "keywords", c );

		for ( int i = 0; i < Keywords.main.length; i++ ) {
			mapOfColor.put( Keywords.main[ i ], c );
		}

		for ( int i = 0; i < Keywords.axes.length; i++ ) {
			mapOfColor.put( Keywords.axes[ i ], axisColor );
		}

		c = new Color( 0, 150, 150 );

		for ( int i = 0; i < Keywords.functions.length; i++ ) {
			mapOfColor.put( Keywords.functions[ i ], c );
		}
				
		mapOfColor.put( "(:", commentColor );

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
		boolean variableMode = false;
		boolean stringMode = false;
		char stringEsc = 0;
		boolean tagMode = false;
		
		if ( previousLastToken != null ) { 
			if ( previousLastToken.color == commentColor )
				commentMode = true;
			else 
			if ( previousLastToken.color == string1Color || 
					previousLastToken.color == string2Color ) {
				stringMode = true;
				stringEsc = previousLastToken.color == string1Color ? '"' : '\''; 
			} else
			if ( previousLastToken.color == tagColor ) {
				tagMode = true;
			}
		}

		for ( int i = 0; i < str.length(); i++ ) {

			char c = str.charAt( i );

			if ( !commentMode && i > 0 ) {
				// Comment opening flag
				if ( c == ':' && 
						str.charAt( i - 1 ) == '(' ) {

					sb.deleteCharAt( sb.length() - 1 );
					addToken( sb.toString() );
					resetBuffer();
					addToken( "(:" );
					commentMode = true;
					continue;
					
				}
			}

			if ( commentMode ) {

				if ( c == ')' && i > 0 &&
					str.charAt( i - 1 ) == ':' ) {
					
					// Comment closing flag
					sb.append( c );
					addToken( sb.toString(), commentColor );
					// For a non commment closing at end
					mustBeLastToken = EMPTY_TOKEN; 
					resetBuffer();
					commentMode = false;

				} else
					sb.append( c );
					
				continue;
			}
			
			if ( !tagMode ) {
				if ( c == '<' ) {
					
					addToken( sb.toString() );
					resetBuffer();
					addToken( "<" , tagColor );
					tagMode = true;
					continue;
					
				}
			} else {
				if ( c == '>' ) {
					
					sb.append( ">" );
					addToken( sb.toString(), tagColor );
					mustBeLastToken = EMPTY_TOKEN;
					resetBuffer();
					tagMode = false;
					
				} else
					sb.append( c );
				continue;
			}

			if ( !stringMode ) {

				if ( c == '"' || c == '\'' ) {
					
					stringEsc = c;
					addToken( sb.toString() );
					resetBuffer();
					addToken( "\"" , c == '"' ? string1Color : string2Color );
					stringMode = true;
					continue;
					
				}

			} else {

				if ( c == stringEsc ) {
					
					sb.append( c );
					addToken( sb.toString(), c == '"' ? string1Color : string2Color );
					mustBeLastToken = EMPTY_TOKEN;
					resetBuffer();
					stringMode = false;	
					
				} else
					sb.append( c );

				continue;

			}

			if ( !variableMode ) {

				if ( c == '$' ) {
					
					addToken( sb.toString() );
					resetBuffer();
					addToken( "$", variableColor );
					resetBuffer();
					variableMode = true;
					continue;
					
				}

			} else {
				if ( c == ' ' || 
						c == '\t' || 
							c == '\n' || 
								c == '\r' || 
									c == '=' || 
										c == ')' || 
											c == '}' ||
												c == ',' ||
													c == '/' ||
														c == ']' ) {
					addToken( sb.toString(), variableColor );
					resetBuffer();
					variableMode = false;
				}
			}
			
			if ( !checkForWord ) {

				if ( c != ' ' && 
						c != '\t' && 
							c!= '\n' && 
								c != '\r' && 
									c!= '(' &&
										c != '{' && 
											c != ',' &&
												c != '=' &&
													c != ':' &&
														c != '[' &&
															c != '/' ) {

					addToken( sb.toString() );
					resetBuffer();
					sb.append( c );
					checkForWord = true;

				} else {					
					sb.append( c );
				}

			} else {

				if ( c == ' ' || 
						c == '\t' ||
							c == '\n' ||
								c == '\t' ||
									c == '(' ||
										c == ':' ||
											c == '=' ||
												c == '/' ||
													c == '[' ||
														c == ']' ||
															c == ')' ||
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
		else
		if ( stringMode ) {
			c = stringEsc == '"' ? string1Color : string2Color;
		} else
		if ( variableMode )
			c = variableColor;

		couldbeLastToken = addToken( sb.toString(), c );

		if ( couldbeLastToken != null )
			mustBeLastToken = couldbeLastToken;

		
		if ( variableMode )
			mustBeLastToken = null;
		
		if ( currentLastToken != null && 
				currentLastToken.color == commentColor && 
					!commentMode ) {
			mustBeLastToken = EMPTY_TOKEN;
		}

		if ( currentLastToken != null && 
				( currentLastToken.color == string1Color || 
						currentLastToken.color == string2Color ) && 
					!stringMode ) {
			mustBeLastToken = EMPTY_TOKEN;
		}

		if ( currentLastToken != null && 
				currentLastToken.color == tagColor 
						&& 
					!tagMode ) {
			mustBeLastToken = EMPTY_TOKEN;
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
		if ( c == null ) {
			c = ( Color )mapOfColor.get( token.toLowerCase() );
			if ( c == null ) {
				if ( token.startsWith( "@" ) ) {
					c = attributeColor;
				}
			}
		}
		
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
		SyntaxLexer sl = new SyntaxLexer();
		sl.getTokenForLine( s, 0 );
		sl.dump();
	}
	
}
