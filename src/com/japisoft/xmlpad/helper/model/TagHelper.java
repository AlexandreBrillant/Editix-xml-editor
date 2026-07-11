// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

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
