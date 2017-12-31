package com.japisoft.framework.xml.parser.node;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.NameCollection;
import com.japisoft.framework.xml.parser.document.*;
import com.japisoft.framework.xml.parser.walker.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

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
public class FPNode implements TreeNode, MutableNode, ViewableNode {

	public final static int TEXT_NODE = 0;
	public final static int TAG_NODE = 1;
	public final static int COMMENT_NODE = 2;
	public final static int DOCUMENT_NODE = 3;
	public final static int ATTRIBUTE_NODE = 4;
	
	private int type;
	private Object content = null;
	
	private int startingLine;
	private int stoppingLine;
	public int startingOffset;
	public int stoppingOffset;

	/** @param type Node type : TEXT_NODE, TAG_NODE or COMMENT_NODE 
	 * @param content the tag content */
	public FPNode(int type, String content) {
		this.type = type;
		if ( type == TAG_NODE || type == ATTRIBUTE_NODE ) {
			if ( content != null )
				this.content = NameCollection.getInstance().getId( content );
		} else
			this.content = content;
	}

	public FPNode(int type, int idContent) {
		this.type = type;
	}

	/** Create a tag node */
	protected FPNode( String content ) {
		this( TAG_NODE, content );
	}

	/** @param type Node type : TEXT_NODE, TAG_NODE or COMMENT_NODE 
	 * @param content the tag content */
	public FPNode( FPNode parent, int type, String content ) {
		this( type, content );
		setFPParent( parent );
	}

	/**
	 * @return the current tag starting offset */
	public int getStartingOffset() {
		return startingOffset;
	}

	/**
	 * @return the current tag stopping offset
	 */
	public int getStoppingOffset() {
		return stoppingOffset;
	}

	/**
	 * @param i the current tag starting offset
	 */
	public void setStartingOffset(int i) {
		startingOffset = i;
	}

	/**
	 * @param i the current tag stopping offset
	 */
	public void setStoppingOffset(int i) {
		stoppingOffset = i;
	}

	/** Set the node line location */
	public void setStartingLine(int line) {
		this.startingLine = line;
	}

	/** @return the starting line location */
	public int getStartingLine() {
		return startingLine;
	}


	/** @return the stopping line */
	public int getStoppingLine() {
		return stoppingLine;
	}

	/** @param i reset the stopping line */
	public void setStoppingLine(int i) {
		stoppingLine = i;
	}

	// Owner document
	private Document doc;

	private String nameSpacePrefix;
	private String nameSpaceURI;
	private HashMap<String,String> htNameSpaceDeclaration;

	/** 
	 * Add a declaration for nameSpace
	 * @param prefix NameSpace prefix
	 * @param uri NameSpace URI */
	public void addNameSpaceDeclaration(String prefix, String uri) {
		if (htNameSpaceDeclaration == null)
			htNameSpaceDeclaration = new HashMap<String,String>();
		htNameSpaceDeclaration.put(prefix, uri);
	}

	/** Remove a nameSpace prefix declaration */
	public void removeNameSpaceDeclaration(String prefix) {
		if (htNameSpaceDeclaration != null)
			htNameSpaceDeclaration.remove(prefix);
	}

	/** Remove all the nameSpace declaration */
	public void removeAllNameSpaceDeclaration() {
		htNameSpaceDeclaration = null;
	}

	/** @return a nameSpace declaration URI for this prefix */
	public String getNameSpaceDeclarationURI(String prefix) {
		if (htNameSpaceDeclaration == null)
			return null;
		return (String) htNameSpaceDeclaration.get(prefix);
	}

	/** @return true if this prefix is delcared inside this node */
	public boolean isNamespaceDeclared( String prefix ) {
		if ( htNameSpaceDeclaration == null )
			return false;
		return htNameSpaceDeclaration.containsKey( prefix );
	}

	/** @return the list of namespaces declared */
	public Iterator<String> getNameSpaceDeclaration() {
		if ( htNameSpaceDeclaration == null )
			return null;
		return htNameSpaceDeclaration.keySet().iterator();
	}

	/** Set the namespace
	 * @param name Namespace prefix
	 * @param nameSpaceURL URI for this namespace */
	public void setNameSpace(String name, String nameSpaceURI) {
		this.nameSpacePrefix = name;
		this.nameSpaceURI = nameSpaceURI;
	}

	/** @return the namespace prefix */
	public String getNameSpacePrefix() {
		return nameSpacePrefix;
	}

