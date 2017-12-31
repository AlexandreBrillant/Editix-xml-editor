package com.japisoft.xmlpad.helper.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;

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
public class SchemaCache {

	private static HashMap htTagHelper = null;

	public static AbstractHelperHandler[] restoreHelperHandlers( Object source, String locationKey ) {
		if ( htTagHelper == null || 
				!SharedProperties.SCHEMA_CACHING )
			return null;
		ReferenceWrapper rw = ( ReferenceWrapper )htTagHelper.get( locationKey );
		if ( rw == null )
			return null;
		return rw.handlers;
	}

	public static void addNewReference( 
			AbstractHelperHandler[] handlers,
			String locationKey ) {

		if ( !SharedProperties.SCHEMA_CACHING )
			return;
		
		if ( locationKey != null ) {
			if ( htTagHelper == null )
				htTagHelper = new HashMap();
						
			htTagHelper.put( locationKey, 
					new ReferenceWrapper( handlers ) );
		}
	}

	public static void removeReference( Object source, AbstractHelperHandler[] handlers ) {
		
		if ( !SharedProperties.SCHEMA_CACHING )
			return;
				
		if ( htTagHelper != null && 
				handlers != null ) {
			Collection keys = htTagHelper.keySet();
			Iterator it = keys.iterator();
			while ( it.hasNext() ) {
				String loc = ( String )it.next();
				ReferenceWrapper rw = ( ReferenceWrapper )htTagHelper.get( loc );
				if ( compareArray( rw.handlers , handlers ) ) {
					if ( rw.removeSource( source ) ) {
						htTagHelper.remove( loc );
						break;
					}
				}
			}
		}
	}

	private static boolean compareArray( AbstractHelperHandler[] a1, AbstractHelperHandler[] a2 ) {
		for ( int i = 0; i < a1.length; i++ ) {
			if ( a1[ i ] != a2[ i ] )
				return false;
		}
		return true;
	}
	
	// ------------------------------------------------------------------
	
	static class ReferenceWrapper {
		public AbstractHelperHandler[] handlers = null;

		public ReferenceWrapper( AbstractHelperHandler[] handlers ) {
			this.handlers = handlers;
		}
		
		private ArrayList sources = null;
		
		public void addSource( Object source ) {
			if ( sources == null )
				sources = new ArrayList();
			sources.add( new Integer( source.hashCode() ) );
		}
		
		public boolean removeSource( Object source ) {
			if ( sources == null )
				return false;
			return sources.remove( new Integer( source.hashCode() ) );
		}
		
	}

}
 
