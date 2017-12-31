package com.japisoft.xmlpad.helper.model;

import java.util.ArrayList;
import java.util.HashMap;

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
public class SchemaNode {

	public int type = 0;

	public TagDescriptor element = null;

	public ArrayList next = null;

	public boolean marked = false;

	public boolean marked2 = false;

	public String namespace = null;

	public SchemaNode parent = null;

	public static final int ELEMENT = 0;

	public static final int EMPTY = 1;

	public static final int ROOT = 2;

	public static final int OP_OR = 3;

	public static final int OP_AND = 4;

	static int ID = 0;

	int id = 0;

	private SchemaNode() {
		ID++;
		this.id = ID;
	}

	public SchemaNode(int type) {
		this();
		this.type = type;
	}

	private SchemaNode(SchemaNode node) {
		this(); // We don't maintain the id
		this.type = node.type;
		this.element = node.element;
	}

	public SchemaNode(TagDescriptor element) {
		this();
		this.element = element;
	}

	public void addNext(SchemaNode node) {
		if (next == null)
			next = new ArrayList();

		next.add(node);
		node.parent = this;
	}

	public void removeNext(SchemaNode node) {
		if (next != null)
			next.remove(node);
	}

	public SchemaNode nextSibling() {
		if (parent == null)
			return null;
		ArrayList nextFromParent = parent.next;
		int i = nextFromParent.indexOf(this);
		if (nextFromParent.size() > (i + 1))
			return (SchemaNode) nextFromParent.get(i + 1);
		return null;
	}

	public SchemaNode getParent() {
		return parent;
	}

	public int getSchemaNodeCount() {
		if (next == null)
			return 0;
		return next.size();
	}

	public SchemaNode getSchemaNode(int index) {
		if (next == null)
			return null;
		return (SchemaNode) next.get(index);
	}

	public boolean isSigma() {
		return (type == EMPTY);
	}

	public boolean isRoot() {
		return (type == ROOT);
	}

	public boolean isOpAND() {
		return (type == OP_AND);
	}

	public boolean isOpOR() {
		return (type == OP_OR);
	}

	public boolean isElement() {
		return (type == ELEMENT);
	}

	public void dump() {
		dump(this, 0);
	}

	public String toString() {
		if (isElement())
			return "e(" + element.getName() + ")";
		else if (isOpOR())
			return "or" + "(" + id + ")";
		else if (isOpAND())
			return "and" + "(" + id + ")";
		else if (isSigma())
			return "?";
		else if (isRoot())
			return "R";
		return super.toString();
	}

	private void dump(SchemaNode node, int level) {
		for (int i = 0; i < level; i++)
			System.out.print("\t");
		if (node.isElement()) {
			System.out.println(node);
			for (int i = 0; i < node.getSchemaNodeCount(); i++) {
				if (node.getSchemaNode(i) != node)
					dump(node.getSchemaNode(i), level + 1);
				else
					dump(new SchemaNode(node), level + 1);
			}
		} else if (node.isOpAND()) {
			System.out.println(node);
			for (int i = 0; i < node.getSchemaNodeCount(); i++) {
				if (node.getSchemaNode(i) != node)
					dump(node.getSchemaNode(i), level + 1);
				else
					dump(new SchemaNode(node), level + 1);
			}
		} else if (node.isOpOR()) {
			System.out.println(node);
			for (int i = 0; i < node.getSchemaNodeCount(); i++)
				if (node.getSchemaNode(i) != node)
					dump(node.getSchemaNode(i), level + 1);
				else
					dump(new SchemaNode(node), level + 1);
		} else if (node.isRoot()) {
			System.out.println(node);
			for (int i = 0; i < node.getSchemaNodeCount(); i++)
				dump(node.getSchemaNode(i), level + 1);
		} else if (node.isSigma()) {
			System.out.println(node);
		}
	}

	public Object clone() {
		SchemaNode newRoot = new SchemaNode(this);
		clone(this, newRoot, new HashMap());
		return newRoot;
	}

	private void clone(SchemaNode nodeRef, SchemaNode newNode, HashMap mem) {

		for (int i = 0; i < nodeRef.getSchemaNodeCount(); i++) {
			SchemaNode childRef1 = (SchemaNode) nodeRef.getSchemaNode(i);

			if (childRef1 == nodeRef) {
				newNode.addNext(newNode);
			} else {
				if (mem.containsKey(childRef1))
					newNode.addNext((SchemaNode) mem.get(childRef1));
				else {
					SchemaNode cloneRef1 = (SchemaNode) childRef1.clone();
					mem.put(childRef1, cloneRef1);
					newNode.addNext(cloneRef1);
				}
			}

		}
	}

}