	/** @return the namespace URI */
	public String getNameSpaceURI() {
		if ( nameSpaceURI == null )
			return defaultNamespace;
		return nameSpaceURI;
	}

	private String defaultNamespace;

	/** Reset the default namespace */
	public void setDefaultNamespace( String namespaceURI ) {
		this.defaultNamespace = namespaceURI;
		if ( nameSpacePrefix == null ) {
			this.nameSpaceURI = namespaceURI;
		}
	}

	/** @return the default namespace */
	public String getDefaultNamespace() {
		return defaultNamespace;		
	}

	/** Owner document for this node */
	public void setDocument(Document doc) {
		this.doc = doc;
	}

	/** @return the owner document */
	public Document getDocument() {
		if ( doc == null ) {
			doc = new Document();
			doc.setRoot( this );
		}
		return doc;
	}

	/** Clone the current node, include subchild if deep is <code>true</code> */
	public FPNode clone(boolean deep) {
		if (getDocument() == null) {
			if ( getFPParent() != null && getFPParent().getDocument() != null )
				setDocument( getFPParent().getDocument() );
			else
				throw new RuntimeException("Can't clone need a document");	
		}

		if (getDocument().getNodeFactory() == null)
			throw new RuntimeException("Can't clone need a NodeFactory inside the document");

		NodeFactory nf = getDocument().getNodeFactory();
		if (isText()) {
			return (FPNode) nf.getTextNode(getContent());
		} else if (isComment()) {
			return (FPNode) nf.getCommentNode(getContent());
		} else if (isTag()) {
			return (FPNode) cloneTag(deep);
		} else
			throw new RuntimeException(
				"Can't clone , unknown node type : " + this);

	}

	/** Clone tag node deeply */
	private FPNode cloneTag(boolean deep) {
		FPNode n =
			(FPNode) getDocument().getNodeFactory().getTagNode(
				getContent());
		if (attributes != null) {
			n.attributes = new ArrayList<FPNode>();
			for ( int i = 0; i < attributes.size(); i++ ) {
				FPNode attClone = new FPNode( FPNode.ATTRIBUTE_NODE, attributes.get( i ).getContent() );
				attClone.setNodeValue( attributes.get( i ).getNodeValue() );
				n.attributes.add( attClone );
			}
		}
		if (htNameSpaceDeclaration != null) {
			n.htNameSpaceDeclaration =
				(HashMap<String, String>) htNameSpaceDeclaration.clone();
		}
		if (nameSpacePrefix != null)
			n.nameSpacePrefix = nameSpacePrefix;
		if (nameSpaceURI != null)
			n.nameSpaceURI = nameSpaceURI;
		if ( defaultNamespace != null )
			n.defaultNamespace = defaultNamespace;

		if (deep) {
			for (int i = 0; i < childCount(); i++) {
				n.appendChild(((FPNode) childAt(i)).clone(deep));
			}
		}
		return n;
	}

	private FPNode parent;

	/** reset the parent node */
	public void setFPParent(FPNode node) {
		this.parent = node;
	}

	/** @return the parent node or null for the root node */
	public FPNode getFPParent() {
		return parent;
	}

	/** @return true for the root node */
	public boolean isRoot() {
		return (parent == null);
	}

	/** Update the node content, for tag this is the tag name, for
	 * text this is the content, for comment this is the content */
	public void setContent(String content) {
		if ( type == FPNode.TEXT_NODE ) {
			this.content = content;			
		} else {
			this.content = NameCollection.getInstance().getId( content );
		}
	}

	/** @return the node content */
	public String getContent() {
		if ( content instanceof String ) {
			return ( String )content;
		} else
			if ( content instanceof Integer ) {
				return NameCollection.getInstance().getName( ( Integer )content );
			}
		return null;
	}

	public boolean matchContent( String val ) {
		if ( content != null )
			return getContent().equals( val );
		else
			if ( val != null )
				return val.equals( getContent() );
		return false;
	}
	
	/** @return the content with a namespace prefix if needed */
	public String getQualifiedContent() {
		if ( nameSpacePrefix == null )
			return getContent();
		return nameSpacePrefix + ":" + getContent();
	}

	/** Update the node type, this is TEXT_NODE, TAG_NODE or COMMENT_NODE */
	public void setType(int type) {
		this.type = type;
	}

