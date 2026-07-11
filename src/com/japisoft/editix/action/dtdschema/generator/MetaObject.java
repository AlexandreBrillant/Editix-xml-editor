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

package com.japisoft.editix.action.dtdschema.generator;

/**
 * Common objet
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class MetaObject {

	public static final String TEXT_TYPE = "TEXT";
	public static final String DATE_TYPE = "DATE";
	public static final String TIME_TYPE = "TIME";
	public static final String BOOL_TYPE = "BOOLEAN";
	public static final String DECIMAL_TYPE = "DECIMAL";
	public static final String DOUBLE_TYPE = "DOUBLE";
	public static final String URI_TYPE = "URI";
	public static final String ID_TYPE = "ID";
	public static final String IDREF_TYPE = "IDREF";
	
	public static final String[] AVAILABLE_TYPES = {
			TEXT_TYPE,
			DATE_TYPE,
			TIME_TYPE,
			BOOL_TYPE,
			DECIMAL_TYPE,
			DOUBLE_TYPE,
			URI_TYPE,
			ID_TYPE,
			IDREF_TYPE
	};

	public static final String[] OCCURENCES = {
		"0",
		"1",
		"unbounded"
	};

	private boolean always = true;
	
	public void setAlways( boolean always ) {
		this.always = always;
	}

	public boolean isAlways() {
		return always;
	}

}
