// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath.navigator;

import org.w3c.dom.*;
import com.japisoft.xpath.*;
import java.util.*;

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
public final class DOMNavigator implements Navigator {

	// Node type mapping

	public final static int NODE = Element.ELEMENT_NODE;
	public final static int TEXT = Element.TEXT_NODE;
	public final static int COMMENT = Element.COMMENT_NODE;
	public final static int ATTRIBUTE = Element.ATTRIBUTE_NODE;
	public final static int PROCESSING_INSTRUCTION =
		Element.PROCESSING_INSTRUCTION_NODE;

	static interface NodeMatcher {
		boolean matchNode(Node n, int type, String namespaceURI);
		boolean matchNode(Node n, String name, int type, String namespaceURI);
		boolean matchNode(Node n, String namespaceURI);
		boolean matchNode(Node n, String name, String namespaceURI);
	}

	// This class doesn't ignore lower/upper case
	final static class StandardNodeMatcher implements NodeMatcher {
		public boolean matchNode(Node n, int type, String namespaceURI) {
			return (
				( n.getNodeType() == type || type == -1 )
					&& ((namespaceURI == null)
						|| (namespaceURI.equals(n.getNamespaceURI()))));
		}

		public boolean matchNode(
			Node n,
			String name,
			int type,
			String namespaceURI) {
			boolean r1 =
				(n.getNodeType() == type
					&& ((namespaceURI == null)
						|| (namespaceURI.equals(n.getNamespaceURI()))));
			if (r1) {
				String v1 = n.getLocalName();
				if (v1 == null) {
					v1 = n.getNodeName();
				}
				return name.equals(v1);
			} else {
				return false;
			}
		}

		public boolean matchNode(Node n, String namespaceURI) {
			if (namespaceURI == null) {
				return true;
			}
			return namespaceURI.equals(n.getNamespaceURI());
		}

		public boolean matchNode(Node n, String name, String namespaceURI) {
			String v1 = n.getLocalName();
			if (v1 == null) {
				v1 = n.getNodeName();
			}
			if (namespaceURI == null) {
				return name.equals(v1);
			} else {
				return namespaceURI.equals(n.getNamespaceURI())
					&& name.equals(v1);
			}
		}
	}

	final static class IgnoreCaseNodeMatcher implements NodeMatcher {
		public boolean matchNode(Node n, int type, String namespaceURI) {
			return (
				n.getNodeType() == type
					&& ((namespaceURI == null)
						|| (namespaceURI.equalsIgnoreCase(n.getNamespaceURI()))));
		}

		public boolean matchNode(
			Node n,
			String name,
			int type,
			String namespaceURI) {
			boolean r1 =
				(n.getNodeType() == type
					&& ((namespaceURI == null)
						|| (namespaceURI.equalsIgnoreCase(n.getNamespaceURI()))));
			if (r1) {
				String v1 = n.getLocalName();
				if (v1 == null) {
					v1 = n.getNodeName();
				}
				return name.equalsIgnoreCase(v1);
			} else {
				return false;
			}
		}

		public boolean matchNode(Node n, String namespaceURI) {
			if (namespaceURI == null) {
				return true;
			}
			return namespaceURI.equalsIgnoreCase(n.getNamespaceURI());
		}

		public boolean matchNode(Node n, String name, String namespaceURI) {
			String v1 = n.getLocalName();
			if (v1 == null) {
				v1 = n.getNodeName();
			}
			if (namespaceURI == null) {
				return name.equalsIgnoreCase(v1);
			} else {
				return namespaceURI.equalsIgnoreCase(n.getNamespaceURI())
					&& name.equalsIgnoreCase(v1);
			}
		}
	}

	static NodeMatcher nodeMatcher = new StandardNodeMatcher();

	/** Ignore case when matching node */
	public void setIgnoreCaseMode(boolean ignoreCaseMode) {
		if (!ignoreCaseMode) {
			nodeMatcher = new StandardNodeMatcher();
		} else
			nodeMatcher = new IgnoreCaseNodeMatcher();
	}

	////////////////////////////////////////////////////////////////////////////////

	// Get all descendants

