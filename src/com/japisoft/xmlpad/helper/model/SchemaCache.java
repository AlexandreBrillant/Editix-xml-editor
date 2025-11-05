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

package com.japisoft.xmlpad.helper.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;

/**
 * This is a cache for a schema URI. It avoids duplicate
 * memory references for a same schema.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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
 
