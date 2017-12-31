// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath.node;

import com.japisoft.xpath.*;

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
public class Node extends Expr {

	public Node() {
		super();
	}

	private boolean attributeMode;
	private String namespacePrefix;
	private String name;

	/** Node name
	 * @param name The node name
	 * @param qname specified if the name is qualified or node. A qualified node
	 * should have namespace prefix and a namespace declaration from the
	 * expression context
	 * */
	public void setName(String name, String namespacePrefix) {
		this.name = name;
		this.namespacePrefix = namespacePrefix;
	}

	public void setAttributeMode( boolean attributeMode ) {
		this.attributeMode = attributeMode;
	}
	
	public boolean isAttributeMode() {
		return attributeMode;
	}
	
	/** @return the node name */
	public String getName() {
		if (name == null) {
			name = "*";
		}
		return name;
	}

	private String axis;

	/** Axis name : 'ancestor'  'ancestor-or-self'  'attribute'  'child'  'descendant'  'descendant-or-self'  'following'  'following-sibling'  'namespace'  'parent'  'preceding'  'preceding-sibling'  'self' */
	public void setAxis(String axis) {
		this.axis = axis;
	}

	public boolean hasAxis() {
		return !(axis == null);
	}

	public String getAxis() {
		if (axis == null) {
			axis = "child";
		}
		return axis;
	}

	private String nodeType;

	public void setType(String type) {
		this.nodeType = type;
	}

	public String getType() {
		if (nodeType == null) {
			nodeType = "node";
		}
		return nodeType;
	}

	public void addNode(AbstractNode node) {
		if ( node instanceof Node ) {
			if ( getNodeCount() > 0 && getNodeAt( 0 ) instanceof Node ) {
				getNodeAt( 0 ).addNode( node );
			} else
				if ( getNodeCount() > 1 && getNodeAt( 1 ) instanceof Node ) {
					getNodeAt( 1 ).addNode( node );
				} else
				super.addNode( node );
		} else
				super.addNode( node );
	}

	///////////////////////////////////////////////////////

	private int type;

	private boolean fromRoot;
	/** Set if this node starts from the document root */
	public void setFromRoot(boolean from) {
		fromRoot = from;
	}

	/** @return true if this node starts from the document root */
	public boolean isFromRoot() {
		return fromRoot;
	}

	public String toString() {
		return name;
	}

	/** @return the next sub-node from this node */
	public Node getNextNodeLocation() {
		for (int i = 0; i < getNodeCount(); i++) {
			AbstractNode n = getNodeAt(i);
			if (n instanceof Node) {
				return (Node) n;
			}
		}
		return null;
	}

	/** @return all predicates for this node */
	public Predicate[] getPredicates() {
		FastVector vp = null;
		for (int i = 0; i < getNodeCount(); i++) {
			AbstractNode n = getNodeAt(i);
			if (n instanceof Predicate) {
				if (vp == null) {
					vp = new FastVector();
				}
				vp.addElement(n);
			}
		}
		if (vp == null) {
			return null;
		}
		Predicate[] ps = new Predicate[vp.size()];
		for (int i = 0; i < vp.size(); i++) {
			ps[i] = (Predicate) vp.elementAt(i);
		}
		return ps;
	}

	//////////////////////////////////////////////////

	public Object eval(XPathContext context) {
		// Check for prefix declaration
		if (namespacePrefix != null)
			if (!context.hasNamespaceDeclaration(namespacePrefix))
				throw new RuntimeException(
					"No namespace declaration for the prefix "
						+ namespacePrefix);

		Navigator navigator = context.getNavigator();

		if (isFromRoot()) {

			context.setContextNode(
				context.getNavigator().getDocumentRoot(
					context.getFirstNodeFromContext() ) );

			if ( context.getContextSize() == 0 )
					throw new RuntimeException( "No root node" );
			
			if (name == null && nodeType == null)
				return context.getContextNodeSet();

		}

		// Find a Predicate
		Predicate[] ps = getPredicates();
		NodeSet ns = context.getContextNodeSet();
		NodeSet r = new NodeSet(); // Result

		// Test all NodeSet nodes
		int min = 0;
		int max = context.getContextSize();
		if (context.isPredicateMode()) {
			min = context.getContextPosition();
			max = min + 1;
		}

		if (ns.size() > 0) {

			String namespaceURI = null;
			if (namespacePrefix != null)
				namespaceURI = context.getNamespaceURI(namespacePrefix);

			for (int i = min; i < max; i++) {
				Object node = ns.elementAt(i);
				NodeSet nsn = null;
				if ( context.hasCache() ) {
					nsn = context.getNavigatorCacheValue( node, getAxis(), getType(), getName(), namespaceURI );
				}
				
				if ( nsn == null ) {
					nsn =
						navigator.getNodes(
							node,
							getAxis(),
							getType(),
							getName(),
							namespaceURI,
							attributeMode );
					if ( nsn != null ) {
						context.setNavigatorCacheValue( node, getAxis(), getType(), getName(), namespaceURI, nsn );
					}
				}
				if (nsn != null) {
					context.setContextNodeSet(nsn);
					
					// Predicate case
					if ( ( ps != null ) && ( nsn.size() > 0 ) ) {
						
						for ( int j = 0; j < nsn.size(); j++ ) {
							
							XPathContext subContext = context.clone( nsn.get( j ) );
							subContext.setContextPosition( j );
							boolean allOk = true;
							
							// Test all predicates
							for ( int k = 0; k < ps.length; k++ ) {
								Predicate p = ( Predicate )ps[ k ];
								Boolean ok = ( Boolean )p.eval( subContext );
								if ( !ok.booleanValue() ) {
									allOk = false;
									break;
								}
							}
							
							if ( allOk )
								r.addNode( nsn.get( j ) );
						}						
						
					} else {
						for (int j = 0; j < nsn.size(); j++) {
							Object node1 = nsn.elementAt(j);
							context.setContextPosition(j);
							r.addNode(node1);
						}
					}
				}
			}
		}

		context.setContextNodeSet(r);

		if (r.size() > 0) {
			// Try subNodes
			Node n = getNextNodeLocation();
			if (n == null) {
				return r;
			}
			return n.eval(context);
		} else {
			return r;
		}
	}
}

// Node ends here
