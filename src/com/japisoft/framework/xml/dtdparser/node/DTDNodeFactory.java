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

package com.japisoft.framework.xml.dtdparser.node;

/**
 * Factory for producing DTDNode. If you want to
 * produce your own DTDNode, please inherit from this
 * class. You will have to create custom DTDNode
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class DTDNodeFactory {

    public DTDNodeFactory() {
	super();   
    }

    /** Root node tied to any node */
    protected RootDTDNode root;

    /** Recall the getNodeForType with a <code>null</code> argument */
    public DTDNode getNodeForType( int type ) {
	return getNodeForType( type, null );
    }

    /** HERE the mehod you will have to override if you want to use custom node
	@return the good DTDNode for the specified type :
	DTDNode.COMMENT, DTDNode.ENTITY, DTDNode.ELEMENT, DTDNode.ATTRIBUTE */
    public DTDNode getNodeForType( int type, String arg ) {
	DTDNode node = null;

	switch( type ) {
	 case DTDNode.COMMENT :
	     node = new CommentDTDNode( arg );
	     break;
	 case DTDNode.ROOT : 
	     return ( root = new RootDTDNode() );
	 case DTDNode.ENTITY :
	     node = new EntityDTDNode( arg, null );
	     break;
	 case DTDNode.ELEMENT :
	     node = new ElementDTDNode( arg );
	     break;
	 case DTDNode.ELEMENT_SET :
	     node = new ElementSetDTDNode();
	     break;
	 case DTDNode.ATTRIBUTE :
	     node = new AttributeDTDNode( arg );
	     break;
	 case DTDNode.ELEMENT_REF :
	     node = new ElementRefDTDNode( arg );
	     break;
	}

	node.setRoot( root );
	return node;
    }

}

