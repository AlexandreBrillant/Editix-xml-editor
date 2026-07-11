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

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

public class AttributeRefactor extends AbstractRefactor {
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";
	private static final String DELETE_ACTION = "DELETE (V1)";

	public static String[] ACTIONS = new String[] { RENAME_ACTION,
			DELETE_ACTION, };

	public AttributeRefactor() {
		super(Node.ATTRIBUTE_NODE);
	}

	public String[] getActions() {
		return ACTIONS;
	}

	public String getName() {
		return "Attribute";
	}
	
	protected Node refactorIt(Node node, RefactorAction ra) {

		if ( DELETE_ACTION.equals( ra.getAction() ) ) {
			if ( ra.matchOldValue( node.getNodeName() ) )
				return null;
		} else
		if ( RENAME_ACTION.equals( ra.getAction() ) ) {
			if ( ra.matchOldValue( node.getNodeName() ) ) {
				if ( !ra.isNewValueEmpty() )
					return new AttrProxyNode( ( Attr )node, ra.getNewValue() );
			}
		}
		return node;
	}

	public void initTable(RefactorTable table, FPNode context) {
		for ( int i = 0; i < context.getViewAttributeCount() ; i++ ) {
			table.init( i, context.getViewAttributeAt( i ) );
		}
	}	

}
