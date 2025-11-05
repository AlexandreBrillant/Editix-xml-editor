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

package com.japisoft.editix.editor.html.helper;

import java.util.List;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class IDRefHandler extends AbstractHelperHandler {

	@Override
	public boolean haveDescriptors(
			FPNode currentNode, 
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString) {

		return "#".equals( activatorString );

	}

	@Override
	protected void installDescriptors(FPNode currentNode, XMLPadDocument document, int offset, String activatorString) {
		
		if ( currentNode != null ) {
		
			List<FPNode> fv = currentNode.getDocument().getFlatNodes();
			if ( fv != null ) {
				for ( int i = 0; i < fv.size(); i++ ) {
					FPNode node = ( FPNode )fv.get( i );
					if ( node.hasAttribute( "id" ) ) {
						String idValue = node.getAttribute( "id" );
						
						BasicDescriptor bd = new BasicDescriptor( "#" + idValue );
						addOrderedDescriptor( bd );
					}
				}
			}
		
		}
		
	}
	
}

