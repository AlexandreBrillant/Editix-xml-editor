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

package com.japisoft.xmlpad.helper.model;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLEditor;

/**
 * Helper interface
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public interface Helper {

	public String getNamespace();
	
	/** For inner usage */
	public void setEditor( XMLEditor editor );
	
	/** Show the helper at this offset */
	public boolean show( int offset, String charToInsert );

	/** Prepare the helper at this location */
	public void setLocation( FPNode loc, int offset );
	
	/** Free unused ressource */
	public void dispose();
	
	/** @return a title for the helper popup */
	public String getTitle();
	
}

