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

package com.japisoft.framework.xml.refactor.elements;

import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

public class EntityRefactor extends AbstractRefactor {
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";
	private static final String DELETE_ACTION = "DELETE (V1)";
	private static final String CONVERT_VALUE_ACTION = "CONVERT (V1) TO DTD VALUE";

	public static String[] ACTIONS = new String[] { 
		RENAME_ACTION,
		DELETE_ACTION
		// CONVERT_VALUE_ACTION 
	};

	public EntityRefactor() {
		super(Node.ENTITY_REFERENCE_NODE);
	}

	public String[] getActions() {
		return ACTIONS;
	}

	public String getName() {
		return "Entity";
	}

	protected Node refactorIt( Node node, RefactorAction ra ) {
		if ( DELETE_ACTION.equals( ra.getAction() ) ) {
			if ( ra.matchOldValue( node.getNodeName() ) )
				return null;
		} else if ( RENAME_ACTION.equals( ra.getAction() ) ) {
			if ( ra.matchOldValue(node.getNodeName() ) ) {
				if ( !ra.isNewValueEmpty() )
					return new EntityProxyNode((EntityReference) node, ra.getNewValue());
			}
		} else if ( CONVERT_VALUE_ACTION.equals( ra.getAction() ) ) {
			// Create a text node with the entity value
			if ( ra.matchOldValue( node.getNodeName() ) ) {
				EntityReference er = ( EntityReference )node;				
				return node.getOwnerDocument().createTextNode( node.getNodeValue() );
			}
		}
		return node;
	}

}