	/** @return the node type TEXT_NODE, TAG_NODE or COMMENT_NODE */
	public int getType() {
		return type;
	}

	/** @return true for text node */
	public boolean isText() {
		return type == TEXT_NODE;
	}

	/** @return true for tag node */
	public boolean isTag() {
		return type == TAG_NODE;
	}

	/** @return true for comment node */
	public boolean isComment() {
		return type == COMMENT_NODE;
	}

	public boolean isAttribute() {
		return type == ATTRIBUTE_NODE;
	}
	
	private FastVector children = null;

	/** Insert a childnode at the index location */
	public void insertChildNode(int index, FPNode node) {
		if (children == null) {
			appendChild(node);
		} else {
			node.setFPParent(this);
			children.insertElementAt(node, index);
		}
	}

	/** Replace this oldNode by this newOne */
	public void replaceChildNode( FPNode oldNode, FPNode newNode ) {
		if ( children != null ) {
			int i = children.indexOf( oldNode );
			if ( i > -1 ) {
				children.insertElementAt( newNode, i );
				children.remove( oldNode );
			}
		}
	}

	public FPNode insertFirstChild( FPNode node ) {
		if (children == null)
			children = new FastVector(10);
		node.setFPParent(this);
		children.insertElementAt( node, 0 );
		return node;
	}
	
	/** Insert a new node */
	public FPNode appendChild(FPNode node) {
		if (isText() && node.isText()) {
			setContent(getContent() + node.getContent());
			return node;
		}
		if (children == null)
			children = new FastVector(10);
		node.setFPParent(this);
		children.add(node);
		return node;
	}

	/** Remove all the children */
	public void removeChildrenNodes() {
		children = null;
	}

	/** Remove the following node */
	public void removeChildNode(FPNode node) {
		if (children == null)
			return;
		children.remove(node);
		node.setFPParent( null );
		if (children.size() == 0)
			children = null;
	}
		
	public void removeChildNodeAt( int index ) {
		if ( children == null )
			return;
		children.removeElementAt( index );
	}

	/** @return the child node index. -1 for unknown child */
	public int childNodeIndex(FPNode node) {
		if (children == null)
			return -1;
		return children.indexOf(node);
	}

	/** @return the children count */
	public int childCount() {
		if (children == null)
			return 0;
		return children.size();
	}

	/** Get all child */
	public Enumeration getAllChild() {
		if (children == null)
			return null;
		return children.elements();
	}

	/** @return the children at the index position starting from 0 */
	public FPNode childAt(int index) {
		if (children == null)
			return null;
		return (FPNode) children.get(index);
	}

	/** @return the first child name with the following name 
	 */
	public FPNode getFirstChildByName( String name ) {
		for ( int i = 0; i < childCount(); i++ ) {
			if ( childAt( i ).matchContent( name) )
				return childAt( i );
		}
		return null;
	}
	
	public boolean hasTextChildNode() {
		for ( int i = 0; i < childCount(); i++ ) {
			if ( childAt( i ).isText() )
				return true;
		}
		return false;
	}

	/** @return true for leaf node */
	public boolean isLeaf() {
		return children == null;
	}

	private List<FPNode> attributes;
	
	public List<FPNode> getViewAttributeNodes() {
		return attributes;
	}

	/** @return the attribute for this index */
	public String getViewAttributeAt( int index ) {
		if ( attributes == null )
			return null;
		return attributes.get( index ).getContent();
	}

	/** @return the number of attribute */
	public int getViewAttributeCount() {
		if ( attributes == null )
			return 0;
		return attributes.size();
	}

	/** @return the attribute value for the name or null */
	public String getViewAttribute(String name) {
		return getAttribute(name);
	}
	
	private FPNode getAttributeNode( String qname ) {
		if ( attributes != null )
		for ( int i = 0; i < attributes.size(); i++ ) {
			if ( attributes.get( i ).matchContent( qname ) )
				return attributes.get( i );
		}
		return null;
	}
	
	/** Set the attribute value. If value is null the attribute is removed 
	 * @param qname Qualified name (namespace prefix + local name )
	 * @param value value for this attribute */
	public void setAttribute(String qname, String value) {
		if ( attributes == null ) {
			attributes = new ArrayList<FPNode>();
		}
		FPNode attNode = getAttributeNode( qname );
		
		// AB 10/06/16
		if ( value == null ) {
		
			if ( attNode != null ) {
				attributes.remove( attNode );
			}
			
		} else {
			if ( attNode == null ) {
				attNode = new FPNode( FPNode.ATTRIBUTE_NODE, qname );
				attributes.add( attNode );
			}
			attNode.setNodeValue( value );
		}
	}
	
