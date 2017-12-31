package com.japisoft.framework.css;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

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
