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

package com.japisoft.editix.plugin;

import javax.swing.text.Document;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class PluginContext {
	private Document doc;
	private int location;
	private String path;

	public PluginContext( Document doc, int location, String path ) {
		this.doc = doc;
		this.location = location;
		this.path = path;
	}
	
	/** @return the current document */
	public Document getDocument() { return doc; 
	}

	/** @return the current document location */
	public int getLocation() {
		return location;
	} 

	/** @return the current document path */
	public String getPath() {
		return path;
	}
}

