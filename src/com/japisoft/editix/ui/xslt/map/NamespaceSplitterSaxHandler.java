package com.japisoft.editix.ui.xslt.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
public class NamespaceSplitterSaxHandler extends DefaultHandler {

	private VirtualDomNode root = null;
	private Stack<VirtualDomNode> stack = null;
	private NodeList refNodes = null;
	private int nodeCounter;

	private String namespace;
	private boolean ignoreNamespace;

	private List<VirtualDomNode> list = null;

	private boolean skipRoot = false;

	public NamespaceSplitterSaxHandler( String namespace, boolean ignoreNamespace, Element refNode ) {
		this.namespace = namespace;
		this.ignoreNamespace = ignoreNamespace;

		root = new VirtualDomNode( refNode );
		stack = new Stack<VirtualDomNode>();
		stack.push( root );

		// Get all the nodes
		// refNodes = refNode.getElementsByTagNameNS( namespace, "*" );
		
		refNodes = refNode.getElementsByTagName( "*" );		
		nodeCounter = 0;
		list = new ArrayList<VirtualDomNode>();
		list.add( root );
	}

	public List<VirtualDomNode> getAll() { return list; }
	
	public VirtualDomNode getRoot() { return root; }
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ( !skipRoot ) {
			skipRoot = true;
			return;
		}
		
		if ( ( !ignoreNamespace && uri.equalsIgnoreCase( namespace ) ) || 
				( ignoreNamespace && !uri.equalsIgnoreCase( namespace ) ) ) {
			VirtualDomNode parent = stack.peek();
			VirtualDomNode node = new VirtualDomNode( refNodes.item( nodeCounter ) );
			parent.addChild( node );
			stack.push( node );
			list.add( node );
		}

		nodeCounter++;
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ( ( !ignoreNamespace && uri.equalsIgnoreCase( namespace ) ) || 
				( ignoreNamespace && !uri.equalsIgnoreCase( namespace ) ) ) {
			stack.pop();
		}
	}

}
