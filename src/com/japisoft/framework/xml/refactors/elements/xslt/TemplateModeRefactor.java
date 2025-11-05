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

package com.japisoft.framework.xml.refactors.elements.xslt;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.elements.AbstractRefactor;
import com.japisoft.framework.xml.refactor.elements.RefactorAction;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

public class TemplateModeRefactor extends AbstractRefactor {
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";

	public static String[] ACTIONS = new String[] {
		RENAME_ACTION
	};

	public TemplateModeRefactor( ) {
		super( Node.ELEMENT_NODE );
	}

	public String[] getActions() {
		return ACTIONS;
	}

	public String getName() {
		return "xslt Template mode";
	}

	public boolean isDefault() {
		return false;
	}

	protected Node refactorIt( Node node, RefactorAction ra ) {
		if ( "template".equals( node.getLocalName() ) ) {
			Element e = ( Element )node;
			if ( ra.matchOldValue( e.getAttribute( "mode" ) ) ) {
				if ( RENAME_ACTION.equals( ra.getAction() ) ) {
					if ( !ra.isNewValueEmpty() )
						e.setAttribute( "mode", ra.getNewValue() );					
				}
			}
		} else {
			if ( "apply-templates".equals( node.getLocalName() ) ) {
				Element e = ( Element )node;
				if ( ra.matchOldValue( e.getAttribute( "mode" ) ) ) {
					if ( RENAME_ACTION.equals( ra.getAction() ) ) {
						if ( !ra.isNewValueEmpty() )
							e.setAttribute( "mode", ra.getNewValue() );
					}
				}
			}
		}
		return node;
	}

	public void initTable(RefactorTable table, FPNode context) {
		if ( context.matchContent( "template" ) ) {
			if ( context.hasAttribute( "mode" ) )
				table.init( 0, context.getAttribute( "mode" ) );
		} else
		if ( context.matchContent( "apply-templates" ) ) {
			if ( context.hasAttribute( "mode" ) )
				table.init( 0, context.getAttribute( "mode" ) );			
		}
	}
	
	
}

