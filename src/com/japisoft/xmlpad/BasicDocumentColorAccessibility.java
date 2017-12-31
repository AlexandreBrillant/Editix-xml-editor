package com.japisoft.xmlpad;

import java.awt.Color;

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
class BasicDocumentColorAccessibility implements DocumentColorAccessibility {

	XMLContainer container;
	
	BasicDocumentColorAccessibility( XMLContainer container ) {
		this.container = container;
	}

	public Color getColorForAttribute(String attributeName) {
		return container.getColorForAttribute( attributeName );
	}
	public Color getColorForPrefix(String prefixName) {
		return container.getColorForPrefix( prefixName );
	}
	public Color getColorForTag(String tagName) {
		return container.getColorForTag( tagName );
	}
	public boolean hasColorForAttribute(String attributeName) {
		return container.hasColorForAttribute( attributeName );
	}
	public boolean hasColorForPrefix(String prefixName) {
		return container.hasColorForPrefix( prefixName );
	}
	public boolean hasColorForTag(String tagName) {
		return container.hasColorForTag( tagName );
	}
	public void setColorForAttribute(String attributeName, Color c) {
		container.setColorForAttribute( attributeName, c );
	}
	public void setColorForPrefix(String prefixName, Color c) {
		container.setColorForPrefix( prefixName, c );
	}
	public void setColorForTag(String tagName, Color c) {
		container.setColorForTag( tagName, c );
	}
	public Color getBackgroundColorForPrefix(String prefixName) {
		return container.getBackgroundColorForPrefix( prefixName );
	}
	public boolean hasBackgroundColorForPrefix(String prefixName) {
		return container.hasBackgroundColorForPrefix( prefixName );
	}
	public void setBackgroundColorForPrefix(String prefixName, Color c) {
		container.setBackgroundColorForPrefix( prefixName, c );
	}

	public void dispose() {
		this.container = null;
	}	
}
