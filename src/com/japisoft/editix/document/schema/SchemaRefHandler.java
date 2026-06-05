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

package com.japisoft.editix.document.schema;

import java.util.Enumeration;
import java.util.Iterator;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class SchemaRefHandler extends AbstractHelperHandler {

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {	

		XMLContainer container = document.getContainer();
		if ( container == null )
			return;
		FPNode root = container.getRootNode();
		if ( root == null )
			return;
		if ( container.getCurrentElementNode() == null )
			return;

		String currentElement = container.getCurrentElementNode().getContent();
		// Search for this element with all the name
		TreeWalker tw = new TreeWalker( root );
		Iterator enume = tw.getNodeByCriteria(
				new NodeNameCriteria( currentElement ), false );
		if ( enume != null ) {

			String prefix = "";

			// Check for targetNameSpace
			if ( root.hasAttribute( "targetNamespace" ) ) {
				String namespace = root.getAttribute( "targetNamespace" );
				Iterator<String> enum2 = root.getNameSpaceDeclaration();
				// Search a prefix
				if ( enum2 != null ) {
					while ( enum2.hasNext() ) {
						String p = ( String )enum2.next();
						if ( namespace.equals( root.getNameSpaceDeclarationURI( p ) ) ) {
							prefix = p + ":";
							break;
						}
					}
				}
			}

			while ( enume.hasNext() ) {
				FPNode n = ( FPNode )enume.next();
				if ( n.hasAttribute( "name" ) )
					addDescriptor( new BasicDescriptor( prefix + n.getAttribute( "name" ) ) );
			}
		}
	}

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		if ( activatorString == null ) {
			return match(
				document, 
				offset, 
				"", 
				"ref=\"" );
		} else {
			if  ( "\"".equals( activatorString ) ) {
				return match(
						document, 
						offset, 
						"", 
						"ref=" );
			} else
				return false;
		}
	}

	public String getTitle() {
		return "Content ref";
	}

	protected String getActivatorSequence() {
		return null;
	}	
	
	public int getPriority() {
		return 1;
	}	
}

