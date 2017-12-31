package com.japisoft.framework.xml.refactor.elements;

import org.w3c.dom.Node;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

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
