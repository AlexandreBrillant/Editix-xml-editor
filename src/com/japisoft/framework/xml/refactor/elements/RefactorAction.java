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

package com.japisoft.framework.xml.refactor.elements;

import java.util.StringTokenizer;

public class RefactorAction {

	private String action, oldValue, newValue;

	public RefactorAction( String compactDesc ) {
		StringTokenizer st = new StringTokenizer( compactDesc, ";;", false );
		setAction( st.nextToken() );
		setOldValue( st.nextToken() );
		if ( st.hasMoreTokens() )
			setNewValue( st.nextToken() );
	}

	public RefactorAction( String action, String oldv, String newv ) {
		this.action = action;
		this.oldValue = oldv;
		this.newValue = newv;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getNewValue() {
		return newValue;
	}

	public boolean isNewValueEmpty() {
		return newValue == null || 
			"".equals( newValue );
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	
	public boolean matchOldValue( String value ) {
		if ( value == null )
			return false;
		//if ( "*".equals( oldValue ) )
		//	return true;
		return value.equals( oldValue );
	}

	public boolean containsOldValue( String value ) {
		if ( value == null )
			return false;
		return value.contains( oldValue );
	}
	
	public boolean matchNewValue( String value ) {
		if ( value == null )
			return false;
		return value.equals( newValue );
	}
	
	public String toString() {
		if ( oldValue == null )
			oldValue = "";
		if ( newValue == null )
			newValue = "";
		return action + ";;" + oldValue + ";;" + newValue;
	}

}
