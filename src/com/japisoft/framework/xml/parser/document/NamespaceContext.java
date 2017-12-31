package com.japisoft.framework.xml.parser.document;

import java.util.Stack;

import com.japisoft.framework.collection.FastVector;

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
