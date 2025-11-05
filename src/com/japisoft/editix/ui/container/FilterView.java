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

package com.japisoft.editix.ui.container;

import javax.swing.JComponent;

import org.w3c.dom.Document;

import com.japisoft.xmlpad.helper.HelperManager;

/** Filter view bound to the txt editor */
public interface FilterView extends SerializeStateObject {

	public String getName();	
	public void init( HelperManager helper, String location, Document xmlContent ) throws Exception;
	public JComponent getView();
	public boolean isModified();
	public void dispose();

	public void requestFocus();
	public void cut();
	public void copy();
	public void paste();
	
}

