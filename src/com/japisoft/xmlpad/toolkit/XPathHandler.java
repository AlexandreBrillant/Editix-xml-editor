// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.xmlpad.toolkit;

import java.util.ArrayList;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xpath.NodeSet;
import com.japisoft.xpath.XPath;
import com.japisoft.xpath.XPathException;
import com.japisoft.xpath.kit.FastParserKit;

/**
 * Handler for XPath expression like highlighting XPath expression nodes
 * inside the XMLContainer or select the first one or the next one...
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class XPathHandler {

	private ArrayList l;
	private int index = -1;
	private XMLContainer container;
	
	public XPathHandler( XMLContainer container, String xpathExpression, boolean relative ) throws XPathException {
		this.container = container;

		FPNode root = null;
		if ( !relative )
			root = container.getRootNode();
		else {
			root = container.getCurrentNode();
			if ( root == null )
				root = container.getRootNode();
		}

		if ( root == null )
			throw new XPathException( "No document", 0 );

		XPath xpath = new XPath( 
				new FastParserKit() );
		xpath.setXPathExpression( xpathExpression );
		xpath.setReferenceNode( root );

		res = xpath.resolve();

		if ( res instanceof NodeSet ) {
			NodeSet ns = ( NodeSet )res;
			l = new ArrayList();
			for ( int i = 0; i < ns.size(); i++ ) {
				FPNode sn = ( FPNode )ns.get( i );
				
				if ( sn.getType() == FPNode.COMMENT_NODE + 1 ) {
					if ( ns.size() == 1 ) {
						res = sn;
						return;
					}
				}
				
				l.add( sn );
			}
			container.getEditor().highlightNodes( l );
			selectNext();
		}
	}

	Object res;

	
	/** @return <code>true</code> if the result type is a nodeset */
	public boolean isResultNodesType() {
		return res instanceof NodeSet;
	}
	
	/** @return <code>true</code> if the result type is an attribute */
	public boolean isAttributeType() {
		return res instanceof FPNode;
	}

	/** @return the attribute value for an attribute type result */
	public String getAttributeValue() {
		if ( res instanceof FPNode ) {
			FPNode sn = ( FPNode )res;
			String attname = sn.getNodeContent();
			FPNode p = sn.getFPParent();
			return p.getAttribute( attname );
		}
		return null;
	}

	/** @return the xpath result. This is useful only for a non nodeset type */
	public Object getRawResult() { return res; }
	
	/**
	 * @return <code>true</code> if the previous xpath expression has result node
	 */
	public boolean hasResultNodes() {
		return ( l.size() > 0 );
	}

	/** 
	 * Select the next node
	 */
	public boolean selectNext() {
				
		if ( l.size() > 0 && 
				( index + 1 ) < l.size() ) {
			index++;
			FPNode node = ( FPNode )l.get( index );
			int line = node.getStartingLine();
			container.getEditor().highlightLine( line );
			return ( index + 1 < l.size() );
		} else
			return false;
	}

	/**
	 * Select the previous node
	 */
	public boolean selectPrevious() {
		if ( index > 0 ) {
			index--;
			FPNode node = ( FPNode )l.get( index );
			int line = node.getStartingLine();
			container.getEditor().highlightLine( line );			
			return ( index > 0 );
		} else
			return false;		
	}

}
