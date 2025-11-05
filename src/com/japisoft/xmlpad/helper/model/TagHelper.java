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

/**
 * This interface helps the UI to known available tag and required attributes
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see TagHelper */
public interface TagHelper extends Helper {
	public void setNamespace( String namespace );
	/** @return available tags */
	public TagDescriptor[] getTags();
	/** @return a tag descriptor for this tag name */
	public TagDescriptor getTag( FPNode node );
	/** add a new descriptor */
	public TagDescriptor addTagDescriptor( TagDescriptor tag );
	/** Reset the current location for computing available tags */
	public void setLocation( FPNode locationPath, int offset );
	/** Force a new namespace prefix */
	//public void setForcePrefix( String locationPrefix );
	/** @return the initial schema location */
	public String getSource();
}

