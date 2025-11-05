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

import org.w3c.dom.Node;

public class CommentRefactor extends AbstractRefactor {
	private static final String DELETE_ACTION = "DELETE IF CONTAINS (V1)";

	public static String[] ACTIONS = new String[] { DELETE_ACTION, };

	public CommentRefactor() {
		super( Node.COMMENT_NODE );
	}

	public String[] getActions() {
		return ACTIONS;
	}

	public String getName() {
		return "Comment";
	}

	protected Node refactorIt( Node node, RefactorAction ra ) {
		if ( DELETE_ACTION.equals( ra.getAction() ) ) {			
			if ( ra.containsOldValue( node.getNodeValue() ) )
				return null;
		}
		return node;
	}

}

