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

package com.japisoft.editix.ui.xslt.debug;

import org.w3c.dom.Node;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class DebugElement {
	
	public Node element;
	public int line;
	public boolean resultElement;
	public String uri;
	
	public String elementName;
	
	public DebugElement( Node element, int line, boolean resultElement, String uri ) {
		this.element = element;
		this.line = line;
		this.resultElement = resultElement;
		this.elementName = element.getNodeName();
		this.uri = uri;
	}

	public DebugElement( String element, int line, String uri ) {
		this( element, line, false, uri );
	}

	public DebugElement( String element, int line, boolean resultElement, String uri ) {
		this.elementName = element;
		this.line = line;
		this.resultElement = resultElement;
		this.uri = uri;
	}
	
	public String toString() {
		return elementName;
	}

}

