// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath;

import com.japisoft.xpath.function.Lib;
import java.util.Enumeration;
import java.util.Hashtable;

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
public final class XPathContext {

	private XPathKit kit;

	private XPathContext( XPathKit kit ) {
		this.kit = kit;
		setLibrary(kit.getLibrary());
		setNavigator(kit.getNavigator());
	}

	private Object refNode;

	/** Set this XPathContext with the following node. If no node are needed
	 * because the XPath resolution is made from the root, then you have to include
	 * the root node of your XML document.
	 * @param refNode refNode for resolving XPath. If none is available, it muse be the root node of the current XML tree
	 * @param kit The specific context resolver depending on Node type like FastParser lightweight node, JDOM, DOM... */
	public XPathContext(Object refNode, XPathKit kit) {
		this(kit);
		setContextNode(refNode);
		this.refNode = refNode;
	}

	/** Create a new context for this refNode and the current XPathKit */
	public XPathContext clone( Object refNode ) {
		XPathContext context = new XPathContext( refNode, kit );
		context.htNamespace = htNamespace;
		context.htVariable = htVariable;
		return context;
	}
	
	private boolean cache;

	/** Set the cache for the navigator : by default to true */	
	public void setCache( boolean cache ) {
		this.cache = cache;
	}

	/** Is cache available for the navigator : by default to true */
	public boolean hasCache() {
		return cache;
	}

	private Hashtable htCache = null;

 	/** Reset the cache content */
 	public void setNavigatorCacheContent( Hashtable content ) {
 		htCache = content;
 	}

	/** Get a previous navigator result or null if nothing is found */
	public NodeSet getNavigatorCacheValue( Object node, String axis, String type, String name, String namespace ) {
		if ( htCache == null )
			return null;
		StringBuffer sbKey = new StringBuffer();
		sbKey.append( node.hashCode() ).append( axis ).append( type ).append( name ).append( namespace );	
		return (NodeSet)htCache.get( sbKey.toString() );
	}

	/** Store a previous navigator result */
	public void setNavigatorCacheValue( Object node, String axis, String type, String name, String namespace, NodeSet res ) {
		if ( htCache == null )
				htCache = new Hashtable();
		StringBuffer sbKey = new StringBuffer();
		sbKey.append( node.hashCode() ).append( axis ).append( type ).append( name ).append( namespace );
		htCache.put( sbKey.toString(), res );
	}

	/////////////// SETTERS //////////////

	private NodeSet contextNodeSet;

	/** Reset the current NodeSet with this node */
	public void setContextNode(Object contextNode) {
		if ( contextNode != null )
			setContextNodeSet(new NodeSet(contextNode));
	}

	/** Reset the current NodeSet */
	public void setContextNodeSet(NodeSet n) {
		this.contextNodeSet = n;
		setContextPosition(0);
	}

	/** @return the current nodeSet */
	public NodeSet getContextNodeSet() {
		return contextNodeSet;
	}

	private int position;

	/** Reset the current context resolution position */
	public void setContextPosition(int position) {
		this.position = position;
	}

	/** @return the current context position */
	public int getContextPosition() {
		return position;
	}

	/** @return the current node from the current nodeSet and the current context location */
	public Object getNodeFromContext() {
		NodeSet ns = getContextNodeSet();
		if (ns.size() > getContextPosition())
			return ns.elementAt(getContextPosition());
		return null;
	}

	/** @return the first node from the curent nodeSet */
	public Object getFirstNodeFromContext() {
		NodeSet ns = getContextNodeSet();
		if ( ns == null || 
				ns.size() == 0 )
			return null;
		return ns.elementAt(0);
	}

	/** @return the current NodeSet size */
	public int getContextSize() {
		return getContextNodeSet().size();
	}

	private Hashtable htVariable;

	/** Reset a variable with this value. Primitives values must be wrapped
	 * with java.lang.Double or java.lang.Boolean... */
	public void setVariable(String name, Object value) {
		if (htVariable == null)
			htVariable = new Hashtable();
		htVariable.put(name, value);
	}

