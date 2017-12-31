package com.japisoft.framework.xml.parser;

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
public class NameCollection {

	private Map<Integer,String> names = null;
	private Map<String,Integer> ids = null;	
	private int initialCapacity = 100;
	
	private static NameCollection THIS = null;

	private NameCollection() {
		THIS = this;
	}

	public static NameCollection getInstance() {
		if ( THIS == null )
			new NameCollection();
		return THIS;
	}
	
	private int id = 0;

	public synchronized int getId( String name ) {
		if ( ids == null )
			ids = new HashMap<String, Integer>( initialCapacity );
		Object res = ids.get( name );
		if ( res != null )
			return ( Integer )res;
		if ( names == null )
			names = new HashMap<Integer, String>( initialCapacity );
		id++;
		ids.put( name, id );
		names.put( id, name );
		return id;
	}

	public String getName( int id ) {
		if ( names == null )
			return null;
		return names.get( id );
	}
	
	public static void main( String[] args ) {
		int id = NameCollection.getInstance().getId( "hello" );
		System.out.println( id );
		id = NameCollection.getInstance().getId( "hello" );
		System.out.println( id );
		id = NameCollection.getInstance().getId( "hello2" );
		System.out.println( id );
		id = NameCollection.getInstance().getId( "hello2" );
		System.out.println( id );
		
	}
	
}
