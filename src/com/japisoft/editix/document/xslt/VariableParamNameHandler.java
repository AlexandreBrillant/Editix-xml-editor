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

package com.japisoft.editix.document.xslt;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class VariableParamNameHandler extends AbstractHelperHandler {

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {	
		XMLContainer container = document.getContainer();
		if ( container == null )
			return;

		if ( addedString == null )
			addedString = "";
		
		currentNode = container.getCurrentElementNode();
		if ( currentNode != null )
			orderedSearch( 
					addedString, 
					currentNode.getFPParent(), 
					currentNode );
	}

	private void orderedSearch( String addedString, FPNode parentNode, FPNode endChild ) {
		if ( parentNode != null ) {
			for ( int i = 0; i < parentNode.childCount(); i++ ) {
				FPNode child = parentNode.childAt( i );
				
				if ( child == endChild ) {
					orderedSearch( addedString, parentNode.getFPParent(), parentNode );
					break;
				}
				
				if ( child.matchContent( "variable" ) ||
						child.matchContent( "param" ) ) {
					
					if ( child.hasAttribute( "name" ) ) {						
						addDescriptor( 
								new BasicDescriptor(
										addedString +
										child.getAttribute( "name" ) ) );
					}
					
				}
			}
		}
	}

	protected String getActivatorSequence() {
		return "$";
	}

	public boolean haveDescriptors(FPNode currentNode,
			XMLPadDocument document, boolean insertBefore, int offset,
			String activatorString) {
		if ( "$".equals( activatorString ) ) {
			return document.isInsideAttributeValue( offset );
		} 
		return false;
	}

	public String getTitle() {
		return "variables or params";
	}

}

