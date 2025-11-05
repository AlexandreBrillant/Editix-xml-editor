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

package com.japisoft.framework.xml.parser.node;

/**
 * Factory for building node.
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see MutableNode
 * @see FPNode
 * @see NodeFactory */
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
	public FPNode getTextNode(String text) {
		FPNode node = new FPNode(FPNode.TEXT_NODE, text);
		return node;
	}

	/** @return a tag node */
	public FPNode getTagNode(String tag) {
		FPNode node = new FPNode(FPNode.TAG_NODE, tag);
		return node;
	}

	public FPNode getTagNode( int tag ) {
		FPNode node = new FPNode(FPNode.TAG_NODE, tag);
		return node;
	}

	/** @return a comment node */
	public FPNode getCommentNode(String comment) {
		FPNode node = new FPNode(FPNode.COMMENT_NODE, comment);
		return node;
	}

}


