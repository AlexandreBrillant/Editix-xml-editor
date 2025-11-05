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

package com.japisoft.framework.xml.parser.document;

import java.util.Stack;

import com.japisoft.framework.collection.FastVector;

/**
 * Context for namespace. It stores available prefix with tied namespace uri
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class NamespaceContext extends FastVector {
	private Stack defaultNamespaceSt;

	/**
	 * @return the default namespace from the attribute xmlns */
	public String currentDefaultNamespace() {
		if ( defaultNamespaceSt == null )
			return null;
		if ( defaultNamespaceSt.isEmpty() )
			return null;
		return (String)defaultNamespaceSt.peek();
	}

	/**
	 * Reset the default namespace
	 * @param defaultNamespace  */
	public void pushDefaultNamespace(String defaultNamespace) {
		if ( defaultNamespaceSt == null )
			defaultNamespaceSt = new Stack();
		defaultNamespaceSt.push( defaultNamespace );
	}

	/** Remove the last default namespace */
	public void popDefaultNamespace() {
		if ( defaultNamespaceSt != null )
			defaultNamespaceSt.pop();
	}

	/** Add a current prefix for this uri */
	public void addPrefixScope( String prefix, String uri ) {
		add( new PrefixURI( prefix, uri ) );
	}

	/** Remove a prefix that should not be accessible */
	public void removePrefixScope( String prefix ) {
		remove( prefix );
	}

	/** @return true if this prefix is available */
	public boolean isPrefixAvaiable( String prefix ) {
		return contains( prefix );
	}
	
	/** @return the URI for this prefix */
	public String getPrefixURI( String prefix ) {
		for ( int i = 0; i < size(); i++ ) {
			Object o;
			if ( ( o = get( i ) ).equals( prefix ) ) {
				return ((PrefixURI)o).uri;
			}
		}	
		return null;
	}

	// ----------------------------------------------------

	class PrefixURI {
		public String prefix;
		public String uri;

		PrefixURI( String prefix, String uri ) {
			this.prefix = prefix;
			this.uri = uri;
		}

		public boolean equals( Object o ) {
			if ( o instanceof String ) {
				return prefix.equals( o );
			} else
					return super.equals( o );
		}
	}

}

