package com.japisoft.xmlpad.helper.handler.schema.dtd;

import com.japisoft.dtdparser.node.DTDNode;
import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.ElementRefDTDNode;
import com.japisoft.dtdparser.node.ElementSetDTDNode;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.SchemaNodeProducer;

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
public class DTDToSchemaNode implements SchemaNodeProducer {

	public SchemaNode getSchemaNode( Object element ) {
		ElementDTDNode e = ( ElementDTDNode ) element;
		SchemaNode root = new SchemaNode( SchemaNode.ROOT );

		if ( !e.isEmptyElement() )
			buildSchema( e, root );

		return root;
	}
	
	private void buildSchema(
			ElementDTDNode elementParent,
			SchemaNode schemaParent ) {

		for ( int i = 0; i < elementParent.getDTDNodeCount(); i++ ) {

			DTDNode node = elementParent.getDTDNodeAt( i );

			if ( node.isElementSet() ) {
				ElementSetDTDNode set = ( ElementSetDTDNode ) node;
				addElementSet( set, schemaParent );
			}

		}
	}

	private void addElementSet( ElementSetDTDNode set, SchemaNode schemaParent ) {

		SchemaNode setNode = new SchemaNode(
				set.getType() == ElementSetDTDNode.CHOICE_TYPE ? 
						SchemaNode.OP_OR
						: SchemaNode.OP_AND );

		// Complete the setNode
		
		for ( int j = 0; j < set.getDTDNodeCount(); j++ ) {
			
			DTDNode node2 = set.getDTDNodeAt( j );

			if ( node2.isElementRef() ) {
				
				addElementRef( 
						( ElementRefDTDNode )node2,
						setNode 
				);
				
			} else
			if ( node2.isElementSet() ) {
				
				addElementSet( 
						( ElementSetDTDNode )node2, 
						setNode );

			}

		}

		// Manage the operator

		int op = set.getOperator();

		if ( op == ElementDTDNode.ONE_ITEM_OPERATOR ) {
			
			// Ok

			schemaParent.addNext( setNode );
						
		} else 
        if ( op == ElementDTDNode.ONE_MORE_ITEM_OPERATOR ) {

			// +
		
        	schemaParent.addNext( setNode );
    		setNode.addNext( setNode );

		} else
		if ( op == ElementDTDNode.ZERO_MORE_ITEM_OPERATOR ) {
			
			// *

			SchemaNode or = new SchemaNode( SchemaNode.OP_OR );
			or.addNext( setNode );			
			or.addNext( 
					new SchemaNode( SchemaNode.EMPTY ) );
			setNode.addNext( setNode );
			schemaParent.addNext( or );
			
		} else
		if ( op == ElementDTDNode.ZERO_ONE_ITEM_OPERATOR ) {

			// ?

			SchemaNode or = new SchemaNode( SchemaNode.OP_OR );
			or.addNext( setNode );
			or.addNext( new SchemaNode( SchemaNode.EMPTY ) );
			schemaParent.addNext( or );

		}
		
	}

	private void addElementRef( ElementRefDTDNode node, SchemaNode schemaParent ) {

		// Search the element

		ElementDTDNode target = node.getReferenceNode();

		if ( target == null )
			return;	// Error ?

		SchemaNode elementNode = new SchemaNode( 
				new DTDTagDescriptor( target ) );

		// Manage the operator

		int op = node.getOperator();

		if ( op == ElementDTDNode.ONE_ITEM_OPERATOR ) {
			
			// Ok
			
			schemaParent.addNext( elementNode );

		} else 
        if ( op == ElementDTDNode.ONE_MORE_ITEM_OPERATOR ) {

			// +

    		schemaParent.addNext( elementNode );
        	        	
        	elementNode.addNext( elementNode );

		} else
		if ( op == ElementDTDNode.ZERO_MORE_ITEM_OPERATOR ) {

			// *

			SchemaNode or = new SchemaNode( SchemaNode.OP_OR );
			or.addNext( new SchemaNode( SchemaNode.EMPTY ) );
			or.addNext( elementNode );
			or.addNext( or );
			schemaParent.addNext( or );

		} else
		if ( op == ElementDTDNode.ZERO_ONE_ITEM_OPERATOR ) {

			// ?

			SchemaNode or = new SchemaNode( SchemaNode.OP_OR );

			or.addNext( elementNode );
			or.addNext( new SchemaNode( SchemaNode.EMPTY ) );
						
			schemaParent.addNext( or );

		}

	}

}
