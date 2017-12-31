package com.japisoft.editix.ui.xslt.map;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class VirtualDomNode {

	private Node source;

	private VirtualDomNode parent;
	
	public VirtualDomNode( Node source ) {
		this.source = source;
		source.setUserData( "vdm", this, null );		
	}

	public Node getSource() { return source; }

	private List<VirtualDomNode> children = null;

	public void addChild( VirtualDomNode node ) {
		if ( children == null ) {
			children = new ArrayList<VirtualDomNode>();
		}
		node.parent = this;
		children.add( node );
	}

	public void removeChild( VirtualDomNode node ) {
		// Remove virtually
		children.remove( node );
		// Remove physically
		Node sn = node.getSource();
		sn.getParentNode().removeChild( sn );
	}
	
	public int getChildCount() {
		if ( children == null )
			return 0;
		return children.size();
	}
	
	public VirtualDomNode getParent() { return parent; }

	/** 
	 * @param ns The namespace to avoid
	 * @return
	 */
	public int getChildCount( String ns ) {
		NodeList nl = source.getChildNodes();
		int cpt = 0;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				if ( !ns.equalsIgnoreCase( nl.item( i ).getNamespaceURI() ) )
					cpt++;
			}
		}
		return cpt;
	}

	/**
	 * @param ns The namespace to avoid
	 * @param index
	 * @return */
	public VirtualDomNode getChildAt( String ns, int index ) {
		NodeList nl = source.getChildNodes();
		int cpt = 0;
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				if ( !ns.equalsIgnoreCase( nl.item( i ).getNamespaceURI() ) ) {
					if ( cpt == index ) {
						return ( VirtualDomNode )nl.item( i ).getUserData( "vdm" );
					}
					cpt++;
				}
			}
		}
		return null;
	}

	public VirtualDomNode getChildAt( int index ) {
		return children.get( index );
	}
	
	public int getIndexOfChild( VirtualDomNode node ) {
		if ( node.children == null ) {
			return -1;
		}
		return node.children.indexOf( node );
	}
	
	@Override
	public String toString() {
		if ( source.getNodeType() == Node.ELEMENT_NODE ) {
			return source.getLocalName();
		}
		return source.getNodeValue();
	}

}
