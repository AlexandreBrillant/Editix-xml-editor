package com.japisoft.editix.action.dtdschema.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import com.japisoft.framework.xml.parser.node.FPNode;

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
public class MetaNode extends MetaObject implements TreeNode {

	private List<MetaNode> children = null;
	private boolean acceptText = false;
	private Vector attributes = null;
	private String name = null;
	private String prefix = null;
	private String namespace = null;
	private Map<String,MetaNode> metaNodeLibrary = null;
	private HashMap occurences;
	
	boolean built = false;
	MetaNode parentNode = null;

	public MetaNode(
			FPNode node, 
			Map<String,MetaNode> metaNodeLibrary ) {
		this.name = node.getNodeContent();
		this.prefix = node.getNameSpacePrefix();
		this.namespace = node.getNameSpaceURI();
		this.metaNodeLibrary = metaNodeLibrary;
	}

	public String getName() { return name; }
	public String getPrefix() { return prefix; }
	public String getNamespace() { return namespace; }
	public boolean acceptText() { return acceptText; }
	public List getNodeCollection() { return new ArrayList( metaNodeLibrary.values() ); }

	public List getChildren() {
		if ( children == null )
			return new ArrayList<MetaNode>();
		return children;
	}

	public Vector getAttributes() {
		return attributes;
	}

	public boolean hasAttributes() {
		return attributes != null;
	}

	public boolean hasMultipleOccurence( MetaNode child ) {
		if ( occurences == null )
			return false;
		return occurences.containsKey( child );
	}

	private Map<MetaNode,Boolean> missings = null;

	public boolean canBeMissing( MetaNode child ) {
		if ( missings == null ) {
			return false;
		}
		return missings.containsKey( child );
	}

	public boolean equals( Object obj ) {
		if ( obj instanceof MetaNode ) {
			MetaNode mn = ( MetaNode ) obj;
			if ( mn.name.equals( name ) )
				return true;
			return false;
		} else
			return super.equals( obj );
	}

	private boolean isWhitespace( String value ) {
		boolean ok = true;
		for ( int i = 0; i < value.length(); i++ ) {
			if ( !Character.isWhitespace( value.charAt( i ) ) ) {
				ok = false;
				break;
			}
		}
		return ok;
	}
	
	private MetaNode getChild( String name ) {
		if ( children == null )
			return null;
		for ( MetaNode mn : children ) {
			if ( name.equals( mn.getName() ) ) {
				// Return it from the library for having one instance

				if ( metaNodeLibrary.containsKey( mn.getName() ) )
					return metaNodeLibrary.get( mn.getName() );
				
				return mn;
			}
		}
		return null;
	}

	private MetaNode insertChildAfter( FPNode node, String previousName ) {

		if ( children == null )
			children = new ArrayList<MetaNode>();

		MetaNode res = getChild( node.getContent() );
		
		if ( res == null ) {

			res = metaNodeLibrary.get( node.getContent() );
						
			if ( res == null )	// Build the first one
				res = new MetaNode(node, metaNodeLibrary );			

			if ( previousName == null ) {
				
				if ( children.size() > 0 )
					children.add( 0, res );
				else
					children.add( res );

				res.parentNode = this;
				
			} else {
		
				MetaNode previousMn = getChild( previousName );
				int index = children.indexOf( previousMn );
				
				children.add( index + 1, res );

				res.parentNode = this;
				
			}
			
			metaNodeLibrary.put( node.getContent(), res );

		} else {
			
			if ( previousName != null ) {
				
				if ( node.matchContent( previousName ) ) {

					setMultipleOccurence( res, true );
					
				}
				
			}
			
		}

		return res;
		
	}

	public void setMultipleOccurence( MetaNode child, boolean multiple ) {

		if ( occurences == null ) {
			
			occurences = new HashMap<MetaNode,Boolean>();
			
		}

		if ( multiple )
			occurences.put( child, Boolean.TRUE );
		else
		    occurences.remove( child );
		
				
	}
	
