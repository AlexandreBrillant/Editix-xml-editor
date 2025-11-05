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
 * Search for namespace prefix declaration
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class NamespacePrefixCriteria extends AbstractCriteria {
	private String prefix;

	/** Match node in the following namespace prefix */
	public NamespacePrefixCriteria(String p) {
		super();
		this.prefix = p;
		if (p == null)
			throw new RuntimeException("invalid criteria prefix");
	}

	public boolean isValid(FPNode node) {
		if ( node.getNameSpacePrefix() == null )
			return false;
		
		if ( ignoreCase ) {
			
			if ( containsMode ) {
				
				return node.getNameSpacePrefix().toLowerCase().contains( prefix.toLowerCase() );
				
			} else
				return prefix.equalsIgnoreCase( node.getNameSpacePrefix() );
		} else {
			
			if ( containsMode ) {
				
				return node.getNameSpacePrefix().contains( prefix );
				
			} else {
				
				return prefix.equals( node.getNameSpacePrefix() );
				
			}

		}

	}

}

