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

package com.japisoft.editix.mapper.fo;

import com.japisoft.editix.mapper.AbstractMapper;
import com.japisoft.framework.xml.parser.node.FPNode;

public class MasterReferenceMapper extends AbstractMapper {

	public boolean canMap(FPNode node) {
		return node.hasAttribute( getMapAttribute() );
	}

	@Override
	protected boolean isMatchingNode(
		FPNode sourceNode,
		FPNode walkingNode) {		
		boolean ok = walkingNode.hasAttribute( "master-reference" );
		if ( ok ) {
			return walkingNode.getAttribute( "master-reference" ).equals( 
					sourceNode.getAttribute( "master-name" ) 
			);
		} else
			return false;	
	}

	public String getMapAttribute() {
		return "master-name";
	}	
	
	@Override
	public String toString() {
		return "find master page sequence(s) from this page name";
	}	
		
}

