package com.japisoft.dtdparser.node;
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

// DTDNodeFactory ends here
