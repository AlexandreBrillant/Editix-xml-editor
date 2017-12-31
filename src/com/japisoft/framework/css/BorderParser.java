package com.japisoft.framework.css;

import java.util.ArrayList;
import java.util.List;

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
public class BorderParser {

	private static BorderParser INSTANCE = null;
	
	private static List<String> styles = null;
	
	static {
		styles = new ArrayList<String>();
		styles.add( "none" );
		styles.add( "hidden" );
		styles.add( "dotted" );
		styles.add( "dashed" );
		styles.add( "solid" );
		styles.add( "double" );
		styles.add( "groove" );		
		styles.add( "ridge" );
		styles.add( "inset" );
		styles.add( "outset" );
		styles.add( "inherit" );
	}
	
	private BorderParser() {
		INSTANCE = this;
	}
	
	public static BorderParser getInstance() {
		if ( INSTANCE == null )
			new BorderParser();
		return INSTANCE;
	}
	
	public CSSBorder parse( String content ) {
		CSSBorder border = new CSSBorder();
		String[] parts = content.split( " " );
		for ( String part : parts ) {
			part = part.toLowerCase();
			if ( styles.contains( part ) )
				border.setStyle( part );
			else {
				if ( Character.isDigit(
					part.charAt( 0 ) ) ) {
					border.setWidth( new CSSDim( part ) );
				} else
					border.setColor( 
						ColorParser.getInstance().parseColor( part ) 
					);
			}
				
		}
		return border;
	}
	
}
