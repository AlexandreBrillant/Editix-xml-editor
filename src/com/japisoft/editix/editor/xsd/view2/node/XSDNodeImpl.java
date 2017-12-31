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
public class XSDNodeImpl implements XSDNode {

	private Element node;
	private List<XSDNode> children;

	public XSDNodeImpl( Element node ) {
		this.node = node;
		node.setUserData( "node", this, null );
		// Avoid loop
		if ( !SchemaHelper.isMarked( node ) ) {
				SchemaHelper.mark( node );
			init( node );
		}
	}

	@Override
	public Element getDOM() {
		return node;
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
		
		Element parent = ( Element )node.getParentNode();		
		parent.removeChild( node );
		
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
		SchemaHelper.unmark( node );
		init( node );
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
		if ( node.getUserData( "open" ) == null ) {
			node.setUserData( "open", Boolean.TRUE, null );
			return true;
		} else
			return ( Boolean )node.getUserData( "open" );
	}

	@Override
	public void setOpened( boolean value) {
		node.setUserData( "open", value, null );
		SchemaHelper.unmark( node );
		init( node );	// Reset content
		SchemaHelper.dumpMark( node );
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
		return node.getUserData( name );
	}

	@Override
	public void setData(String name, Object value) {
		node.setUserData( name, value, null );
	}
	
	private Element getRoot() {
		
		return node.getOwnerDocument().getDocumentElement();
		
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
	
	public void init( Element node ) {
		
		children = new ArrayList<XSDNode>();

		String tagName = SchemaHelper.getElementName( node );

		Element sourceNode = node;
		
		if ( node.hasAttribute( "substitutionGroup" ) ) {

			node = searchForTypeRef( getRoot(), tagName, true, node.getAttribute( "substitutionGroup" ) );
			
		}
		
		if ( node.hasAttribute( "type" ) ) {

			// Search for the type definition
		
			String type = node.getAttribute( "type" );

			// not a primitive type
			if ( !SchemaHelper.isPrimitiveType( type ) ) {

				// Test for key or keyref

				node = searchForTypeRef( getRoot(), tagName, false, type );
				
			}
			
		} else

		if ( node.hasAttribute( "ref" ) ) {
			
			// Search for the ref definition
			
			node = searchForTypeRef( getRoot(), tagName, true, node.getAttribute( "ref" ) );

		}

		if ( node == null )	// Can't find ref or type
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
						addChild( new XSDNodeImpl( e ) );
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
		
		Document doc = node.getOwnerDocument();
		Element parentTmp = node;
		
		XSDNode parentNode = null;

		if ( SchemaHelper.isComplexTypeChild( nodeName ) ) {
			if ( !"complexType".equals( parentTmp.getLocalName() ) ) {
				parentTmp = SchemaHelper.getFirstChild( parentTmp, "complexType" );
				if ( parentTmp == null ) {
					// Create it
					parentNode = add( "complexType" );
					parentTmp = parentNode.getDOM();
				}
			}
		}

		Element newNode = SchemaHelper.createTag( node, nodeName );
		
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

		Document doc = node.getOwnerDocument();
		Element parentNode = ( Element )node.getParentNode();

		Element newNode = SchemaHelper.createTag( node, nodeName );

		parentNode.insertBefore(
			newNode,
			node
		);

		getParent().invalidate();
		
		return new XSDNodeImpl( newNode );
	}

	@Override
	public String toString() {
		if ( node.hasAttribute( "name" ) )
			return node.getAttribute( "name" );
		if ( node.hasAttribute( "ref" ) )
			return node.getAttribute( "ref" );
		
		return node.getNodeName();
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
	
}
