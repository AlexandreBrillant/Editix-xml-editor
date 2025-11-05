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

package com.japisoft.editix.wizard.link;

public class BasicLink implements Link {

	private String label = "";
	private String uri = "";
	private boolean enabled = true;

	public BasicLink() {}
	
	public BasicLink(String uri,String label) {
		this.uri = uri;
		this.label = label;
	}
	
	public boolean isEmpty() {
		return "".equals( label ) && "".equals( uri );
	}
	
	public String getLabel() {
		return label;
	}

	public String getUri() {
		return uri;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public void setUri( String uri ) {
		this.uri = uri;
	}

	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

}

