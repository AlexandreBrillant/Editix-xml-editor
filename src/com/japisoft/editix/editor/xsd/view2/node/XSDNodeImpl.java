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

package com.japisoft.editix.editor.xsd.view2.node;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view2.nodeview.XSDNodeView;
import com.japisoft.editix.editor.xsd.view2.nodeview.XSDNodeViewFactory;

public class XSDNodeImpl implements XSDNode {

	// Resolved node (reference...)	
	private Element node;
	private List<XSDNode> children;

	// Resolve reference and complexe type for having a complete tree structure
	private boolean resolveRef = true;
	
	public XSDNodeImpl( Element node, boolean resolveRef ) {
		this.resolveRef = resolveRef;
		this.node = node;
		this.source = node;
		node.setUserData( "node", this, null );
		init( node, resolveRef );
	}
	
	public XSDNodeImpl( Element node ) {
		this( node, true );
	}
	
	// Initial node
	private Element source;
	
	@Override
	public Element getDOM() {
		return source;
	}
	
	@Override
	public void repaint() {
		getView().invalidateBuffer();
	}
	@Override
	public XSDNode getParent() {
		return parent;
	}

	@Override
	public void remove() {
		/*
		Element parent = ( Element )node.getParentNode();
		if ( parent != null ) {
			XSDNode nodeToInvalidate = ( XSDNode )parent.getUserData( "node" );
			parent.removeChild( node );
			while ( isInvisible( parent ) ) {
				Element tmp = parent;
				
				if ( parent.getParentNode() instanceof Element ) {
					parent = ( Element )parent.getParentNode();

					if ( parent.hasChildNodes() ) {
						
						if ( parent.getChildNodes().getLength() == 1 )
							parent.removeChild( tmp );
						
					}
					else
						break;
				} else
					break;
				
			}
			XSDNode node = ( XSDNode )parent.getUserData( "node" );
			if ( node != null )
				node.invalidate();
			if ( nodeToInvalidate != null && node != nodeToInvalidate )
				nodeToInvalidate.invalidate();
		}
		*/
				
		Element parent = ( Element )source.getParentNode();
		parent.removeChild( source );
		
		while ( parent.getUserData( "node") == null || isInvisible( parent ) ) {
			if ( parent.getParentNode() instanceof Element )
				parent = ( Element )parent.getParentNode();
			else
				break;
		}
		XSDNode node = ( XSDNode )parent.getUserData( "node" );
		if ( node != null ) {
			node.invalidate();
		}
		
	}

	@Override
	public void invalidate() {
		SchemaHelper.unmark( source );
		init( source );
	}

	private boolean ignore( Element element ) {
		String name = element.getLocalName();
		if ( name == null )
			name = element.getNodeName();
		return ( "annotation".equals( name ) ) || !isOpened();
	}

	@Override
	public boolean isRoot() {
		return parent == null;
	}
	
	@Override
	public boolean append(Element newChildren) {
		if ( newChildren.getParentNode() != null ) {
			newChildren = ( Element )newChildren.cloneNode( true );
		}

		String tag = SchemaHelper.getElementName( newChildren );
		String[] authorizedChildren = SchemaHelper.getChildrenForElement( getDOM() );

		for ( String good : authorizedChildren ) {
			if ( tag.equals( good ) ) {
				getDOM().appendChild( newChildren );
				invalidate();
				return true;
			}
		}

		return false;
	}

	@Override
	public void moveDown() {
		Element parent = ( Element )getDOM().getParentNode();
		Node n = getDOM();
		while ( n != null ) {
			n = n.getNextSibling();
			if ( n instanceof Element ) {
				parent.removeChild( n );
				parent.insertBefore( n, getDOM() );
				getParent().invalidate();
				break;
			}			
		}		
	}
	
	@Override
	public void moveUp() {
		Element parent = ( Element )getDOM().getParentNode();
		Node n = parent.getFirstChild();
		Node p = null;
		while ( n != null ) {
			if ( n instanceof Element ) {
				if ( n != getDOM() ) {
					p = n;	
				} else {
					if ( p != null ) {
						parent.removeChild( getDOM() );
						parent.insertBefore( getDOM(), p );
						getParent().invalidate();
						break;
					}	
				}
			}
			n = n.getNextSibling();			
		}
	}

	private boolean isInvisible( Element element ) {
		// Global definition, can't be invisible
		if ( "schema".equals( SchemaHelper.getElementName( element.getParentNode() ) ) ) {
			return false;
		}
		String name = SchemaHelper.getElementName( element );
		if ( name == null )
			name = element.getNodeName();
		return ( "complexType".equals( name ) || 
					"simpleType".equals( name ) ||
						"simpleContent".equals( name ) || 
							"extension".equals( name ) ||
								"restriction".equals( name ) ) ||
									SchemaHelper.isFacet( element ) ||
										"selector".equals( name ) ||	// Key/keyref
											"field".equals( name );
	}
	
