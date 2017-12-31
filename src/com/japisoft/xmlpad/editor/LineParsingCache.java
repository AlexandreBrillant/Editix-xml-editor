package com.japisoft.xmlpad.editor;

import java.util.Vector;

import javax.swing.text.Segment;

// Cache for big lines */
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
final class LineParsingCache {

	private static final int LINE_BOUND = 100;
	private static final boolean ENABLED = false;
	static final int CACHE_SIZE = 5;
	static int[] storedLineLocation = new int[ CACHE_SIZE ];
	static int[] storedLineSize = new int[ CACHE_SIZE ];
	static Vector[] storedParsingResult = new Vector[ CACHE_SIZE ];
	static int cacheLocation = -1;
	static int cacheSize = 0;
	
	static Vector getParsedLine( Segment line, int lineLocation ) {
		if ( line.count > LINE_BOUND && ENABLED ) {
			for ( int i = 0; i < cacheSize; i++ ) {
				if ( storedLineLocation[ i ] == lineLocation && 
						storedLineSize[ i ] == line.count ) {
					return storedParsingResult[ i ];
				}
			}
		}
		return null;
	}
	
	static void updateCache( Segment line, int location, Vector v ) {
		if ( line.count > LINE_BOUND && ENABLED ) {
			
			// In cache already ?
			for ( int i = 0; i < cacheSize; i++ ) {
				if ( storedLineLocation[ i ] == location && storedLineSize[ i ] == line.count ) {
					return;
				}
			}

			System.out.println( "UPDATED CACHE");
			
			cacheLocation = ( cacheLocation + 1 ) % CACHE_SIZE;
			cacheSize = Math.max( cacheSize, cacheLocation + 1 );
			storedLineLocation[ cacheLocation ] = location;
			storedLineSize[ cacheLocation ] = line.count;
			storedParsingResult[ cacheLocation ] = ( Vector )v.clone();
		}
	}

}
