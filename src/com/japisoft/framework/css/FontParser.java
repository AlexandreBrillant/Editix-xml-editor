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

package com.japisoft.framework.css;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

public class FontParser {

	public static Map<String,Font> predefined = new HashMap<String,Font>();

	static {

		predefined.put( "serif", new Font( Font.SERIF, Font.PLAIN, 14 ) );
		predefined.put( "sans-serif", new Font( Font.SANS_SERIF, Font.PLAIN, 14 ) );
		predefined.put( "monospace", new Font( Font.MONOSPACED, Font.PLAIN, 14 ) );
		predefined.put( "cursive", predefined.get( "sans-serif" ) );
		predefined.put( "fantasy", predefined.get( "sans-serif" ) );

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = ge.getAllFonts();
		for ( Font f : fonts ) {
			
			f = f.deriveFont( Font.PLAIN, 14 );
			
			predefined.put(
					f.getFontName().toLowerCase(), f );
			
		}

	}

	private FontParser() {}

	private final static FontParser INSTANCE = new FontParser();

	public static FontParser getInstance() { return INSTANCE; }
	
	public Font parseFont( String val ) {
		String[] fonts = val.split( "," );
		for ( String f : fonts ) {
			f = f.trim().toLowerCase();
			if ( f.startsWith( "\"" ) ) 
				f = f.substring( 1, f.length() -1 );
			if ( predefined.containsKey( f ) )
				return predefined.get( f );
		}
		return null;
	}

	public Font getDefaultFont() { return predefined.get( "serif" ); }
	
	public static void main( String[] args ) {
		System.out.println( FontParser.getInstance().parseFont( "\"Lucida Console\", Courier, monospace" ) );
	}
}
