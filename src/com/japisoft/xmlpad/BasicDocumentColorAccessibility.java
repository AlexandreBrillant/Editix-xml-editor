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

package com.japisoft.xmlpad;

import java.awt.Color;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
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
	@Override
	public Color getColorForNamespaceURI(String uri) {
		return null;
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

