// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath;
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
public class NodeSet extends FastVector {
	public NodeSet() {
		super();
	}

	/** Add this node */
	public NodeSet(Object node) {
		this();
		addNode(node);
	}

	/** Add a new node */
	public void addNode(Object node) {
		if (!contains(node))
			addElement(node);
	}

	/** For JDK1.1 compatibility */
	public boolean add(Object node) {
		super.addElement(node);
		return true;
	}

	/** Mix with another NodeSet and return the current one */
	public NodeSet union(NodeSet set) {
		for (int i = 0; i < set.size(); i++)
			addNode(set.get(i));
		return this;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer( "[" );
		for ( int i = 0; i < size(); i++ ) {
			if ( i != 0 )
				sb.append( "," );
			sb.append( get( i ) );
		}
		sb.append( "]" );
		return sb.toString();
	}

}

// NodeSet ends here
