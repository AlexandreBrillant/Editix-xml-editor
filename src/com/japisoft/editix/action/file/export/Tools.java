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

package com.japisoft.editix.action.file.export;

public class Tools {

	// Cause : Bug xerces for local name
	public static String getLocalNameForQName( String qname ) {
		int i = qname.indexOf( ":" );
		if ( i > -1 )
			return qname.substring( i + 1 );
		return qname;
	}

	public static String toClassName( String name ) {
		if ( name.length() > 1 )
			return Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
		else
			return name.toUpperCase();
	}	

	public static String toAddName( String name ) {
		return toAnyName( "add", name );
	}
		
	public static String toSetName( String name ) {
		return toAnyName( "set", name );
	}

	public static String toGetName( String name ) {
		return toAnyName( "get", name );
	}

	public static String toAnyName( String prefix, String name ) {
		//name = convertIfKeyWord( name );
		if ( name.length() > 1 )
			return prefix + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
		else
			return prefix + name.toUpperCase();
	}

	public static final String[] KEYWORDS = new String[] {
		"abstract",
		"assert",
		"boolean",
		"break",
		"byte",
		"case",
		"catch",
		"char",
		"class",
		"const",
		"continue",
		"default",
		"do",
		"double",
		"else",
		"enum",
		"extends",
		"false",
		"final",
		"finally",
		"float",
		"for",
		"goto",
		"if",
		"implements",
		"import",
		"instanceof",
		"int",
		"interface",
		"long",
		"native",
		"new",
		"null",
		"package",
		"private",
		"protected",
		"public",
		"return",
		"short",
		"static",
		"strictfp",
		"super",
		"switch",
		"synchronized",
		"this",
		"throw",
		"throws",
		"transient",
		"true",
		"try",
		"void",
		"volatile",
		"while"
	};

	public static String convertIfKeyWord( String value ) {
		for ( int i = 0; i < KEYWORDS.length; i++ ) {
			if ( value.equals( KEYWORDS[ i ] ) ) {
				return "_" + value;
			}
		}
		return value;
	}

	public static void warning( String message ) {
		System.out.println( "* Warning : " + message );
	}
	
	public static void error( String message ) {
		System.err.println( "** Error : " + message );
	}
	
	public static boolean DEBUG;
	static {
		try {
			DEBUG = "true".equals( System.getProperty( "debug" ) );
		} catch( Throwable th ) {
			// For java web start
		}
	}

	public static void debug( String message ) {
		if ( DEBUG )
			System.out.println( message );
	}
}