	public FPNode att( String name, String value ) {
		setAttribute( name, value );
		return this;
	}

	/** Reset the attribute value */
	public void setAttribute( String qname, int value ) {
		setAttribute( qname, "" + value );
	}

	/** Reset the attribute value */
	public void setAttribute( String qname, double value ) {
		setAttribute( qname, "" + value );
	}

	/** Reset the attribute value */
	public void setAttribute( String qname, boolean value ) {
		setAttribute( qname, "" + value );
	}

	/** @return an attribute value. defaultValue is returned for unknown attribute name */
	public String getAttribute(String qname, String defaultValue) {
		String val = getAttribute(qname);
		if (val == null)
			return defaultValue;
		return val;
	}
	
	/** @return the first attribute value */
	public String getFirstAttributeValue() {
		if ( attributes != null && attributes.size() > 0 )
			return attributes.get( 0 ).getContent();
		return null;
	}

	/** @return all nodes matching the following name */
	public Enumeration getNodeByName(String name, boolean deep) {
		TreeWalker tw = new TreeWalker(this);
		return tw.getTagNodeByName(name, deep);
	}

	/** 
	 * Sample of criteria :
	 * <code>new OrCriteria( new NodeNameCriteria( "aa" ), new NodeNameCriteria( "bb" ) )</code> for
	 * returning 'aa' or 'bb' node.
	 @return all nodes matching this criteria */
	public Enumeration getNodeByCriteria(
		ValidCriteria criteria,
		boolean deep) {
		TreeWalker tw = new TreeWalker(this);
		return tw.getNodeByCriteria(criteria, deep);
	}

	/** @return an attribute value. null is returned for unknown attribute name */
	public String getAttribute(String qname) {
		if (attributes == null)
			return null;
		FPNode node = getAttributeNode( qname );
		if ( node == null )
			return null;
		return node.getNodeValue();
	}

	/** @return true is the name attribute exists */
	public boolean hasAttribute(String qname) {
		if (attributes == null)
			return false;
		return (getAttribute(qname) != null);
	}

	public String toString() {
		return getContent();
	}

	// Mutable

	/** Reset the node content */
	public void setNodeContent(String content) {
		setContent(content);
	}

	/** @return the node content */
	public String getNodeContent() {
		return getContent();
	}

	private String value;
	
	public void setNodeValue( String value ) {
		this.value = value;
	}
	
	public String getNodeValue() {
		return value;
	}

	/** Reset the node parent */
	public void setNodeParent(MutableNode node) {
		setFPParent((FPNode) node);
	}

	/** Reset the node attribute */
	public void setNodeAttribute(String name, String value) {
		setAttribute(name, value);
	}

	/** Reset the children */
	public void addNode(MutableNode node) {
		appendChild((FPNode) node);
	}

	// -- ViewableNode ---


	/** @return the content of the node */
	public String getViewContent() {
		return getContent();
	}

	/** @return true is the node is a leaf */
	public boolean isViewLeaf() {
		return getViewChildCount() == 0;
	}

	/** @return the child count */
	public int getViewChildCount() {
		return childCount();
	}

	/** @return a child starting from 0 */
	public ViewableNode getViewChildAt(int i) {
		return (ViewableNode) childAt(i);
	}

	/** @return true for text node */
	public boolean isViewText() {
		return isText();
	}

	/** @return true for comment node */
	public boolean isViewComment() {
		return isComment();
	}

	///////////////////// XPATH //////////////////////

	private FPNode resolveXPath(String tag) {
		
		int occ = 0;
		int i = tag.indexOf("[");
		if (i > -1) {
			int j = tag.indexOf("]");
			if (j > i) {
				try {
					occ = Integer.parseInt(tag.substring((i + 1), j)) - 1;
				} catch (NumberFormatException exc) {
				}
			}
			tag = tag.substring(0, i);
		}

		for (int j = 0; j < childCount(); j++) {
			FPNode n = childAt(j);
			if ("text()".equals(tag) && n.isText())
				return n;

			if (n.isTag()) {
				if (tag.equals(n.getNodeContent())) {
					if (occ == 0)
						return n;
					occ--;
				}
			}
		}
		return null;
	}

