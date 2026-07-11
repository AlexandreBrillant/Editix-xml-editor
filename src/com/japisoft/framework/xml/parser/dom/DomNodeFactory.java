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

package com.japisoft.framework.xml.parser.dom;

import com.japisoft.framework.xml.parser.document.*;
import com.japisoft.framework.xml.parser.node.*;

/**
 * Node factory for DOM
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see NodeFactory
 */
public class DomNodeFactory implements NodeFactory {

	public DomNodeFactory() {
		super();
	}

	private Document refDoc;

	public void setRefDocument(Document doc) {
		this.refDoc = doc;
	}

	private static NodeFactory instance;

	/** @return a single instance of the node factory */
	public static NodeFactory getFactory() {
		if (instance == null)
			instance = new DomNodeFactory();
		return instance;
	}

	/** @return a text node */
	public FPNode getTextNode(String text) {
		FPNode node = new TextImpl(text);
		node.setDocument(refDoc);
		return node;
	}

	/** @return a tag node */
	public FPNode getTagNode(String tag) {
		FPNode node = new ElementImpl(tag);
		node.setDocument(refDoc);
		return node;
	}

	public FPNode getTagNode(int idTag) {
		FPNode node = new ElementImpl(idTag);
		node.setDocument(refDoc);
		return node;
	}	

	/** @return a comment node */
	public FPNode getCommentNode(String comment) {
		FPNode node = new CommentImpl(comment);
		node.setDocument(refDoc);
		return node;
	}

}

