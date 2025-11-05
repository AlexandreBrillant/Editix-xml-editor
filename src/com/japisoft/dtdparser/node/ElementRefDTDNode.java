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

package com.japisoft.dtdparser.node;

import java.io.*;

import com.japisoft.dtdparser.CannotFindElementException;

/**
 * This is only a reference node to existing ELEMENT.
 * This ELEMENT reference is stored in the <code>ElementSetDTDNode</code>.
 * For instance <!ELMENT a (b,c)> means that a is a <code>ElementDTDNode</code> with
 * a child <code>ElementSetDTDNode</code> with two child <code>ElementRefDTDNode</code>
 *
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see ElementDTDNode
 */
public class ElementRefDTDNode
	extends ElementDTDNode
	implements DocumentWritable {
	public ElementRefDTDNode() {
		super();
		setNodeType(ELEMENT_REF);
	}

	/** This name is the reference. You can find the real ELEMENT by the
	 * <code>RootDTDNode</code> */
	public ElementRefDTDNode(String name) {
		super(name);
		setNodeType(ELEMENT_REF);
	}

	/** This is the max of the occurence between this element ref
	 * and the element set */
	public int getOperator() {
		return super.getOperator();
	}

	public String toString() {
		return "[ELEMENTREF] " + getName() + getOperatorString();
	}

	/** @return the reference node: null value means an error in the DTD */
	public ElementDTDNode getReferenceNode() {
		if (getName() == null)
			return null;
		try {
			return getRoot().getElementDefinitionByName(getName());
		} catch( CannotFindElementException e ) {
			return null;
		}
	}

	public void writeDocument(PrintWriter output) throws IOException {
		ElementDTDNode ref = getReferenceNode();
		if (ref != null)
			ref.writeDocument(output);
	}

}