	/** 
	 * This feature is limited to a subset of the XPath set : /tag1[ location ]/tag2...
	@return a node for a single xpath location. 'resolveAlways' will help to
	 * return the best location even if a sub path is not available */
	public FPNode getNodeForXPathLocation(
		String xpath,
		boolean resolveAlways) {

		if ( xpath == null )
			throw new RuntimeException( "Invalid xpath expression null ?" );

		StringBuffer sb = new StringBuffer();
		boolean rootMode = false;

		for (int i = 0; i < xpath.length(); i++) {
			if ( xpath.charAt(i) == '/' ) {
				if ( i == 0 ) {
					rootMode = true;
					continue;
				}
				String tag = sb.toString();
				sb = new StringBuffer();
				FPNode n = null;
				
				if ( !rootMode )
					n = resolveXPath( tag );
				else {
					n = ( FPNode )getDocument().getRoot();
					int j = tag.indexOf( '[' );
					if ( j > -1 )
						tag = tag.substring( 0, j );
					if ( !n.getContent().equals( tag ) )
						n = null;
					rootMode = false;
				}

				if (n == null) {
					if (resolveAlways)
						return n;
					else
						return this;
				}

				if ((i + 1) < xpath.length()) {
					return n.getNodeForXPathLocation(
						xpath.substring(i + 1),
						resolveAlways);
				} else
					return n;
			} else
				sb.append(xpath.charAt(i));
		}

		if (sb.length() > 0) {
			FPNode n = resolveXPath(sb.toString());
			if (n == null && resolveAlways)
				return this;
			return n;
		}

		// The worse case
		if (resolveAlways)
			return this;
		return null;
	}

	/** @return the XPath location for the current node */
	public String getXPathLocation() {
		if ( isText() ) {
			if ( getFPParent() == null )
				return "text()";
			return getFPParent().getXPathLocation() + "/text()";
		}
		FPNode p = getFPParent();

		if (p == null)
			return "/" + getContent();
		else
			return p.getXPathLocation(this);
	}

	private String xpathLocationCache = null;

	/** @return the current XPath location caching the last value. This is 
	 * only useful for readonly document */
	public String getCachedXPathLocation() {
		if ( xpathLocationCache != null )
			return xpathLocationCache;
		return ( xpathLocationCache = getXPathLocation() );		
	}

	/** @return the XPath location for the tied child node */
	public String getXPathLocation(FPNode node) {
		String name = node.getNodeContent();
		int loc = 1;

		for (int i = 0; i < childCount(); i++) {
			FPNode child = childAt(i);

			if (child == node) {
				return getXPathLocation() + "/" + name + "[" + loc + "]";
			}
			if (child.isTag() && child.getNodeContent().equals(name))
				loc++;
		}

		return getFPParent().getXPathLocation();
	}

	public boolean equals( FPNode node ) {
		if ( node.getType() == node.getType() ) {
			if ( node.getType() == FPNode.TEXT_NODE ) {
				return getContent().equals( node.getContent() );
			} else
			if ( node.getType() == FPNode.TAG_NODE ) {
				if ( getContent().equals( node.getContent() ) ) {
					// Check for attributes
					if ( attributes == null ) {
						return node.attributes == null;
					} else {
						if ( node.attributes == null )
							return false;
						for ( int i = 0; i < getViewAttributeCount(); i++ ) {
							String a = getViewAttributeAt( i );
							String v = getAttribute( a );
							if ( ( node.getAttribute( a ) == null || !node.getAttribute( a ).equals( v ) ) )
								return false;
						}
						return true;
					}
				} else
					return false;
			} else
				return super.equals( node );
		} else
			return false;
	}

	///////////// for XSL Usage

	/** @return a raw text view of this XML node */
	public String getRawXML() {
		return getRawXML( 0 );
	}

	public String getRawXML( int indent ) {
		StringBuffer sb = new StringBuffer();
		resetRawXML( sb, indent );
		return sb.toString();
	}

	private boolean preservedWhitespace = false;
	
	public FPNode setPreservedWhitespace( boolean p ) {
		this.preservedWhitespace = true;
		return this;
	}

