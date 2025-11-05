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

package com.japisoft.xmlpad.editor;

import java.util.Vector;

import javax.swing.text.Segment;

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