	/** Remove a variable */
	public void removeVariable(String name) {
		if (htVariable != null)
			htVariable.remove(name);
	}

	private Hashtable htNamespace;

	/** From the XPath */
	void setNamespaceDeclarationModel(Hashtable htModel) {
		this.htNamespace = htModel;
	}

	/** Add a namespace, linking a prefix to its namespace URI */
	public void addNamespace(String prefix, String uri) {
		if (htNamespace == null)
			htNamespace = new Hashtable();
		htNamespace.put(prefix, uri);
	}

	/** Remove a namespace declaration : a prefix */
	public void removeNamespace(String prefix) {
		if (htNamespace != null)
			htNamespace.remove(prefix);
	}

	/** Check if the prefix has namespace URI declaration with the addNamespace method */
	public boolean hasNamespaceDeclaration(String prefix) {
		if (htNamespace == null)
			return false;
		else
			return htNamespace.containsKey(prefix);
	}

	/////////////////////////////////////

	/** @return a variable value for its name. Primitives types are wrapped with standard
	 * java.lang.Double or java.lang.Boolean */
	public Object getVariable(String name) {
		if (htVariable == null)
			return null;
		return htVariable.get(name);
	}

	public Enumeration variablesEnum() {
		if (htVariable != null)
			return htVariable.keys();
		return null;
	}

	/** @return a namespace URI for this prefix. Such prefix appears in Qualified Name */
	public String getNamespaceURI(String prefix) {
		return (String) htNamespace.get(prefix);
	}

	public boolean hasVariable(String name) {
		if (htVariable == null)
			return false;
		return htVariable.containsKey(name);
	}

	void setVariableModel(Hashtable ht) {
		this.htVariable = ht;
	}

	////////////////////

	// To delegate from the XPathKit

	/** string-value is part of the node; for other types of node, the string-value is computed from the string-value of descendant nodes */
	public String getStringValue(Object node) {
		return kit.getStringValue(node);
	}

	/** Convert this NodeSet to a double number */
	public Double convertNodeSetToDouble( NodeSet ns ) {
		try {
			String r = "";
			for ( int i = 0; i < ns.size(); i++ ) {
				Object o = ns.get( i );
				r += getStringValue( o );
			}
		return new Double( r );
		} catch( NumberFormatException e ) {
			return new Double( Double.NaN );
		}
	}	

	/** @return a node matching the unique id */
	public Object getNodeForId(String id) {
		return kit.getNodeForId(refNode, id);
	}

	/** @return all nodes from the current document */
	public NodeSet getFullDocumentNodes(Object refNode) {
		return getNavigator().getNodes(
			refNode,
			"descendant",
			"node",
			"*",
			null,
			false );
	}

	/** @return the local name of the node */
	public String getLocalName(Object node) {
		return kit.getLocalName(node);
	}

	/** @return the namaespace URI for this node */
	public String getNamespaceURI(Object node) {
		return kit.getNamespaceURI(node);
	}

	/** @return the qualified node name */
	public String getName(Object node) {
		return kit.getName(node);
	}

	/** @return the language for the node */
	public String getLang(Object node) {
		return kit.getLang(node);
	}

	///////////////////////////////////////////////////////

	private Lib lib;

	/** Reset the library for XPath functions */
	public void setLibrary(Lib lib) {
		this.lib = lib;
	}

	/** @return the current library for XPath functions */
	public Lib getLibrary() {
		return lib;
	}

	private Navigator nav;

	/** Set a specific navigator for retreiving nodes */
	public void setNavigator(Navigator nav) {
		this.nav = nav;
	}

	/** @return the current navigator */
	public Navigator getNavigator() {
		return nav;
	}

	private boolean predicateMode = false;

	/** Particular case for predicate evaluation */
	public void setPredicateMode(boolean predicateMode) {
		this.predicateMode = predicateMode;
	}

	/** Particular case for predicate evaluation */
	public boolean isPredicateMode() {
		return predicateMode;
	}
}

// XPathContext ends here
