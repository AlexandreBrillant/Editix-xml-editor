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
 * Here an interface for customizing part of your document color
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface DocumentColorAccessibility {

	/** Define a color for this tagName. Use a <code>null</code> color for removing it */
	public void setColorForTag( String tagName, Color c );

	/** @return a custom color for this tagName */ 
	public Color getColorForTag( String tagName );
	
	/** @return <code>true</code> if a custom color exists for this tagName */
	public boolean hasColorForTag( String tagName );	

	/** Choose a particular color for an attribute. Use the color <code>null</code> for removing it */
	public void setColorForAttribute( String attributeName, Color c );
		
	/** @return the user custom color for this attribute */
	public Color getColorForAttribute( String attributeName );

	/** @return <code>true</code> if this attribute has a custom color */ 
	public boolean hasColorForAttribute( String attributeName );
	
	/** Choose a particular color for a tag prefix. Use the color <code>null</code> for removing it */
	public void setColorForPrefix( String prefixName, Color c );
	
	/** @return a custom color for this prefix name */
	public Color getColorForPrefix( String prefixName );

	public Color getColorForNamespaceURI( String uri );
	
	/** @return <code>true</code> if a color exist for this prefixName */
	public boolean hasColorForPrefix( String prefixName );	

	/** Choose a particular background color for a tag prefix. Use the color <code>null</code> for removing it */
	public void setBackgroundColorForPrefix( String prefixName, Color c );
	
	/** @return a custom background color for this prefix name */
	public Color getBackgroundColorForPrefix( String prefixName );

	/** @return <code>true</code> if a background color exist for this prefixName */
	public boolean hasBackgroundColorForPrefix( String prefixName );	

	/** This is only for inner usage, it mustn't be called by the user */
	void dispose();
	
}

