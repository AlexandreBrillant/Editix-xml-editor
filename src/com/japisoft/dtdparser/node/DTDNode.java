package com.japisoft.dtdparser.node;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

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
public class DTDNode implements TreeNode {
	private String value;

	private int type;

	/** Comment type */
	public static final int COMMENT = 0;

	/** Entity type */
	public static final int ENTITY = 1;

	/** Element type */
	public static final int ELEMENT = 2;

	/** Attribute type */
	public static final int ATTRIBUTE = 3;

	/** Root type */
	public static final int ROOT = 4;

	/** Element set */
	public static final int ELEMENT_SET = 5;

	/** Element set content */
	public static final int ELEMENT_REF = 6;

	/** Reset the node type : COMMENT, ENTITY, ELEMENT or ATTRIBUTE */
	public void setNodeType(int type) {
		this.type = type;
	}

	/** @return the node type : COMMENT, ENTITY, ELEMENT or ATTRIBUTE */
	public int getNodeType() {
		return type;
	}
	
	public String toString() {
		return value + "(" + type + ")";
	}

	private RootDTDNode root;

	/** Reset the root node */
	public void setRoot(RootDTDNode node) {
		this.root = node;
	}

	/** @return the root node */
	public RootDTDNode getRoot() {
		return root;
	}

	/** @return true for comment node */
	public boolean isComment() {
		return type == COMMENT;
	}

	/** @return true for entity node */
	public boolean isEntity() {
		return type == ENTITY;
	}

	/** @return true for tag description */
	public boolean isElement() {
		return type == ELEMENT;
	}

	/** @return true for attribute description */
	public boolean isAttribute() {
		return type == ATTRIBUTE;
	}

	/** @return true if this is the root node */
	public boolean isRoot() {
		return type == ROOT;
	}

	/** @return true for a set of element */
	public boolean isElementSet() {
		return type == ELEMENT_SET;
	}

	/** @return true for a part of set of element */
	public boolean isElementRef() {
		return type == ELEMENT_REF;
	}

	private String comment = null;

	public void addNodeComment(String value) {
		if (comment == null) {
			comment = value;
		} else
			comment += "\n" + value;
	}

	public String getNodeComment() {
		return comment;
	}

	public void writeComment(PrintWriter output) {
		if (comment != null) {
			output.write(getDTDComment());
		}
	}

	protected String getDTDComment( String comment ) {
		if (comment == null)
			return "";
		return "<!--" + comment + "-->\n";
	}
	
	protected String getDTDComment() {
		return getDTDComment( comment );
	}

	private Vector children = null;

	/** @return true for empty node */
	public boolean isEmpty() {
		return (children == null);
	}

	/**
	 * Add a DTDNode child /
	 * 
	 * @return the added node
	 */
	public void addDTDNode(DTDNode node) {
		if (children == null)
			children = new Vector();
		children.add(node);
		node.setDTDParentNode(this);
	}

	/** Remove a DTDNode child */
	public void removeDTDNode(DTDNode node) {
		if (children != null)
			children.remove(node);
	}

	/** @return the DTDNode children count */
	public int getDTDNodeCount() {
		if (children == null)
			return 0;
		return children.size();
	}

	/** @return a DTDNode child at index from 0 to DTDNodeCount - 1 */
	public DTDNode getDTDNodeAt(int index) {
		if (children == null)
			return null;
		return (DTDNode) children.get(index);
	}

	/** @return the index of the node parameter or -1 for leaf case */
	public int getDTDNodeIndex(DTDNode node) {
		if (children == null)
			return -1;
		return children.indexOf(node);
	}

	/** @return all children */
	public Enumeration getDTDNodes() {
		if (children == null)
			return null;
		return children.elements();
	}

	private DTDNode parent;

	/** Reset the parent node */
	public void setDTDParentNode(DTDNode parent) {
		this.parent = parent;
	}

	/** @return the parent node */
	public DTDNode getDTDParentNode() {
		return parent;
	}

	/** @return all children matching the type */
	public Enumeration getDTDNodeForType(int type) {
		Vector v = new Vector();
		for (int i = 0; i < getDTDNodeCount(); i++) {
			DTDNode node = getDTDNodeAt(i);
			if (node.getNodeType() == type)
				v.add(node);
		}
		return v.elements();
	}

	// JTREE COMPATIBILITY
	
	public Enumeration children() {
		return getDTDNodes();
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public TreeNode getChildAt(int childIndex) {
		return getDTDNodeAt( childIndex );
	}

	public int getChildCount() {
		return getDTDNodeCount();
	}

	public int getIndex(TreeNode node) {
		return getDTDNodeIndex( (DTDNode)node );
	}

	public TreeNode getParent() {
		return getDTDParentNode();
	}

	public boolean isLeaf() {
		return getChildCount() == 0;
	}
	
	private boolean flagged = false;;
	
	public void setFlagged() { flagged = true; }
	public boolean isFlagged() { return flagged; }
}
