package com.japisoft.editix.ui.xslt.debug;

import org.w3c.dom.Node;

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
