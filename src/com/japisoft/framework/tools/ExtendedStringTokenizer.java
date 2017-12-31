package com.japisoft.framework.tools;

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
public class ExtendedStringTokenizer {

	private char[] line = null;
	private char colSep = 0;
	private char textSep = 0;
	private int l = 0;
	private String firstToken = null;
	
	/**
	 * @param line Line to scan
	 * @param colSep The separator of each column
	 * @param textSep The separator for each text like a quote */
	public ExtendedStringTokenizer( String line, char colSep, char textSep ) {
		this.line = line.toCharArray();
		this.colSep = colSep;
		this.textSep = textSep;
		firstToken = nextScan();
	}

	public boolean hasMoreTokens() {
		return ( l > -1 );
	}

	public String nextToken()  {
		if ( firstToken != null ) {
			String tmp = firstToken;
			firstToken = null;
			return tmp;
		}
		return nextScan();
	}

	private String nextScan() {
		if ( l == -1 )
			return null;
		if ( l >= line.length ) {
			l = -1;
			return "";
		} else {
			boolean startText = false;
			StringBuffer sb = null;
			while ( l < line.length ) {
				if ( line[ l ] == textSep ) {
					if ( startText ) {
						// We found the end
						//l++;
						//return sb.toString();
						
						startText = false;
						
					} else
						startText = true;
				} else
					if ( startText ) {
						if ( sb == null )
							sb = new StringBuffer();
						sb.append( line[ l ] );
					} else
						if ( line[ l ] == colSep ) {
							if ( sb != null ) {
								if ( l < line.length - 1 )
									l++;
								return sb.toString();
							} else {
								l++;
								if ( l == line.length )
									l = -1;
								return "";
							}
						} else {
							// This is character we must maintain until the next column separator
							if ( sb == null )
								sb = new StringBuffer();
							sb.append( line[ l ] );
						}
				l++;
			}
			l = -1;
			if ( sb != null ) {
				return sb.toString();
			}
			return "";
		}
	}

	public static void main( String[] test ) {

		ExtendedStringTokenizer est = new ExtendedStringTokenizer( ";'a';\"b\";\"c;cc\";;;d;", ';', '\"' );
		while ( est.hasMoreTokens() ) {
			System.out.println( "[" + est.nextToken() + "]" );
		}
		
	}

}
