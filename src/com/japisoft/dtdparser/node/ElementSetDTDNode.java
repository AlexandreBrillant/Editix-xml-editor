package com.japisoft.dtdparser.node;

import java.io.*;
import java.util.Hashtable;

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
public class ElementSetDTDNode
	extends ElementDTDNode
	implements DocumentWritable {

	public ElementSetDTDNode() {
		super();
		setNodeType(ELEMENT_SET);
	}

	private Hashtable htElementRef;

	/** ( element1 | element2 ) */
	public static int CHOICE_TYPE = 0;
	/** ( element1 , element2 ) */
	public static int SEQUENCE_TYPE = 1;

	private int type;

	/** Set the type of the set :
	 * CHOICE_TYPE or SEQUENCE_TYPE */
	public void setType(int type) {
		this.type = type;
	}

	/** @return the type of the set */
	public int getType() {
		return type;
	}

	/** @return <code>true</code> if the nodeName is supported, previousNode is the previous child, occurence if for the child count 
	<pre>
	&lt;A&gt;
	&lt;B&gt; &lt;/B&gt;
	&lt;C&gt; &lt;/C&gt;
	&lt;/A&gt;
	</pre>
	B is the previous child node of C. B appears only 1
	so we will invoke as sample <code>isNodeChildSupported( "A", "B", 1 )</code> */
	public boolean isNodeChildSupported(
		String nodeName,
		String previousNode,
		int occurrence) {
		if (htElementRef == null)
			return false;

		ElementRefDTDNode ref = (ElementRefDTDNode) htElementRef.get(nodeName);

		boolean res = false;

		if (ref != null) {
			if (ref.getOperator() == ElementDTDNode.ONE_ITEM_OPERATOR) {
				res = (occurrence == 1);
			} else if (
				ref.getOperator() == ElementDTDNode.ZERO_MORE_ITEM_OPERATOR) {
				res = (occurrence == 1) || (occurrence == 0);
			} else if (
				ref.getOperator() == ElementDTDNode.ONE_MORE_ITEM_OPERATOR) {
				res = (occurrence >= 0);
			} else if (
				ref.getOperator() == ElementDTDNode.ZERO_ONE_ITEM_OPERATOR) {
				res = (occurrence > 0);
			}
		} else {

			// Check for other set child
			for (int i = 0; i < getDTDNodeCount(); i++) {
				if (getDTDNodeAt(i).isElementSet()) {
					if (((ElementSetDTDNode) getDTDNodeAt(i))
						.isNodeChildSupported(nodeName, null, occurrence))
						res = true;
				}
			}
		}

		if ((getType() == CHOICE_TYPE) || (previousNode == null))
			return res;
		else {

			// Get the previous ref
			if (ref == null)
				return false; // To correct later

			int i = getDTDNodeIndex(ref);
			if (i == 0)
				return false;
			for (int j = i - 1; j >= 0; j--) {
				if (getDTDNodeAt(j).isElementRef()) {
					ElementRefDTDNode n = (ElementRefDTDNode) getDTDNodeAt(j);
					if (n.getName().equals(previousNode))
						return true;
				}
			}

			return false;
		}
	}

	/** Overriding for elementRef */
	public void addDTDNode(DTDNode node) {
		if (node.isElementRef()) {
			if (htElementRef == null)
				htElementRef = new Hashtable();
			if ( ( ( ElementRefDTDNode )node ).getName() != null )
				htElementRef.put(((ElementRefDTDNode) node).getName(), node);
		}
		super.addDTDNode(node);
	}

	/** Write a minimal valid XML document */
	public void writeDocument(PrintWriter output) throws IOException {
		if ((getOperator() == ElementDTDNode.ZERO_ONE_ITEM_OPERATOR)
			|| (getOperator() == ElementDTDNode.ZERO_MORE_ITEM_OPERATOR))
			return;
		if (getType() == CHOICE_TYPE) {
			// Choose the first one
			for (int i = 0; i < getDTDNodeCount(); i++) {
				if (getDTDNodeAt(i) instanceof DocumentWritable) {
					((DocumentWritable) getDTDNodeAt(i)).writeDocument(output);
					break;
				}
			}

		} else { // Write all
			for (int i = 0; i < getDTDNodeCount(); i++) {
				if (getDTDNodeAt(i) instanceof DocumentWritable) {
					((DocumentWritable) getDTDNodeAt(i)).writeDocument(output);
				}
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < getDTDNodeCount(); i++) {
			if (i > 0) {
				if (getType() == CHOICE_TYPE)
					sb.append("|");
				else
					sb.append(",");
			}

			DTDNode node = getDTDNodeAt(i);
			if (!(node instanceof ElementSetDTDNode)) {
				if (node instanceof ElementDTDNode) {
					ElementDTDNode e = (ElementDTDNode) node;
					sb.append(e.getName()).append(e.getOperatorString());
				}
			} else
				sb.append(node.toString());
		}

		sb.append(")").append(getOperatorString());
		return sb.toString();
	}

}

// ElementSetDTDNode ends here
