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

import javax.swing.JComponent;

/**
 * This is for UI part of the XMLContainer. 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public interface IView {
	
	/** Show a border when true */
	public void setFocusView( boolean focusView );

	/** Return a set of listener from the addDocumentStateListener */
	public Object[] getDocumentStateListeners();

	/** Store a listener for a document */
	public void addDocumentStateListener( DocumentStateListener listener );

	/** Remove a listener for a document */
	public void removeDocumentStateListener( DocumentStateListener listener );

	/** Release this view */
	public void dispose();

	/** @return the bound container */
	public XMLContainer getContainer();

	/** @return the final view */
	public JComponent getFinalView();

}

