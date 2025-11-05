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

package com.japisoft.editix.mapper;

import java.util.ArrayList;
import java.util.List;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;


public abstract class AbstractMapper implements Mapper {

	public List<MatchingResult> map(IXMLPanel panel, FPNode currentNode) {
		ArrayList<MatchingResult> result = new ArrayList<MatchingResult>();
		
		FPNode root = ( FPNode )currentNode.getDocument().getRoot();
		if ( panel instanceof XMLContainer ) {
			XMLContainer container = ( XMLContainer )panel;
		}
		
		walkInto(
			panel,
			currentNode,
			root, 
			result 
		);

		return result;
	}

	public String[] getMapAttributes() {
		return new String[] { getMapAttribute() };
	}

	protected String getMapAttribute() {
		return null;
	}
	
	protected void walkInto(
		IXMLPanel panel,
		FPNode sourceNode,
		FPNode walkingNode, 
		ArrayList<MatchingResult> result  ) {
		if ( isMatchingNode( sourceNode, walkingNode ) )
			result.add( new MatchingResult( panel, walkingNode ) );
		for ( int i = 0; i < walkingNode.childCount(); i++ ) {
			if ( walkingNode.getType() == FPNode.TAG_NODE )
				walkInto(
					panel,
					sourceNode,
					walkingNode.childAt( i ), 
					result 
				);
		}
	}

	protected boolean isMatchingNode( FPNode sourceNode, FPNode walkingNode ) {
		return false;
	}

}

