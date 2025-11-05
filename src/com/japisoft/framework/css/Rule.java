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

package com.japisoft.framework.css;

import java.util.List;

import org.w3c.dom.Element;

public interface Rule {

	public Selector getSelector();
	
	public List<Property> getProperties();
	
	public Property getProperty( String name );

	public void removeProperty( String name );
	
	public boolean match( Element node );	

	public Rule merge( Rule r );
	
	public static Rule EMPTY_RULE = new Rule() {

		public Rule merge(Rule r) {
			return this;
		}
		
		public boolean match(Element node) {
			return false;
		}
		
		public Selector getSelector() {
			return null;
		}
		
		public Property getProperty(String name) {
			return null;
		}
		
		public void removeProperty(String name) {
		}
		
		public List<Property> getProperties() {
			return null;
		}
		
	};

}

