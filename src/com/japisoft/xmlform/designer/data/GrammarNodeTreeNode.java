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

package com.japisoft.xmlform.designer.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.japisoft.framework.xml.grammar.GrammarAttribute;
import com.japisoft.framework.xml.grammar.GrammarContainer;
import com.japisoft.framework.xml.grammar.GrammarElement;
import com.japisoft.framework.xml.grammar.GrammarNode;
import com.japisoft.framework.xml.grammar.GrammarText;
import com.japisoft.framework.xml.grammar.GrammarType;
import com.japisoft.framework.xml.grammar.xsd.XSDGrammar;

public class GrammarNodeTreeNode extends AbstractTreeNode {

	private GrammarNode node = null;

	public GrammarNodeTreeNode( 
		ArrayList<GrammarNode> processed, 
		GrammarNode node ) {
		this.node = node;		
		processed.add( node );
		GrammarType type = node.getType();
		List<GrammarNode> nodes = type.getValues();
		if ( nodes != null && 
				!( node instanceof GrammarText ) && 
					!(node instanceof GrammarAttribute ) ) {
			for ( GrammarNode gn : nodes ) {
				if ( processed.contains( gn ) )
					continue;
				GrammarNodeTreeNode gntn = new GrammarNodeTreeNode( processed, gn );
				gntn.parent = this;
				addChild( gntn );
			}
		}	
	}

	@Override
	public String toString() {
		return node.getName();
	}
	
	public GrammarNode getSource() {
		return node;
	}

	public GrammarNodeTreeNode resolveRelativeXPath( String xpath ) {
		if ( xpath.startsWith( "/" ) ) {
			xpath = xpath.substring( 1 );
			// Don't resolve the root element
			return this;
		}

		if ( xpath.startsWith( "@" ) ) {
			String attributeName = xpath.substring( 1 );
			for ( int i = 0; i < getChildCount(); i++ ) {
				GrammarNodeTreeNode child = 
					( GrammarNodeTreeNode )getChildAt( i );
				if ( child.node instanceof GrammarAttribute ) {
					if ( attributeName.equals( child.node.getName() ) )
						return child;
				}
			}
		} else {
			int i = xpath.lastIndexOf( "/" );
			if ( i > -1 )
				xpath = xpath.substring( 0, i );
			
			for ( i = 0; i < getChildCount(); i++ ) {

				GrammarNodeTreeNode child = 
					( GrammarNodeTreeNode )getChildAt( i );
				
				if ( child.node instanceof GrammarContainer ) {
					// Explore the container
					GrammarNodeTreeNode result = 
						child.resolveRelativeXPath( xpath );
					if ( result != null )
						return result;
				} else
				if ( child.node instanceof GrammarElement ) {
					if ( xpath.equals( 
							child.node.getName() ) )
						return child;
				} else
				if ( child.node instanceof GrammarText ) {
					if ( "text()".equals( xpath ) )
							return child;
				}
				
			}

		}

		return null;
	}

	public String getId() { 
		return toXPath();
	}

	public String toXPath() {

		if ( node instanceof GrammarContainer ) {

			return ( ( GrammarNodeTreeNode )getParent() ).toXPath();

		} else {

			StringBuffer sb = new StringBuffer();
			if ( node instanceof GrammarAttribute ) {
				sb.append( "@" ).append( toString() );
			} else {

				if ( node.getType().getValues() == null ) {	// Simple type

					if ( "#text".equals( toString() ) ) {

					} else {
						sb.append( toString() );
						sb.append( "/" );
					}

					sb.append( "text()" );
				} else
					sb.append( toString() );

			}

			sb.insert( 0, "/" );

			if ( parent instanceof GrammarNodeTreeNode ) {
				sb.insert(
						0,
						( ( GrammarNodeTreeNode )parent ).toXPath() );
			}

			return sb.toString();

		}
	}
	
	public static void main( String[] args ) throws Exception {
		
		String p = "c:/travail/soft/japisoft-editix-2014/distrib/install-content/samples/xmlform/purchaseOrder.xsd";
		XSDGrammar g = new XSDGrammar( p );
		GrammarNodeTreeNode gntn = new GrammarNodeTreeNode( new ArrayList<GrammarNode>(), g.getGlobalElement( "purchaseOrder" ) );
		JFrame f = new JFrame();
		JTree t = new JTree( gntn );
		f.add( new JScrollPane( t ) );
		f.setVisible( true );
		
	}

}

