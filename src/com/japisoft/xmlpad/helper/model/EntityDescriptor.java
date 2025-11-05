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

import com.japisoft.xmlpad.SharedProperties;

/**
 * Descriptor for XML entity
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see EntityHelper
 */
public class EntityDescriptor extends AbstractDescriptor {
	private String name;
	private String value;

	public EntityDescriptor( String name, String value ) {
		this.name = name;
		this.value = value;	
		type = "entity";
		setComment( "Entity : " + value );
	}

	public boolean isRaw() {
		return false;
	}

	public String getName() { 
		if ( name.length() > SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER )
			return name.substring( 0, SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER ) + "...";
		return name;
	}
	
	public String getNameForHelper() {
		return getValueForHelper() + " (" + getName() + ")";
	}
	
	public String getValue() { return value; }

	public String getValueForHelper() {
		if ( value.length() > SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER )
			return value.substring( 0, SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER ) + "...";
		return value;
	}
	
	public boolean isEnabled() {
		return true;
	}
	
	public String getBuiltEntity() {
		return name + ";";
	}
	
	public String toExternalForm() {
		return name + ";";
	}
	
	public String toString() {
		return getName() + " : " + getValue();
	}

}

