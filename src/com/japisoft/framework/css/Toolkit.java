package com.japisoft.framework.css;

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
public class Toolkit {

	private static Map<String,Boolean> i = null;
	
	static {
		i = new HashMap<String,Boolean>();
		i.put( "color", Boolean.TRUE );
		i.put( "cursor", Boolean.TRUE );
		i.put( "font-family", Boolean.TRUE );
		i.put( "font-size", Boolean.TRUE );
		i.put( "font-style", Boolean.TRUE );
		i.put( "font-variant", Boolean.TRUE );
		i.put( "font-weight", Boolean.TRUE );
		i.put( "font", Boolean.TRUE );
		i.put( "text-align", Boolean.TRUE );
		i.put( "text-indent", Boolean.TRUE );
		i.put( "text-transform", Boolean.TRUE );		
		i.put( "visibility", Boolean.TRUE );				
	}
	
	public static boolean isInheritedProperty( String name ) {
		name = name.toLowerCase();
		if ( i.containsKey( name ) )
			return true;
		return false;
	}
	
	public static String[] getInheritedProperties() {
		return i.keySet().toArray( new String[ i.size() ] );
	}
	
}