	private static void setAllDescendant(
		boolean self,
		boolean all,
		String name,
		int type,
		String namespaceURI,
		Node n,
		NodeSet ns) {

		if (self) {
			if (all && nodeMatcher.matchNode(n, type, namespaceURI)) {
				ns.add(n);
			} else if (nodeMatcher.matchNode(n, name, type, namespaceURI)) {
				ns.add(n);
			}
		}

		
		
		NodeList nl = n.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n2 = nl.item(i);
			if (all && (nodeMatcher.matchNode(n2, type, namespaceURI))) {
				ns.add(n2);
			} else if (nodeMatcher.matchNode(n2, name, type, namespaceURI)) {
				ns.add(n2);
			}
			if (n2.hasChildNodes()) {
				setAllDescendant(false, all, name, type, namespaceURI, n2, ns);
			}
		} 
		
/*
		Node n2 = n;
		do {
			n2 = n2.getNextSibling();
			if ( n2 != null ) {
				if (all && (nodeMatcher.matchNode(n2, type, namespaceURI))) {
					ns.add(n2);
				} else if (nodeMatcher.matchNode(n2, name, type, namespaceURI)) {
					ns.add(n2);
				}
			}
		} while ( n2 != null );
*/

	}

	//////////////////////////////

	private static Hashtable htResolver = new Hashtable();

	static {
		htResolver.put("ancestor", new AncestorResolver());
		htResolver.put("ancestor-or-self", new AncestorOrSelfResolver());
		htResolver.put("attribute", new AttributeResolver());
		htResolver.put("child", new ChildResolver());
		htResolver.put("descendant", new DescendantResolver());
		htResolver.put("descendant-or-self", new DescendantOrSelfResolver());
		htResolver.put("following", new FollowingResolver());
		htResolver.put("following-sibling", new FollowingSiblingResolver());
		htResolver.put("parent", new ParentResolver());
		htResolver.put("preceding", new PrecedingResolver());
		htResolver.put("preceding-sibling", new PrecedingSiblingResolver());
		htResolver.put("self", new SelfResolver());
	}

	static interface AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode );
	}

	// ancestor

	static class AncestorResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			Node ntmp = n.getParentNode();

			while (ntmp != null) {
				if (all && (nodeMatcher.matchNode(ntmp, -1, namespaceURI))) {
					ns.addNode(ntmp);
				} else {
					if (nodeMatcher
						.matchNode(ntmp, name, type, namespaceURI)) {
						ns.addNode(ntmp);
					}
				}
				ntmp = ntmp.getParentNode();
			}
		}
	}

	// ancestor-or-self

	static class AncestorOrSelfResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			Node ntmp = n;

			while ( !( ntmp instanceof Document ) ) {
				if (all && (nodeMatcher.matchNode(ntmp, -1, namespaceURI))) {
					ns.addNode( ntmp );
				} else {
					if (nodeMatcher
						.matchNode(ntmp, name, type, namespaceURI)) {
						ns.addNode(ntmp);
					}
				}
				ntmp = ntmp.getParentNode();
			}
		}
	}

	// attribute

	static class AttributeResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			NamedNodeMap enume = n.getAttributes();
			if (enume != null) {
				for (int i = 0; i < enume.getLength(); i++) {
					Node att = enume.item(i);
					if (all && nodeMatcher.matchNode(att, namespaceURI)) {
						ns.addNode(att);
					} else {
						if (nodeMatcher.matchNode(att, name, namespaceURI)) {
							ns.addNode(att);
						}
					}
				}
			}
		}
	}

	// child

	static class ChildResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			Node ntmp = n;
			NodeList nl = n.getChildNodes();

			for (int i = 0; i < nl.getLength(); i++) {
				ntmp = nl.item(i);
				if (all && (nodeMatcher.matchNode(ntmp, -1, namespaceURI))) {
					ns.addNode(ntmp);
				} else if (
					nodeMatcher.matchNode(ntmp, name, type, namespaceURI)) {
					ns.addNode(ntmp);
				}
			}
		}
	}

	// descendant

	static class DescendantResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			setAllDescendant(false, all, name, type, namespaceURI, n, ns);
		}
	}

	// descendant-or-self

	static class DescendantOrSelfResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			
			if ( attributeMode ) {
				
				// Test for this node
				
				NamedNodeMap map = n.getAttributes();
				
				if ( map != null && n.getNodeType() == Node.ELEMENT_NODE )
					for  (int i = 0; i < map.getLength(); i++ ) {
						Node attName = map.item( i );
						if ( all ) {
							ns.add( attName );
						} else
							if ( attName.getNodeName().equals( name ) ) 
								ns.add( attName );
					}
				
				// Test for the children

				NodeList nl = n.getChildNodes();

				for ( int i = 0; i < nl.getLength(); i++ ) {
					Node _= nl.item( i );

					if ( _.getNodeType() == Node.ELEMENT_NODE ) {
						resolve(
								ns,
								nl.item( i ),
								name,
								type,
								all,
								namespaceURI,
								attributeMode );
					}
				}

			} else
			setAllDescendant(true, all, name, type, namespaceURI, n, ns);
		}
	}

	private static int getIndexOfNode(Node p, Node n) {
		NodeList nl = p.getChildNodes();
		int index = 0;
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i) == n) {
				index = i;
				break;
			}
		}
		return index;
	}

	// following

	static class FollowingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			Node p = n.getParentNode();
			if (p != null) {
				int index = getIndexOfNode(p, n);
				NodeList nl = p.getChildNodes();
				for (int j = index + 1; j < nl.getLength(); j++) {
					setAllDescendant(
						true,
						all,
						name,
						type,
						namespaceURI,
						nl.item(j),
						ns);
				}
			}
		}
	}

	// following-sibling

	static class FollowingSiblingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			Node p = n.getParentNode();
			if (p != null) {
				int index = getIndexOfNode(p, n);
				NodeList nl = p.getChildNodes();
				for (int j = index + 1; j < nl.getLength(); j++) {
					Node ntmp = nl.item(j);
					if (all
						&& nodeMatcher.matchNode(ntmp, NODE, namespaceURI)) {
						ns.addNode(ntmp);
					} else if (
						nodeMatcher.matchNode(
							ntmp,
							name,
							type,
							namespaceURI)) {
						ns.addNode(ntmp);
					}
				}
			}
		}
	}

	// parent

	static class ParentResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			if (n.getParentNode() != null) {
				ns.add(n.getParentNode());
			}
		}
	}

	// preceding

	static class PrecedingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			Node p = n.getParentNode();
			if (p != null) {
				int i = getIndexOfNode(p, n);
				NodeList nl = p.getChildNodes();
				for (int j = 0; j < i; j++) {
					setAllDescendant(
						true,
						all,
						name,
						type,
						namespaceURI,
						nl.item(j),
						ns);
				}
			}
		}
	}

	// preceding-sibling

	static class PrecedingSiblingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			Node p = n.getParentNode();
			if (p != null) {
				int i = getIndexOfNode(p, n);
				NodeList nl = p.getChildNodes();
				for (int j = 0; j < i; j++) {
					Node ntmp = nl.item(j);
					if (all
						&& nodeMatcher.matchNode(ntmp, NODE, namespaceURI)) {
						ns.addNode(ntmp);
					} else if (
						nodeMatcher.matchNode(
							ntmp,
							name,
							type,
							namespaceURI)) {
						ns.addNode(ntmp);
					}
				}
			}
		}
	}

	// self

	static class SelfResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			Node n,
			String name,
			int type,
			boolean all,
			String namespaceURI,
			boolean attributeMode ) {
			if (all) {
				if (nodeMatcher.matchNode(n, type, namespaceURI)) {
					ns.addNode(n);
				}
			} else if (nodeMatcher.matchNode(n, name, type, namespaceURI)) {
				ns.addNode(n);
			}
		}
	}

	//////////////////////////////

	public Object getRoot(Object refNode) {
		Node n = (Node) refNode;
		if ((n.getParentNode() == null)
			|| (n.getParentNode() instanceof Document)) {
			return n;
		}
		return n.getOwnerDocument().getDocumentElement();
	}

	public Object getDocumentRoot(Object refNode) {
		if ( refNode == null )
			throw new RuntimeException( "No reference node" );
		return ((Node) refNode).getOwnerDocument();
	}

	public NodeSet getNodes(
		Object refNode,
		String axis,
		String nodeType,
		String name,
		String namespaceURI, 
		boolean attributeMode ) {

		Node n = (Node) refNode;
		NodeSet ns = new NodeSet();
		int type = NODE;

		if ( "text".equals( nodeType ) ) {
			type = TEXT;
		} else if ( "comment".equals( nodeType ) ) {
			type = COMMENT;
		} else if ( "processing-instruction".equals( nodeType ) ) {
			type = PROCESSING_INSTRUCTION;

		}
		boolean all = "*".equals(name);

		AxisResolver resolver = (AxisResolver) htResolver.get(axis);
		if (resolver == null) {
			throw new RuntimeException("Unknown axis " + axis);
		}

		resolver.resolve(ns, n, name, type, all, namespaceURI, attributeMode );
		return ns;
	}

}
