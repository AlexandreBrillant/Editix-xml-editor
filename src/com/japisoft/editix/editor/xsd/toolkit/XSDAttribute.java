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

package com.japisoft.editix.editor.xsd.toolkit;

public class XSDAttribute {
	public String name;
	public int type;
	public String[] def;
	public boolean anyString;

	public static final int STRING_TYPE = 0;	
	public static final int BOOLEAN_TYPE = 1;
	public static final int NON_NEGATIVE_INTEGER = 2; 
	public static final int ELEMENT_REF = 3; 
	public static final int TYPEC_REF = 4;	// Complex
	public static final int TYPEP_REF = 8;	// Simple
	public static final int TYPE_REF = 9;	// Complex or Simple
	public static final int ATTRIBUTE_REF = 5;
	public static final int GROUP_REF = 6;
	public static final int SCHEMA_REF = 7;
	public static final int ATTRIBUTE_GROUP_REF = 10;
	
	public XSDAttribute( String name, int type, String[] def, boolean anyString ) {
		this.name = name;
		this.type = type;
		this.def = def;
		this.anyString = anyString;
	}
	
	public XSDAttribute( String name, String[] def, boolean anyString ) {
		this( name, STRING_TYPE, def, anyString );
	}

}
