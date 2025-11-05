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

/*
 * Created on 30 ao�t 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.japisoft.xmlpad.helper.handler.schema.dtd;

import com.japisoft.dtdparser.node.*;
import com.japisoft.xmlpad.helper.model.AbstractEntityHelper;

/**
 * Helper working with a DTD definition
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class DTDEntityHelper extends AbstractEntityHelper {
	public DTDEntityHelper( RootDTDNode root ) {
		addEntitiesFromRoot( root );
	}
	
	private void addEntitiesFromRoot( RootDTDNode root ) {
		for ( int i = 0; i < root.getDTDNodeCount(); i++ ) {
			DTDNode node = root.getDTDNodeAt( i );
			if ( node.isEntity() ) {
				EntityDTDNode entity = ( EntityDTDNode )node;
				addEntity( 
					( (EntityDTDNode)node ).getName(),
					( (EntityDTDNode)node ).getValue() );
			}
		}
	}


}

