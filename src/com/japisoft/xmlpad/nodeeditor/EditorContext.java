package com.japisoft.xmlpad.nodeeditor;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

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
public class EditorContext {
	private FPNode node;
	private String text;
	private XMLContainer container;

	/** 
	 * @param node The current edited node
	 * @param text The current edited text equals to the current edited node
	 */	
	public EditorContext( XMLContainer container, FPNode node, String text ) {
		this.node = node;
		this.text = text;
		this.container = container;
	}

	/** @return the current XMLContainer */	
	public XMLContainer getXMLContainer() { 
		return container; 
	}

	/** @return the current edited node */
	public FPNode getEditedNode() { 
		return node;
	}

	/** @return the current edited text */
	public String getEditedText() {
		return text;
	}

	private String result;

	/** Set the editing result. <code>null</code> means there's no
	 * editing changes
	 * @param result
	 */	
	public void setResult( String result ) {
		this.result = result;
	}

	/** @return the current editing result */
	public String getResult() {
		return result;
	}
}
