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

package com.japisoft.editix.document.xslfo;

import java.util.Enumeration;
import java.util.Iterator;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class FoInternalDestinationHandler extends AbstractHelperHandler {

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
		if ( addedString == null )
			addedString = "";		
		// Search for page master-name
		TreeWalker tw = new TreeWalker( root );
		Iterator e = tw.getNodeByCriteria( new AttributeCriteria( "id" ), true );
		while ( e.hasNext() ) {
			FPNode n = ( FPNode )e.next();
			String mr = n.getAttribute( "id" );
			if ( mr != null ) {
				addDescriptor(
						new BasicDescriptor( 
								addedString + 
								mr + 
								addedString ) );
			}
		}
	}

	protected String getActivatorSequence() {
		return null;
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
				"internal-destination=\"" );
		} else {
			if  ( "\"".equals( activatorString ) ) {
				return match(
						document, 
						offset, 
						"", 
						"internal-destination=" );
			} else
				return false;
		}
	}

	public String getTitle() {
		return "Internal link";
	}

	public int getPriority() {
		return 1;
	}

}
