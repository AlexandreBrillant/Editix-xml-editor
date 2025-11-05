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

package com.japisoft.framework.xml.grammar.xsd;

import com.japisoft.framework.xml.grammar.Grammar;
import com.japisoft.framework.xml.grammar.GrammarText;
import com.japisoft.framework.xml.grammar.GrammarType;

public class XSDGrammarText implements GrammarText {

	public XSDGrammarText( XSDGrammar grammar, String value ) {
		this.value = value;
	}

	public Grammar getGrammar() {
		return null;
	}	
	
	public String infer() {
		return "";
	}	

	public XSDGrammarText( String value, GrammarType type ) {
		this.value = value;
		this.type = type;
	}
		
	private String value;
	private GrammarType type = null;
	
	public String getValue() {
		return value;
	}

	public String getName() { 
		return "#text"; }

	public String getNamespace() { 
		return null; }

	public int getMaxOccurs() {
		return 1;
	}
	
	public int getMinOccurs() {
		return 1;
	}

	public GrammarType getType() {
		if ( type == null )
			type = new XSDGrammarType( "string" );
		return type;
	}

}

