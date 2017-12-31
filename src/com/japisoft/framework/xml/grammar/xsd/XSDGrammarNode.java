package com.japisoft.framework.xml.grammar.xsd;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.framework.xml.grammar.Grammar;
import com.japisoft.framework.xml.grammar.GrammarNode;
import com.japisoft.framework.xml.grammar.GrammarType;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public abstract class XSDGrammarNode implements GrammarNode {

	protected Node node = null;
	protected int minOccurs = 1;
	protected int maxOccurs = 1;
	protected XSDGrammar grammar = null;
	
	public XSDGrammarNode( XSDGrammar grammar, Node node ) {
		this.grammar = grammar;
		this.node = node;
		if ( node instanceof Element ) {
			Element e = ( Element )node;
			if ( e.hasAttribute( "minOccurs" ) ) {
				try {
					minOccurs = Integer.parseInt( e.getAttribute( "minOccurs" ) );
				} catch (NumberFormatException e1) {
				}
			}
			if ( e.hasAttribute( "maxOccurs" ) ) {
				if ( "unbounded".equalsIgnoreCase( 
						e.getAttribute( "maxOccurs" ) ) ) {
					maxOccurs = Integer.MAX_VALUE;
				} else {
					try {
						maxOccurs = Integer.parseInt( e.getAttribute( "maxOccurs" ) );
					} catch (NumberFormatException e1) {
					}					
				}
			}
		}
	}

	public Grammar getGrammar() {
		return grammar;
	}	
	
	public String getName() {
		return node.getLocalName();
	}

	public String getNamespace() {
		return null;
	}

	protected GrammarType type = null;
	
	public GrammarType getType() {
		if ( type == null )
			type = 
				new XSDGrammarType(
					grammar,
					( Element )node 
				);
		return type;
	}
	
	public int getMaxOccurs() {
		return maxOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public Node getDOMSource() { 
		return node; 
	}

	public Element getDOMElement() { 
		return ( Element )node; 
	}
	
	public void dump() {
		dump( 0 );
	}
	
	public void dump( int indentation ) {
		for ( int i = 0; i < indentation; i++ )
			System.out.print( " " );
		
		System.out.println( "node " + getName() + " / " + getClass() );
		GrammarType gt = getType();
		
		List<GrammarNode> values = gt.getValues();
		if ( values != null ) {
			for ( GrammarNode gn : values ) {
				if ( gn instanceof XSDGrammarNode ) {
					( ( XSDGrammarNode )gn ).dump( indentation + 1 );
				}
			}
		}
	}

}