	private void resetRawXML( StringBuffer sb, int indent ) {
		if ( !preservedWhitespace ) {
			for ( int i = 0; i < indent; i++ ) {
				sb.append( "\t" );
			}			
		}
		
		if ( isTag() ) {
			sb.append( openDeclaration() );
			if ( indent > 0 && childCount() > 0 && !preservedWhitespace ) {
				sb.append( "\n" );
			}
			for ( int i = 0; i < childCount(); i++ ) {
				int nextIndent = indent;
				if  ( indent > 0 )
					nextIndent = nextIndent + 1;
				childAt( i ).resetRawXML( sb, nextIndent );
			}
			if ( childCount() > 0 && !preservedWhitespace )
				for ( int i = 0; i < indent; i++ ) {
					sb.append( "\t" );
				}
			sb.append( closeDeclaration() );
		} else
		if ( isText() ) {
			sb.append( getNodeContent() );
		}		
		if ( indent > 0 && !preservedWhitespace )
			sb.append( "\n" );			
	} 

	/** @return a string with the opening value. This is only for tag type */	
	public String openDeclaration() {
		if ( getNodeContent() == null )
			return "";

		StringBuffer sb = new StringBuffer();
		sb.append( "<" );

		if ( getNameSpacePrefix() != null )
			sb.append( getNameSpacePrefix() ).append( ":" );

		sb.append( getNodeContent() );

		if ( getDefaultNamespace() != null )
			sb.append( " xmlns=\"" ).append( getDefaultNamespace() ).append( "\"" );

		if ( htNameSpaceDeclaration != null ) {
			Iterator<String> enume = htNameSpaceDeclaration.keySet().iterator();
			while ( enume.hasNext() ) {
				String prefix = enume.next();
				String uri = ( String )htNameSpaceDeclaration.get( prefix );
				sb.append( " xmlns:" ).append( prefix ).append( "=\"").append( uri ).append( "\"" ); 			
			}
		}

		if ( attributes != null ) {
			for ( int i = 0; i < attributes.size(); i++ ) {
				String att = ( String )attributes.get( i ).getContent();
				String val = ( String )attributes.get( i ).getNodeValue();
				sb.append( " " );
				sb.append( att );
				sb.append( "=\"" );
				sb.append( mapAttributeValueEntities( val ) );
				sb.append( "\"" );
			}
		}
		if ( isLeaf() && isAutoClose() )
				sb.append( "/");
		sb.append( ">");
		return sb.toString();
	}

	private String mapAttributeValueEntities( String value ) {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < value.length(); i++ ) {
			char c = value.charAt( i );
			if ( c == '<' )
				sb.append( "&lt;" );
			else
			if ( c == '>' )
				sb.append( "&gt;" );
			else
			if ( c == '\"' )
				sb.append( "&quot;" );
			else
			//if ( c == '\'' )
			//	sb.append( "&apos;" );
			//else
			if ( c == '&' && ! ( i + 1 < value.length() && value.charAt( i + 1 ) == '#' ) )
				sb.append( "&amp;" );
			else
			if ( c == '\n' )
				sb.append( "&#10;" );
			else
			sb.append( c );
		}
		return sb.toString();
	}

	/** @return a string with the closing value. This is only for tag type */
	public String closeDeclaration() {
		if ( getNodeContent() == null )
			return "";
		if ( isLeaf() && isAutoClose() )
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append( "</" ).append( getQualifiedContent() ).append( ">" );
		return sb.toString(); 
	}

	private boolean autoClose = false;

	/** Particular way to know the tag declaration is in the &lt;A/&gt; form rather than &lt;A&gt;&lt;/A&gt; */
	public void setAutoClose( boolean closedLeaf ) {
		this.autoClose = closedLeaf;
	}
	
	/** Particular way to know the tag declaration is in the &lt;A/&gt; form rather than &lt;A&gt;&lt;/A&gt; */
	public boolean isAutoClose() {
		return autoClose;
	}

	private Object applicationObject;

	/** Store an application object in this node */
	public void setApplicationObject( Object obj ) {
		this.applicationObject = obj;
	}

	/** @return the current application object */
	public Object getApplicationObject() {
		return applicationObject;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// TreeNode support

	public Enumeration children() {
		return getAllChild();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt( int childIndex ) {
		try {
			return ( TreeNode ) childAt( childIndex );
		} catch ( ClassCastException exc ) {
			// ??
			return this;
		}
	}

	public int getChildCount() {
		return childCount();
	}

	public int getIndex(TreeNode node) {
		return childNodeIndex((FPNode) node);
	}

	public TreeNode getParent() {
		return (TreeNode) getFPParent();
	}
	
	public String errorMessage = null;
	
	

}
