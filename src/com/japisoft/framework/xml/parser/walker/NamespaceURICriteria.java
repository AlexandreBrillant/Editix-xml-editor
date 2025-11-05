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

package com.japisoft.framework.xml.parser.walker;

import com.japisoft.framework.xml.parser.node.*;

/**
 * Namespace criteria
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class NamespaceURICriteria extends AbstractCriteria {
	private String uri;

	/** Match node in the following namespace uri */
	public NamespaceURICriteria(String uri) {
		super();
		this.uri = uri;
		if (uri == null)
			throw new RuntimeException("invalid criteria uri");
	}

	public boolean isValid(FPNode node) {
	
		if ( node.getNameSpaceURI() == null )
			return false;
		
		if ( containsMode ) {

			if ( ignoreCase ) {
				
				return node.getNameSpaceURI().toLowerCase().contains( uri.toLowerCase() );
				
			} else {
				
				return node.getNameSpaceURI().contains( uri );
				
			}
			
		} else {

			if ( ignoreCase ) {
				return node.getNameSpaceURI().equalsIgnoreCase( uri );
			}
			
			return node.getNameSpaceURI().equals( uri );
			
		}

	}

}


