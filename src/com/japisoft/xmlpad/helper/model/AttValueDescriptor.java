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

package com.japisoft.xmlpad.helper.model;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class AttValueDescriptor extends AbstractDescriptor {

	private String value;
	private char delimiter;
	
	public AttValueDescriptor( String value, char delimiter ) {
		this.value = value;
		this.delimiter = delimiter;
	}

	public String getName() {
		return value;
	}
	
	public String getNameForHelper() {
		return getName();
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isRaw() {
		return false;
	}

	public String toExternalForm() {
		return ( addedPart == null ? "" : addedPart ) + 
					getName() + ( delimiter != 0 ? new String( 
							new char[] { delimiter } ) : "" );
	}

}