	public void manageChildren( FPNode node ) {

		String previousNodeName = null;
		
		// Must get the right order !
		for ( int i = 0; i < node.getViewChildCount(); i++ ) {
			FPNode child = ( FPNode )node.getViewChildAt( i );

			if ( child.isText() ) {
				
				if ( !isWhitespace( child.getContent() ) ) {
					acceptText = true;
				}

			} else {
				
				String nodeName = child.getContent();
												
				MetaNode mn = insertChildAfter( child, previousNodeName );
				
				mn.manageAttributes( child );
				
				mn.manageChildren( child );
				
				previousNodeName = nodeName;
				
			}
		}
		
	}

	public void setMissing( MetaNode child, boolean missing ) {
		
		if ( missings == null ) {
			
			missings = new HashMap<MetaNode, Boolean>();
			
		}
	
		if ( missing )
			missings.put( child, Boolean.TRUE );
		else
			missings.remove( child );
				
	}
	
	public void manageMissing( FPNode node ) {
		
		for ( int i = 0; i < getChildCount(); i++ ) {
			
			MetaNode mn = ( MetaNode )getChildAt( i );
			
			// Search as a child
			
			String nameToFind = mn.name;
			
			boolean missing = true;
			
			for ( int j = 0; j < node.childCount(); j++ ) {

				if ( node.childAt( j ).matchContent( nameToFind ) ) {

					missing = false;
					break;
					
				}
				
			}
			
			if ( missing && mn.parentNode != null ) {
		
				setMissing( mn, true );

			}
			
			if ( !missing ) {
			
				for ( int j = 0; j < node.childCount(); j++ ) {
	
					if ( node.childAt( j ).matchContent( nameToFind ) ) {
						
						mn.manageMissing( node.childAt( j ) );
						
					}
					
				}
			
			}

		}
		
	}

	/** For removing the namespace declaration */
	private boolean isW3CFeature( String name, FPNode source ) {
		int i = 0;
		if ( ( i = name.indexOf( ":" ) ) > -1 ) {
			String pref = name.substring( 0, i );
			if ( source.getNameSpaceDeclarationURI( pref ) != null )
				return true;
		}
		return false;
	}

	public void manageAttributes( FPNode node ) {
				
		boolean firstOne = false;
		
		for (int i = 0; i < node.getViewAttributeCount(); i++) {
			String name = node.getViewAttributeAt( i );
			String value = node.getAttribute( name );

			if  ( !isW3CFeature( name, node ) ) {
				// Check if this attribute already exist
				if (attributes == null) {
					attributes = new Vector();
					firstOne = true;
				}
	
				MetaAttribute ma = new MetaAttribute( name, value );
				int _ = attributes.indexOf( ma );
				if ( _ == -1 ) {
					attributes.add( ma );
					if ( !firstOne )
						ma.setAlways( false );	// Wasn't here previously
				} else {
					// Update values
					ma = ( MetaAttribute ) attributes.get( i ); 
					ma.addValue( value );					
				}
			}
		}

		// Check for attributes that does'nt appear inside the node
		if (attributes != null) {
			for (int i = 0; i < attributes.size(); i++) {
				MetaAttribute ma = ( MetaAttribute ) attributes.get(i);
				String name = ma.getName();
				boolean found = false;

				for (int j = 0; j < node.getViewAttributeCount(); j++) {
					String name2 = node.getViewAttributeAt(j);
					if ( name2.equals( name ) ) {
						found = true;
						break;
					}
				}

				if ( !found )
					ma.setAlways( false );
			}
		}
	}

	///////////////////////////////////////////////////////
	
	public Enumeration children() {
		if ( children != null )
			return Collections.enumeration( children );
		return null;
	}

	public boolean getAllowsChildren() {
		return getChildCount() > 0;
	}

	public TreeNode getChildAt( int childIndex ) {
		return ( TreeNode )children.get( childIndex );
	}

	public int getChildCount() {
		if ( children == null )
			return 0;
		return children.size();
	}

	public int getIndex( TreeNode node ) {
		if ( children == null )
			return -1;
		return children.indexOf( node );
	}

	public TreeNode getParent() {
		return ( TreeNode )parentNode;
	}

	public boolean isLeaf() {
		return ( getChildCount() == 0 );
	}

	public String toString() { 
		if ( getName() == null )
			name = "Your document content";
		return getName(); 
	}

}
