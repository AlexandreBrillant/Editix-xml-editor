package com.japisoft.framework.xml.parser.dom;

import java.util.Vector;

import com.japisoft.framework.xml.parser.node.*;

import org.w3c.dom.*;

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
public class NodeListImpl implements NodeList {
	private FPNode n;

	/** @param n reference node */
	public NodeListImpl(FPNode n) {
		super();
		this.n = n;
	}

	private String filter;

	/** @param n reference node
	 * @param filter tag name to find */
	public NodeListImpl(FPNode n, String filter) {
		this(n);
		this.filter = filter;
		if  ( filter != null )
			prepareChildren();
	}

	private Vector children = null;

	private void prepareChildren() {
		if ( children == null )
			children = new Vector();
		prepareChildren( n );
	}

	private void prepareChildren( FPNode n ) {
		for ( int i = 0; i < n.childCount(); i++ ) {
			FPNode n2 = n.childAt( i );
			if ( n2.isTag() && filter.equals( n2.getNodeContent() ) )
				children.addElement( n2 );
		}
		for ( int i = 0; i < n.childCount(); i++ ) {
			FPNode n2 = n.childAt( i );
			if ( n2.isTag() )
				prepareChildren( n2 );		
		}
	}

	/** @return a node at index or <code>null</code> */
	public Node item(int index) {
		if (filter == null)
			return (Node) n.childAt(index);
		else
			return ( Node )children.elementAt( index );
	}

	/** @return the total number of node */
	public int getLength() {
		if (filter == null)
			return n.childCount();
		else {
			return children.size();
		}
	}

}

// NodeListImpl ends here
