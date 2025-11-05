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

package com.japisoft.xmlform;

public class Toolkit {

	public static boolean areRelativeURI( String uri1, String uri2 ) {
		if ( uri1 == null )
			return false;
		if ( uri2 == null )
			return false;
		int i1 = 
			uri1.lastIndexOf( "/" );
		if ( i1 == -1 )
			i1 = uri1.lastIndexOf( "\\" );
		int i2 = 
			uri2.lastIndexOf( "/" );
		if ( i2 == -1 )
			i2 = uri2.lastIndexOf( "\\" );
		if ( i1 == -1 && i2 == -1 )
			return true;
		if ( i1 == -1 )
			return false;
		if ( i2 == -1 )
			return false;
		String parent1 = 
			uri1.substring( 0, i1 );
		String parent2 = 
			uri2.substring( 0, i2 );
		return parent1.equalsIgnoreCase( parent2 );
	}

	public static String getRelativeURI( String uri ) {
		if ( uri == null )
			return null;
		int i = uri.lastIndexOf( "/" );
		if ( i == -1 )
			i = uri.lastIndexOf( "\\" );
		if ( i == -1 )
			return uri;
		return uri.substring( i + 1 );
	}

	public static String getAbsolutePath( String baseURI, String uri ) {

		if ( uri.indexOf( "/" ) > -1 )
			return uri;
		if ( uri.indexOf( "\\" ) > -1 )
			return uri;

		if ( baseURI != null ) {
		
			int i = baseURI.lastIndexOf( "/" );
			if ( i == -1 )
				i = baseURI.lastIndexOf( "\\" );
			if ( i == -1 )
				return uri;	// Can do nothing
			String parentPath = baseURI.substring( 0, i + 1 );
			return parentPath + uri;
		
		} else {
			return uri;
		}

	}

	public static String trimQuote( String str ) {
		if ( str == null )
			return str;
		if ( str.startsWith( "'" ) || str.startsWith( "\"" ) )
			str = str.substring( 1 );
		if ( str.endsWith( "'" ) || str.endsWith( "\"" ) )
			str = str.substring( 
					0, 
					( str.length() - 1 ) 
			);
		return str;
	}
	
}

