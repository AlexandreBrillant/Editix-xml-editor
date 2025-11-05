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

package com.japisoft.editix.mapper.xsd;

import com.japisoft.editix.mapper.AbstractMapper;
import com.japisoft.framework.xml.parser.node.FPNode;

/** ComplexType and simpleType bound to an element */
public class TypeMapper extends AbstractMapper {

	public boolean canMap( FPNode node ) {
		return "element".equals( node.getContent() ) && 
					node.hasAttribute( getMapAttribute() );
	}

	@Override
	protected boolean isMatchingNode(
			FPNode sourceNode,
			FPNode walkingNode ) {
		boolean typeDef = 
			( "complexType".equals( walkingNode.getContent() ) || 
				"simpleType".equals( walkingNode.getContent() ) ) && walkingNode.hasAttribute( "name" );
		if ( !typeDef ) {
			return false;
		} else {
			String type = sourceNode.getAttribute( "type" );
			String name = walkingNode.getAttribute( "name" );
			int i = type.lastIndexOf( ":" );
			if ( i > -1 ) {
				type = type.substring( i );
			}
			return name.equals( type );
		}
	}

	public String getMapAttribute() {
		return "type";
	}	
	
	@Override
	public String toString() {
		return "Find type definition";
	}
}

