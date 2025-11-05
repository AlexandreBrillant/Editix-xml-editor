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

package com.japisoft.stylededitor.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class DocumentElement extends BasicNodeElement {

	private List<NodeElement> children = null;

	public DocumentElement( 
			Document document, 
			org.w3c.dom.Document root ) {
		super( document, root, 0 );
		resetContent( root );
	}

	private int endOffset;
	
	private NodeElement rootElement;
	
	public NodeElement getRootElement() {
		return rootElement;
	}

	public void resetContent( org.w3c.dom.Document doc ) {
		children = null;
		rootElement = null;

		int startOffset = 0;
		DocumentTraversal traversal = ( DocumentTraversal )doc;

		TreeWalker tw = traversal.createTreeWalker( 
				doc.getDocumentElement(), 
				NodeFilter.SHOW_ALL, 
				null, 
				false );

		Node n = null;

		// Split text node for carriage return

		ArrayList<Text> splittable = null;
		ArrayList<Text> stripSpace = null;
		
		while ( ( n = tw.nextNode() ) != null ) {
			if ( n instanceof Text ) {
				String value = n.getNodeValue();
				
				// Trim empty space
				if ( "".equals( value.trim() ) ) {
					if ( !( n.getParentNode().getLastChild() == n && n.getParentNode().getFirstChild() == n ) ) { 
						if ( stripSpace == null ) {
							stripSpace = new ArrayList<Text>();
						}
						stripSpace.add( ( Text )n );
						continue;
					}
				}

				if ( value.contains( "\n" ) && !"\n".equals( value )) {
					if ( splittable == null ) {
						splittable = new ArrayList<Text>();
					}
					splittable.add( ( Text )n );
				}
			}
		}
		
		if ( splittable != null ) {
			for ( Text toSplit : splittable ) {
				org.w3c.dom.Element parent = 
					( org.w3c.dom.Element )toSplit.getParentNode();

				String value = toSplit.getNodeValue();
				String part = "";

				char c = 0;
				
				for ( int i = 0; i < value.length(); i++ ) {
					c = value.charAt( i );
					if ( c == '\n' ) {
						Text newText = doc.createTextNode( part ); 	// + "\n" );
						parent.insertBefore( newText, toSplit );
						part = "";
					} else {
						part += c;
					}
				}

				if ( !"".equals( part ) ) {
					Text newText = doc.createTextNode( part );
					parent.insertBefore( newText, toSplit );					
				} else {
					// Text newText = doc.createTextNode( "" );
					// parent.insertBefore( newText, toSplit );
				}

				toSplit.getParentNode().removeChild( toSplit );
			}
		}

		if ( stripSpace != null ) {
			for ( Text t : stripSpace ) {
				t.getParentNode().removeChild( t );
			}
		}

		/////////////////////////////////////////////////////////////////////

		tw = traversal.createTreeWalker( 
			doc, 
			NodeFilter.SHOW_ALL, 
			null, 
			false 
		);		

		n = null;

		while ( ( n = tw.nextNode() ) != null ) {
			NodeElement element = null;
			if ( n instanceof Text ) {
				element = new TextElement(
					document, 
					( Text )n, 
					startOffset 
				);	
			} else {
				element = new BasicNodeElement(
					document,
					n,
					startOffset
				);
				
				if ( n instanceof org.w3c.dom.Element ) {
					if ( rootElement == null )
						rootElement = element;
				}
				
			}
			addChild( element );
			
			if ( element.getEndOffset() > startOffset ) {
				startOffset = element.getEndOffset() + 1;
			}

			// For the document
			endOffset = element.getEndOffset();
		}
	}

	public void addChild( NodeElement e ) {
		if ( children == null )
			children = new ArrayList<NodeElement>();
		children.add( e );
		
		e.getNode().setUserData( "element", e, null );
	}

	public AttributeSet getAttributes() {
		return null;
	}

	public Document getDocument() {
		return document;
	}

	public Element getElement( int index ) {
		if ( children == null || index < 0 )
			return null;
		return children.get( index );
	}

	public int getElementCount() {
		if ( children == null )
			return 0;
		return children.size();
	}

	public int getElementIndex( int offset ) {
		int result = -1;
		for ( int i = 0; i < getElementCount(); i++ ) {
			Element child = getElement( i );
			if ( !( child instanceof TextElement ) ) {
				continue;
			}
			if ( offset >= child.getStartOffset() && 
					offset <= child.getEndOffset() ) {				
				result = i;
				break;
			} else
				if ( child.getStartOffset() > offset ) {
					break;
				}
		}
		return result;
	}

	public int getStartOffset() {
		return 0;
	}

	public int getEndOffset() {
		return this.endOffset;
	}

	public String getName() {
		return "root";
	}

	public Element getParentElement() {
		return null;
	}

	public boolean isLeaf() {
		return children != null && children.size() > 0;
	}
	
}

