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

package com.japisoft.framework.xml.refactors.elements.schema;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.elements.AbstractRefactor;
import com.japisoft.framework.xml.refactor.elements.RefactorAction;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

public class ElementRefactor extends AbstractRefactor {
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";
	private static final String DELETE_ACTION = "DELETE (V1)";

	public static String[] ACTIONS = new String[] {
		RENAME_ACTION,
		DELETE_ACTION,
	};

	public ElementRefactor() {
		super( Node.ELEMENT_NODE );
	}

	protected Node refactorIt(Node node, RefactorAction ra) {
		Element e = ( Element )node;
		if ( "element".equals( e.getLocalName() ) ) {
			if ( RENAME_ACTION.equals( ra.getAction() ) ) {
				if ( ra.matchOldValue( e.getAttribute( "name" ) )) {
					e.setAttribute( "name", ra.getNewValue() );
				} else
				if ( ra.matchOldValue( e.getAttribute( "ref" ) ) ) {
					e.setAttribute( "ref", ra.getNewValue() );
				} else
				if ( ra.matchOldValue( e.getAttribute( "substitutionGroup" ) ) ) {
					e.setAttribute( "substitutionGroup", ra.getNewValue() );
				}
			} else
			if ( DELETE_ACTION.equals( ra.getAction() ) ) {
				if ( ra.matchOldValue( e.getAttribute( "name" ) ) )
					return null;
				if ( ra.matchOldValue( e.getAttribute( "ref" ) ) )
					return null;
				if ( ra.matchOldValue( e.getAttribute( "substitutionGroup" ) ) ) {
					e.removeAttribute( "substitutionGroup" );
				}
			}
		}
		return node;
	}

	public String[] getActions() {
		return ACTIONS;
	}

	public String getName() {
		return "W3C Schema element";
	}
	
	public boolean isDefault() {
		return false;
	}

	public void initTable(RefactorTable table, FPNode context) {
		if ( context.matchContent( "element" ) ) {
			if ( context.hasAttribute( "name" ) )
				table.init( 0, context.getAttribute( "name" ) );
			else
				table.init( 0, context.getAttribute( "ref" ) );
		}
	}

}
