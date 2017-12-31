package com.japisoft.editix.mapper;

import java.util.ArrayList;
import java.util.List;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;


/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