	@Override
	public boolean isOpened() {
		if ( getData( "open" ) == null ) {
			setData( "open", Boolean.TRUE );
			return true;
		} else
			return ( Boolean )getData( "open" );
	}

	@Override
	public void setOpened( boolean value) {
		setData( "open", value );		
		init( source, resolveRef );	// Reset content
	}

	private boolean selected = false;
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected( boolean selected ) {
		this.selected = selected;
		getView().invalidateBuffer();
	}
	
	@Override
	public Object getData( String name ) {
		return source.getUserData( name );
	}

	@Override
	public void setData(String name, Object value) {
		source.setUserData( name, value, null );
	}
	
	private Element getRoot() {
		
		return source.getOwnerDocument().getDocumentElement();
		
	}
	
	private Element searchForTypeRef( Element root, String source, boolean refMode, String typeName ) {
		
		String ns = null;
		String localTypeName = typeName;
		int i = 0;
		if ( ( i = typeName.indexOf( ":" ) ) > -1 ) {
			String prefix = typeName.substring( 0, i );
			localTypeName = typeName.substring( i + 1 );
			// Search for the namespace
			ns = root.getAttribute( "xmlns:" + prefix );
		}
				
		String currentNamespace = root.getAttribute( "targetNamespace" );
		
		if ( ns == null )
			ns = currentNamespace;
		
		if ( ns == null )
			ns = "";
		
		NodeList nl = root.getChildNodes();
		
		if ( ns.equals( currentNamespace ) ) {		
			for ( i = 0; i < nl.getLength(); i++ ) {
				if ( nl.item( i ) instanceof Element ) {
					Element global = ( Element )nl.item( i );
					if ( refMode ) {
						if ( source.equals( SchemaHelper.getElementName( global ) ) ) {		
							if ( localTypeName.equals( global.getAttribute( "name" ) ) ) {
								return global;
							}
						}
					} else {
						String tag = SchemaHelper.getElementName( global );
						if ( "complexType".equals( tag ) || "simpleType".equals( tag ) ) {					
							if ( localTypeName.equals( global.getAttribute( "name" ) ) ) {
								return global;
							}
						}
					}
				}
			}
		}
		
		// Check imported schema
		
		for ( i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element global = ( Element )nl.item( i );
				if ( SchemaHelper.isImportation( global ) ) {
					Element newRoot = ( Element )global.getUserData( "parsed" );
					String externalNs = ( String )global.getUserData( "ns" );

					if ( newRoot != null ) {
						
						if ( ns.equals( externalNs ) ) {
						
							// Remove prefix from source namespace because we use global definition in the imported namespace
							
							int j = typeName.indexOf( ":" );
							if ( j > -1 ) {
								typeName = typeName.substring( j + 1 );
							}
							
							Element resTmp = searchForTypeRef( newRoot, source, refMode, typeName );
							if ( resTmp != null )
								return resTmp;
						
						}
					}
				}
			}				
		}

