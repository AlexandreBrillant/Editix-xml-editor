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

package com.japisoft.framework.xml.refactor.elements;

import org.w3c.dom.Node;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

public abstract class AbstractRefactor implements RefactorObj {
	private RefactorAction ra;
	private short type;
	public static short ANY = -1;

	public AbstractRefactor(short type) {
		this.type = type;
	}

	public Node refactor(Node node) {
		if (node.getNodeType() == type || type == ANY) {
			return refactorIt(node, ra);
		}
		return node;
	}

	public boolean isDefault() { 
		return true; 
	}

	public Node preRefactor(Node node) {
		if (node.getNodeType() == type || type == ANY) {
			return preRefactorIt(node, ra);
		}
		return node;
	}

	abstract protected Node refactorIt(Node node, RefactorAction ra);

	protected Node preRefactorIt(Node node, RefactorAction ra) {
		return node;
	}

	public void initTable( RefactorTable table, FPNode context ) {}	
	
	// ----------------------------------------

	public void setRefactorAction(RefactorAction ra) {
		this.ra = ra;
	}

	public String toString() {
		return getName();
	}
	
	public void stop() {
	}	
}

