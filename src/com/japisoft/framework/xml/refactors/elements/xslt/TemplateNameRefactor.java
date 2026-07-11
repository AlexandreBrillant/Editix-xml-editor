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

package com.japisoft.framework.xml.refactors.elements.xslt;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.elements.AbstractRefactor;
import com.japisoft.framework.xml.refactor.elements.RefactorAction;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

public class TemplateNameRefactor extends AbstractRefactor {
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";

	public static String[] ACTIONS = new String[] {
		RENAME_ACTION
	};
	
	public TemplateNameRefactor( ) {
		super( Node.ELEMENT_NODE );
	}

	public String[] getActions() {
		return ACTIONS;
	}

	public String getName() {
		return "xslt Template name";
	}

	public boolean isDefault() {
		return false;
	}

	protected Node refactorIt( Node node, RefactorAction ra ) {
		if ( "template".equals( node.getLocalName() ) ) {
			Element e = ( Element )node;
			if ( ra.matchOldValue( e.getAttribute( "name" ) ) ) {
				if ( RENAME_ACTION.equals( ra.getAction() ) ) {
					if ( !ra.isNewValueEmpty() )
						e.setAttribute( "name", ra.getNewValue() );					
				}
			}
		} else {
			if ( "call-template".equals( node.getLocalName() ) ) {
				Element e = ( Element )node;
				if ( ra.matchOldValue( e.getAttribute( "name" ) ) ) {
					if ( RENAME_ACTION.equals( ra.getAction() ) ) {
						e.setAttribute( "name", ra.getNewValue() );
					}
				}
			}
		}
		return node;
	}

	public void initTable(RefactorTable table, FPNode context) {
		if ( context.matchContent( "template" ) ) {
			if ( context.hasAttribute( "name" ) )
				table.init( 0, context.getAttribute( "name" ) );
		} else
		if ( context.matchContent( "call-template" ) ) {
			if ( context.hasAttribute( "name" ) )
				table.init( 0, context.getAttribute( "name" ) );			
		}
	}
	
	
}
