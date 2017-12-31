package com.japisoft.framework.xml.parser.node;

import com.japisoft.framework.xml.parser.document.*;

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
public class NodeFactoryImpl implements NodeFactory {

	private NodeFactoryImpl() {
		super();
		THIS = this;
	}

	static NodeFactoryImpl THIS;
	
	public static NodeFactory getInstance() {
		if ( THIS == null )
			new NodeFactoryImpl();
		return THIS;
	}
	
	private static NodeFactory instance;

	/** @return a single instance of the node factory */
	public static NodeFactory getFactory() {
		if (instance == null)
			instance = new NodeFactoryImpl();
		return instance;
	}

	/** @return a text node */
	public MutableNode getTextNode(String text) {
		FPNode node = new FPNode(FPNode.TEXT_NODE, text);
		return node;
	}

	/** @return a tag node */
	public MutableNode getTagNode(String tag) {
		FPNode node = new FPNode(FPNode.TAG_NODE, tag);
		return node;
	}

	public MutableNode getTagNode( int tag ) {
		FPNode node = new FPNode(FPNode.TAG_NODE, tag);
		return node;
	}

	/** @return a comment node */
	public MutableNode getCommentNode(String comment) {
		FPNode node = new FPNode(FPNode.COMMENT_NODE, comment);
		return node;
	}

}

// NodeFactory ends here
