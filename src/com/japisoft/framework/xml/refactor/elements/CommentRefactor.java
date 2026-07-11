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
