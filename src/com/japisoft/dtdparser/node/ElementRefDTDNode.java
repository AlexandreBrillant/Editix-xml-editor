package com.japisoft.dtdparser.node;

import java.io.*;

import com.japisoft.dtdparser.CannotFindElementException;

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
//		ElementSetDTDNode set = (ElementSetDTDNode) getDTDParentNode();
//		return Math.max(set.getOperator(), super.getOperator());
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

// ElementRefDTDNode ends here
