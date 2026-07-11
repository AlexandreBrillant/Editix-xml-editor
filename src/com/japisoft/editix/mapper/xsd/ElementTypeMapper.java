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

package com.japisoft.editix.mapper.xsd;

import com.japisoft.editix.mapper.AbstractMapper;
import com.japisoft.framework.xml.parser.node.FPNode;

public class ElementTypeMapper extends AbstractMapper {

	public boolean canMap( FPNode node ) {
		return ( "complexType".equals( node.getContent() ) || "simpleType".equals( node.getContent() ) ) && node.hasAttribute( getMapAttribute() );
	}

	@Override
	protected boolean isMatchingNode(FPNode sourceNode,
			FPNode walkingNode) {

		boolean elementDef = "element".equals( walkingNode.getContent() ) && walkingNode.hasAttribute( "type" );

		if ( !elementDef ) {
			return false;
		} else {
			String type = walkingNode.getAttribute( "type" );
			int i = type.lastIndexOf( ":" );
			if ( i > -1 ) {
				type = type.substring( i + 1 );
			}
			return sourceNode.getAttribute( "name" ).equals( type );
		}
	}
	
	public String getMapAttribute() {
		return "name";
	}

	@Override
	public String toString() {
		return "Find element(s) usage for this type";
	}

}
