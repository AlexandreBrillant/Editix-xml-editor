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

/*
 * Created on Feb 21, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.japisoft.editix.action.dtdschema.generator;

import java.util.ArrayList;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MetaAttribute extends MetaObject {

	public String name;
	public ArrayList values;

	public MetaAttribute( String name, String value ) {
		this.name = name;
		this.values = new ArrayList();
		this.values.add( value );
	}

	public String getName() { return name; }
	public ArrayList getValues() { return values; }
	
	public void addValue( String value ) {
		values.add( value );
	}
	
	public boolean equals( Object obj ) {
		if ( obj instanceof MetaAttribute ) {
			MetaAttribute ma = ( MetaAttribute )obj;
			if ( ma.name.equals( name ) )
				return true;
			return false;
		} else
			return super.equals( obj );
	}
	
	private String type;

	public void setType( String type ) {
		this.type = type;
	}

	public String getType() {
		if ( type == null )
			type = TEXT_TYPE;
		return type;
	}
}