		return null;
	}
	
	// For external type or element ref
	
	private boolean reference = false;
	
	public boolean isReference() {
		return reference || ( parent != null && parent.isReference() );
	}
	
	private String referenceName = null;
	
	public String getReferenceName() {
		return referenceName;
	}
	
	public void init( Element node ) {
		init( node, true );
	}
	
	public void init( Element node, boolean resolveRef ) {
		
		if ( SchemaHelper.isMarked( node ) )
			return;

		if ( isReference() ) {
			if ( SchemaHelper.isMarked( this.source ) )
				return;
		}
		
		this.source = node;
		
		SchemaHelper.mark( node );

		try {

			children = new ArrayList<XSDNode>();
	
			String tagName = SchemaHelper.getElementName( node );
	
			Element sourceNode = node;
			
			if ( node.hasAttribute( "substitutionGroup" ) && resolveRef ) {
	
				node = searchForTypeRef( getRoot(), tagName, true, node.getAttribute( "substitutionGroup" ) );
				if ( node == null )
					return;
				
			}
			
			if ( node.hasAttribute( "type" ) && resolveRef ) {
	
				// Search for the type definition
			
				String type = node.getAttribute( "type" );
	
				// not a primitive type
				if ( !SchemaHelper.isPrimitiveType( type ) ) {
					
					Element nodeTmp = node; 
					
					node = searchForTypeRef( getRoot(), tagName, false, type );
					
					if ( nodeTmp != node && node != null ) {
						// Find it as an external one, mustn't edit it
						
						// nodeTmp.setUserData( "disabled", true, null );
						// node.setUserData( "disabled", true, null );
						
						this.node = node;
						
						reference = true;
	
						if ( "simpleType".equals( node.getLocalName() ) ) {
							reference = false;
						} else {
							referenceName = nodeTmp.getAttribute( "type" );
						}
					}
					
				}
				
			} else
	
			if ( node.hasAttribute( "ref" ) && resolveRef ) {
				
				// Search for the ref definition
				
				Element nodeTmp = node; 
				
				node = searchForTypeRef( getRoot(), tagName, true, node.getAttribute( "ref" ) );
				
				if ( node != nodeTmp ) {
									
					if ( node != null ) {
						
						this.reference = true;
						
						this.node = node;
						
						if ( SchemaHelper.isMarked( node ) )
							return;
						
					}
					
				}
	
			}
	
			if ( this.node == null || node == null )	// Can't find ref or type
				return;
					
			NodeList nl = node.getChildNodes();
			for ( int i = 0; i < nl.getLength(); i++ ) {
				if ( nl.item( i ) instanceof Element ) {
					Element e = ( Element )nl.item( i );
										
					if ( !ignore( e ) ) {
						if ( isInvisible( e ) ) {
							// Keep only children for complexType...
							XSDNodeImpl invisible = new XSDNodeImpl( e );
							for ( int j = 0; j < invisible.getChildCount(); j++ ) {							
								addChild( invisible.getChildAt( j ) );
							}
						} else {
							addChild( new XSDNodeImpl( e, resolveRef ) );
						}
					}
				}
			}
	
			// Test for key or keyref
	
			if ( node != sourceNode ) {
			
				nl = sourceNode.getChildNodes();
		
				for ( int i = 0; i < nl.getLength(); i++ ) {
		
					if ( nl.item( i ) instanceof Element ) {
						Element e = ( Element )nl.item( i );
						String name = SchemaHelper.getElementName( e );
						if ( "keyref".equals( name ) || 
								"key".equals( name ) ) {
							addChild( new XSDNodeImpl( e ) );
						}
					}
					
				}
				
			}
			
		} finally {
		
			SchemaHelper.unmark( this.source );
			SchemaHelper.unmark( node );
			
		}

		
	}

	@Override
	public boolean isMarked() {
		return SchemaHelper.isMarked( getDOM() );
	}
	
	private void addChild( XSDNode child ) {
		children.add( child );
		child.setParent( this );
	}

	@Override
	public boolean containsNode( Element node ) {
		for ( int i = 0;i < getChildCount(); i++ ) {
			XSDNode n = getChildAt( i );
			if ( n.getDOM() == node )
				return true;
		}
		if ( parent != null )
			return parent.containsNode( node );
		return false;
	}
	
	
	private XSDNode parent;
	
	@Override
	public void setParent(XSDNode parent) {
		this.parent = parent;
	}
	
	private XSDNodeView view = null;
	
	@Override
	public XSDNodeView getView() {
		if ( view == null ) {
			view = XSDNodeViewFactory.getInstance().getView( this ); 
		}
		return view;
	}
	
	@Override
	public XSDNode getChildAt(int index) {
		return children.get( index );
	}

	@Override
	public int getChildCount() {
		if ( children == null )
			return 0;
		return children.size();
	}

	@Override
	public XSDNode add( String nodeName ) {
		
		Document doc = source.getOwnerDocument();
		Element parentTmp = source;
		
		XSDNode parentNode = null;

		if ( SchemaHelper.isComplexTypeChild( nodeName ) ) {
			if ( !"complexType".equals( parentTmp.getLocalName() ) ) {
				parentTmp = SchemaHelper.getFirstChild( parentTmp, "complexType" );
				if ( parentTmp == null ) {
					// Create it
					parentNode = add( "complexType" );
					if ( parentNode == null )
						return null;
					parentTmp = parentNode.getDOM();
				}
			}
		}
		
		Element newNode = SchemaHelper.createTag( source, nodeName );
		
		parentTmp.appendChild( newNode );
		
		SchemaHelper.unmark( getDOM() );
		invalidate();		

		if ( !isOpened() ) {
			setOpened( true );
		}
		
		return ( XSDNode )newNode.getUserData( "node" );
	}

	@Override
	public XSDNode insert( String nodeName ) {

		Document doc = source.getOwnerDocument();
		Element parentNode = ( Element )source.getParentNode();

		Element newNode = SchemaHelper.createTag( source, nodeName );

		parentNode.insertBefore(
			newNode,
			source
		);

		getParent().invalidate();
		
		return new XSDNodeImpl( newNode );
	}

	@Override
	public String toString() {
		if ( source.hasAttribute( "name" ) )
			return source.getAttribute( "name" );
		if ( source.hasAttribute( "ref" ) )
			return source.getAttribute( "ref" );
		
		return source.getNodeName();
	}

	@Override
	public boolean match( String name ) {
		String currentName = SchemaHelper.getElementName( getDOM() );
		if ( currentName != null ) {
			return currentName.equalsIgnoreCase( name );
		} else
			return false;
	}

	@Override
	public boolean isEnabled() {
		return getData( "disabled" ) == null;
	}
	
	public void dump() {
		System.out.println( "dump " + this.source.getNamespaceURI() + ":" + this.source.getAttribute( "name" ) );
		for ( int i = 0; i < getChildCount(); i++ )
			getChildAt( i ).dump();
	}
	
}
