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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.japisoft.framework.xml.grammar.GrammarAttribute;
import com.japisoft.framework.xml.grammar.GrammarElement;
import com.japisoft.framework.xml.grammar.GrammarNode;
import com.japisoft.framework.xml.grammar.GrammarType;

public class XSDGrammarElement extends XSDGrammarNode 
		implements GrammarElement {

	public XSDGrammarElement( 
		XSDGrammar grammar, 
		Element node ) {
		super( grammar, node );
	}

	public boolean isEmpty() {
		GrammarType type = getType();
		return !type.isComplex();
	}

	public String infer() {
		StringBuffer sb = new StringBuffer();
		sb.append( "<" );
		sb.append( getName() );

		List<GrammarAttribute> l = getAttributes( true );
		if ( l != null ) {
			if ( l.size() > 0 ) {		
				sb.append( " " );
				for ( GrammarAttribute ga : l ) {
					sb.append( ga.infer() );
				}
			}
		}
		if ( isEmpty() ) {
			sb.append( "/>" );
		} else {
			sb.append( ">" );
			sb.append( "</" ).append( getName() ).append( ">" );
		}

		return sb.toString();
	}

	public List<GrammarAttribute> getAttributes( boolean required ) {
		List<GrammarNode> values = getType().getValues();
		if ( values != null ) {
			List<GrammarAttribute> attributes = new ArrayList<GrammarAttribute>();
			for ( GrammarNode gn : values ) {
				if ( gn instanceof GrammarAttribute ) {
					if ( required && !( ( GrammarAttribute )gn ).isRequired() )
						continue;
					attributes.add( ( GrammarAttribute )gn );
				}
			}
			return attributes;
		}
		return null;
	}

	@Override
	public String getName() {
		Element e = getDOMElement();
		if ( e.hasAttribute( "name" ) )
			return e.getAttribute( "name" );
		else
			if ( e.hasAttribute( "ref" ) )
				return e.getAttribute( "ref" );
			else
				return "?";
	}

}

