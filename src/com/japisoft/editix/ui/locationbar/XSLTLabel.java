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

package com.japisoft.editix.ui.locationbar;

import java.awt.Color;

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

@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)

*/
public class XSLTLabel implements Label {

	Color c = new Color( 200, 220, 200 );
	
	public Color getColor(FPNode node) {
		if ( node.matchContent( "template" ) )
			return c;
		if ( node.matchContent( "for-each" ) )
			return c;
		if ( node.matchContent( "variable" ) )
			return c;
		return null;
	}

	public String getLabel(FPNode node) {
		if ( node.matchContent( "variable" ) ) {
			String tmp = node.getNodeContent() + "[";
			tmp += node.getAttribute( "name" );
			tmp += "]";
			return tmp;			
		} else
		if ( node.matchContent( "template" ) ) {
			String tmp = node.getNodeContent() + "[";
			if ( node.hasAttribute( "name" ) )
				tmp += node.getAttribute( "name" );
			if ( node.hasAttribute( "match" ) )
				tmp += node.getAttribute( "match" );
			tmp += "]";
			return tmp;
		} else
		if ( node.matchContent( "for-each" ) ) {
			String tmp = node.getNodeContent() + "[";
			tmp += node.getAttribute( "select" );
			tmp += "]";
			return tmp;			
		}
		return null;
	}

}
