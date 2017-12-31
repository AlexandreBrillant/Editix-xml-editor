package com.japisoft.framework.xml.refactor.elements;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

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
public class PrefixRefactor extends AbstractRefactor {

	private static final String ADD_ACTION = "(V1) ADD TO ELEMENT (V2)";
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";
	private static final String DELETE_ACTION = "DELETE (V1)";

	public static String[] ACTIONS = new String[] {
		RENAME_ACTION,
		DELETE_ACTION,
		ADD_ACTION
	};

	public PrefixRefactor() {
		super( ANY );
	}

        public String[] getActions() {
            return ACTIONS;
        }

        public String getName() {
            return "Prefix";
        }

	protected Node refactorIt( Node node, RefactorAction ra ) {
		if ( RENAME_ACTION.equals( ra.getAction() ) ) {
			if ( ra.matchOldValue( node.getPrefix() ) ) {
				if ( !ra.isNewValueEmpty() ) {
					node.setPrefix( ra.getNewValue() );
					return node;
				}
			} else
			if ( node.getNodeType() == Node.ATTRIBUTE_NODE ) {
				if ( node.getNodeName().equals( "xmlns:" + ra.getOldValue() ) ) {
					return new AttrProxyNode( ( Attr )node, "xmlns:" + ra.getNewValue() );
				}
			}
		} else
		if ( DELETE_ACTION.equals( ra.getAction() ) ) {
			if ( ra.matchOldValue( node.getPrefix() ) ) {
				node.setPrefix( null );
				return node;
			}
			
			if ( node.getNodeType() == Node.ATTRIBUTE_NODE ) {
				if ( node.getNodeName().equals( "xmlns:" + ra.getOldValue() ) ) {
					return null;
				}
			}
			
		} else
		if ( ADD_ACTION.equals( ra.getAction() ) ) {
			if ( ra.matchNewValue( node.getLocalName() ) ) {
				if ( ra.getOldValue() == null )
					throw new RuntimeException( "Prefix Refactor : Invalid oldValue for " + ADD_ACTION );
				node.setPrefix( ra.getOldValue() );				
			}
		}
		return node;
	}
	
	public void initTable(RefactorTable table, FPNode context) {
		if ( context.getNameSpacePrefix() != null )
			table.init( 0, context.getNameSpacePrefix() );
	}
}
