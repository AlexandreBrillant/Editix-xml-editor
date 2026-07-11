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

package com.japisoft.framework.css;

public class CSSDim {

	private int value;
	private boolean percent = false;
	
	public static CSSDim ZERO = new CSSDim( 0, false );
	
	public CSSDim( String value ) {
		if ( value.endsWith( "%" ) ) {
			percent = true;
			value = value.substring( 0, value.length() - 1 );
		} else
		if ( value.endsWith( "px" ) ) {
			value = value.substring( 0, value.length() - 2 );
		}
		this.value = Integer.parseInt( value );
	}
	
	public CSSDim( int value, boolean percent ) {
		this.value = value;
		this.percent = percent;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isPercent() {
		return this.percent;
	}
	
	public String toString() {
		return Integer.toString( value ) + ( percent ? "%" : "px" );
	}
	
}
