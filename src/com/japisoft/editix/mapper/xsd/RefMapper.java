package com.japisoft.editix.mapper.xsd;

import com.japisoft.editix.mapper.AbstractMapper;
import com.japisoft.framework.xml.parser.node.FPNode;

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
public class RefMapper extends AbstractMapper {

	public boolean canMap( FPNode node ) {
		return ( "element".equals( node.getContent() ) ) && node.hasAttribute( getMapAttribute() );
	}

	@Override
	protected boolean isMatchingNode(FPNode sourceNode,
			FPNode walkingNode) {

		boolean elementDef = 
			"element".equals( walkingNode.getContent() ) && 
				walkingNode.hasAttribute( "ref" );

		if ( !elementDef ) {
			return false;
		} else {
			String ref = walkingNode.getAttribute( "ref" );
			int i = ref.lastIndexOf( ":" );
			if ( i > -1 ) {
				ref = ref.substring( i + 1 );
			}
			String name = sourceNode.getAttribute( "name" );
			return name.equals( ref );
		}
	}
	
	public String getMapAttribute() {
		return "name";
	}

	@Override
	public String toString() {
		return "Find the element references to this element";
	}

}
