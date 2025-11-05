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

package com.japisoft.p3.cr;

public class SC {

	public static void c( String s ) {
		StringBuffer sb = new StringBuffer();
		sb.append( "int[] s = new int[] {\n" );
		for ( int i = 0; i < s.length(); i++ ) {
			if ( i > 0 )
				sb.append( "," );
			sb.append( s.charAt( i ) ^ 20 );
			sb.append( "\n" );
		}
		sb.append( "};" );
		System.out.println( sb );
	}

	public static String uc( int[] s ) {
		char[] r = new char[ s.length ];
		for ( int i = 0; i < s.length; i++ ) 
			r[ i ] = (char)( s[ i ] ^ 20 );
		return new String( r );
	}
	
	public static void main(String[] args) {
	}

}

