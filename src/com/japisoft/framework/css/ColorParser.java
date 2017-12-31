package com.japisoft.framework.css;

import java.awt.Color;
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
public class ColorParser {

	public static Map<String,Color> predefined = new HashMap<String,Color>();
	
	static {

		Object[] table = {
				"aqua", "00FFFF",
				"green", "008000",
				"orange", "FFA500",
				"white", "FFFFFF",
				"black", "000000",
				"lime", "00FF00",
				"purple", "800080",	
				"yellow", "FFFF00",
				"blue", "0000FF",
				"maroon", "800000",
				"red", "FF0000",
				"fuschia", "FF00FF",
				"navy", "000080",
				"silver", "C0C0C0",
				"gray", "808080",
				"olive", "808000",
				"teal", "008080"	
		};

		for ( int i = 0; i < table.length; i += 2 ) {
			predefined.put(
				( String )table[ i ],
				new Color( 
					Integer.parseInt( 
						( String )table[ i + 1 ], 16 ) 
				) 
			);
		}

	}
	
	private ColorParser() {
	}
	
	private final static ColorParser INSTANCE = new ColorParser();
	
	public static ColorParser getInstance() { return INSTANCE; }

	public Color parseColor( String val ) {
		if ( val.startsWith( "#" ) ) {
			String rgb = val.substring( 1 );
			if ( rgb.length() == 3 ) {
				String r = Character.toString( rgb.charAt( 0 ) );
				String g = Character.toString( rgb.charAt( 1 ) );
				String b = Character.toString( rgb.charAt( 2 ) );
				rgb = r + r + g + g + b + b;
			}
			return new Color( Integer.parseInt( rgb, 16 ) );	
		} else {
			float r,g,b;
			float a = 1.0f;

			String values = null;
			
			if ( val.startsWith( "rgb" ) ) {
				int start = val.indexOf( "(" );
				int end = val.indexOf( ")" );
				String token = val.substring( start + 1, end );
				String[] parts = token.split( "," );

				r = ( float )( Float.parseFloat( parts[ 0 ] ) / 255.0 );
				g = ( float )( Float.parseFloat( parts[ 1 ] ) / 255.0 );
				b = ( float )( Float.parseFloat( parts[ 2 ] ) / 255.0 );
				
				if ( parts.length == 4 ) {
					a = Float.parseFloat( parts[ 3 ] );
				}
				
				return new Color( r, g, b, a );
			}
		}
		return predefined.get( val.toLowerCase() );
	}
	
	
}
