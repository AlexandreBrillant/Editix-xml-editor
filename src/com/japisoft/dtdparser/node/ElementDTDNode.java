package com.japisoft.dtdparser.node;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;

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
public class ElementDTDNode extends DTDNode implements DocumentWritable {

	public ElementDTDNode() {
		super();
		setNodeType( ELEMENT );
	}

	/** Build an Element node with the following name */
	public ElementDTDNode( String name ) {
		this();
		setName( name );
	}

	private Hashtable htAttribute;
	private String name;

	private String attComment = null;
	
	public void addAttComment( String attComment ) {
		if ( this.attComment == null )
			this.attComment = attComment;
		else
			this.attComment += "\n" + attComment;
	}

	/** Overriding for getting attribute node */
	public void addDTDNode(DTDNode node) {
		if (node.isAttribute()) {
			if (htAttribute == null) {
				htAttribute = new Hashtable();
			}
			AttributeDTDNode n = ( AttributeDTDNode ) node;
			if ( attComment != null )
				n.addNodeComment( attComment );
			
			if  ( n.getName() == null )  {
			} else
			htAttribute.put(n.getName(), n);
		}
		super.addDTDNode(node);
	}

	/** @return a DTD attribute declaration for the following name */
	public AttributeDTDNode getAttributeDeclaration(String name) {
		if (htAttribute == null)
			return null;
		return (AttributeDTDNode) htAttribute.get(name);
	}

	/** Name of the element */
	public void setName( String name ) {
		this.name = name;
	}

	/** @return the name of the element ( tag name ) */
	public String getName() {
		return name;
	}

	private boolean empty = false;

	/** empty node : by default false */
	public void setEmptyElement( boolean empty ) {
		this.empty = empty;
	}

	/** @return true if the node is empty */
	public boolean isEmptyElement() {
		return empty;
	}

	/** default operator */
	public final static int ONE_ITEM_OPERATOR = 0;
	/** '?' operator */
	public final static int ZERO_ONE_ITEM_OPERATOR = 1;
	/** '*' operator */
	public final static int ZERO_MORE_ITEM_OPERATOR = 2;
	/** '+' operator */
	public final static int ONE_MORE_ITEM_OPERATOR = 3;

	private int op = ONE_ITEM_OPERATOR;

	/** Set the occurency operator :
	- ZERO_MORE_ITEM_OPERATOR,
	- ONE_MORE_ITEM_OPERATOR,
	- ZERO_ONE_ITEM_OPERATOR */
	public void setOperator(int op) {
		this.op = op;
	}

	/** @return "*", "+", "?" or "" */
	public String getOperatorString() {
		switch ( op ) {
			case ZERO_MORE_ITEM_OPERATOR :
				return "*";
			case ONE_MORE_ITEM_OPERATOR :
				return "+";
			case ZERO_ONE_ITEM_OPERATOR :
				return "?";
		}
		return "";
	}

	/** @return the occurency operator */
	public int getOperator() {
		return op;
	}

	private boolean pcdata = false;

	/** PCDATA support for text content */
	public void setPCDATA(boolean pcdata) {
		this.pcdata = pcdata;
	}

	/** PCDATA support for text content */
	public boolean hasPCDATA() {
		return pcdata;
	}

	private boolean any = false;

	/** Can have any element children */
	public void setANY(boolean any) {
		this.any = any;
	}

	/** Can have any element children */
	public boolean hasANY() {
		return any;
	}

	/** @return <code>true</code> if the nodeName is supported as child, 
	previousName is to check if the sequence if a good one,
	occurrence if for the child count */
	public boolean isNodeChildSupported(
		String nodeName,
		String previousName,
		int occurrence) {

		if (isEmptyElement())
			return false;
		if (hasANY())
			return true;
		if (hasPCDATA())
			return false;
		for (int i = 0; i < getDTDNodeCount(); i++) {
			if (getDTDNodeAt(i) instanceof ElementSetDTDNode) {
				ElementSetDTDNode set = (ElementSetDTDNode) getDTDNodeAt(i);

				return set.isNodeChildSupported(
					nodeName,
					previousName,
					occurrence);
			}
		}

		return false;
	}

	/** Write a minimal valid XML document */
	public void writeDocument(PrintWriter output) throws IOException {
		
		output.write("<" + getName());

		// Write attribute
		Enumeration enume = getDTDNodeForType(DTDNode.ATTRIBUTE);
		while (enume.hasMoreElements()) {
			AttributeDTDNode att = (AttributeDTDNode) enume.nextElement();
			if (att.getUsage() == AttributeDTDNode.REQUIRED_ATT) {
				output.write(" ");
				output.write(att.getName());
				output.write("=\"");
				output.write(att.getDefaultValue());
				output.write("\"");
			}
		}

		if (isEmptyElement()) {
			output.write("/>\n");
		} else {
			output.write(">\n");
			enume = getDTDNodeForType(DTDNode.ELEMENT_SET);
			if (enume.hasMoreElements()) {
				ElementSetDTDNode set = (ElementSetDTDNode) enume.nextElement();
				set.writeDocument(output);
			}
			output.write("</" + getName() + ">\n"); // ANY ?
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append( getDTDComment() );
		
		
		sb.append("<!ELEMENT ").append(getName()).append(" ");
		if (isEmptyElement())
			sb.append("EMPTY>");
		else if (hasANY())
			sb.append("ANY>");
		else if (hasPCDATA())
			sb.append("(#PCDATA)>");
		else {
			// Show the set once
			Enumeration enume = getDTDNodeForType(DTDNode.ELEMENT_SET);
			if (enume.hasMoreElements())
				sb.append("" + enume.nextElement());
			sb.append(">");
		}

		// Show the attributes
		Enumeration enume = getDTDNodeForType(DTDNode.ATTRIBUTE);
		if (enume.hasMoreElements()) {
			sb.append("\n");
			sb.append( getDTDComment( attComment ) );
			sb.append("<!ATTLIST " + getName());
			sb.append(" \n");
			while (enume.hasMoreElements()) {
				AttributeDTDNode node = (AttributeDTDNode) enume.nextElement();
				sb.append(node.toString());
				sb.append("\n");
			}

			sb.append(">");
		}

		return sb.toString();
	}

}

// ElementDTDNode ends here
