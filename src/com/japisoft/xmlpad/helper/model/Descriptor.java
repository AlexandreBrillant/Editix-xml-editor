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

import java.awt.Color;

import javax.swing.Icon;

import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;

/**
 * Common interface for element descriptor
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public interface Descriptor {
	/** The final text added */
	public String toExternalForm();
	public boolean isRaw();
	public boolean isEnabled();
	public String getName();
	public String getNameForHelper();
	public String getComment();
	public Icon getIcon();
	public Color getColor();
	public AbstractHelperHandler getSource();
	public void dispose();
	/** Descriptor type like entities ... */
	public String getType();
	/** When activating a secondary helper automatically, attribute enum value, the cursor market is used for that */
	public boolean hasAutomaticNextHelper();
	public boolean startsWith( String sequence );
	public void setSequence( String sequence );
	public String getSequence();
	
	public static final String CURSOR = "~";
}

