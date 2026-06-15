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

import java.util.ArrayList;

/**
 * Descriptor for attribute
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see TagDescriptor
 */
public class AttDescriptor extends AbstractDescriptor {
	private String name;
	private String defValue;
	private boolean required = true;
	private ArrayList enumValues;

	public AttDescriptor( String name, String defValue ) {
		this.name = name;
		this.defValue = defValue;
	}

	public AttDescriptor( String name, String defValue, boolean required ) {
		this( name, defValue );
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired( boolean required ) {
		this.required = required;
	}

	private boolean enabled = true;

	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getName() {
		return name;
	}

	public String getNameForHelper() {
		return getName();
	}

	public String getDefaultValue() {
		if ( defValue == null )
			return "";
		return defValue;
	}

	public void setDefaultValue( String def ) {
		this.defValue = def;
	}
	
	public void addEnumValue( String enumItem ) {
		if ( enumItem != null ) {
			if (enumValues == null)
				enumValues = new ArrayList();
			enumValues.add( enumItem );
		}
	}

	public String[] getEnumValues() {
		if (enumValues == null)
			return null;
		String[] res = new String[enumValues.size()];
		for (int i = 0; i < enumValues.size(); i++)
			res[i] = (String) enumValues.get(i);
		return res;
	}

	public boolean hasEnumValues() {
		return enumValues != null;
	}

	public boolean hasAutomaticNextHelper() {
		return hasEnumValues() || super.hasAutomaticNextHelper();
	}	
	
	public boolean isRaw() {
		return false;
	}

	public String toString() {
		return name + (required ? " (required) " : "");
	}

	public String toExternalForm() {
		if ( addedPart == null )
			addedPart = "";
		return addedPart + 
			getName() + "=\"" + Descriptor.CURSOR +  
				getDefaultValue() + "\"";
	}

}
