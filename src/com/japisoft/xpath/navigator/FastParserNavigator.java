// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath.navigator;

import java.util.*;

import com.japisoft.xpath.*;
import com.japisoft.framework.xml.parser.node.*;

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
public final class FastParserNavigator implements Navigator {

	// SimpleNode type mapping

	public final static int NODE = 1;
	public final static int TEXT = 0;
	public final static int COMMENT = 2;
	public final static int ATTRIBUTE = COMMENT + 1;
	public final static int PROCESSING_INSTRUCTION = 4;

	private static boolean matchNode(
		FPNode n,
		int type,
		String namespaceURI) {
		return (
			n.getType() == type
				&& ((namespaceURI == null)
					|| (namespaceURI.equals(n.getNameSpaceURI()))));
	}

	private static boolean matchNode(
		FPNode n,
		String name,
		int type,
		String namespaceURI) {
		boolean r1 =
			(n.getType() == type
				&& ((namespaceURI == null)
					|| (namespaceURI.equals(n.getNameSpaceURI()))));
		if (r1) {
			return name.equals(n.getContent());
		} else
			return false;
	}

	// Get all descendants

	private static void setAllDescendant(
		boolean self,
		boolean all,
		String name,
		int type,
		String namespaceURI,
		FPNode n,
		NodeSet ns) {

		if (self) {
			if (all && matchNode(n, type, namespaceURI)) {
				ns.add(n);
			} else if (matchNode(n, name, type, namespaceURI)) {
				ns.add(n);
			}
		}

		for (int i = 0; i < n.childCount(); i++) {
			FPNode n2 = n.childAt(i);
			if (all && (matchNode(n2, type, namespaceURI))) {
				ns.add(n2);
			} else if (matchNode(n2, name, type, namespaceURI)) {
				ns.add(n2);
			}
			if (n2.childCount() > 0) {
				setAllDescendant(false, all, name, type, namespaceURI, n2, ns);
			}
		}

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
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI);
	}

	// ancestor

	static class AncestorResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {

			FPNode ntmp = n.getFPParent();

			while (ntmp != null) {
				if (all && matchNode(ntmp, type, namespaceURI)) {
					ns.addNode(ntmp);
				} else {
					if (matchNode(ntmp, name, type, namespaceURI)) {
						ns.addNode(ntmp);
					}
				}
				ntmp = ntmp.getFPParent();
			}
		}
	}

	// ancestor-or-self

	static class AncestorOrSelfResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			FPNode ntmp = n;

			while (ntmp != null) {
				if (all && matchNode(ntmp, type, namespaceURI)) {
					ns.addNode(ntmp);
				} else {
					if (matchNode(ntmp, name, type, namespaceURI)) {
						ns.addNode(ntmp);
					}
				}
				ntmp = ntmp.getFPParent();
			}
		}
	}

	// attribute

	static class AttributeResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			FPNode ntmp = n;

			List<FPNode> attributes = n.getViewAttributeNodes();
			if ( attributes != null ) {
				for ( int i = 0; i < attributes.size(); i++ ) {
					FPNode attNode = attributes.get( i );
					String attName = attNode.getContent();
					if (all) {
						ns.addNode(
							new FPNode(
								n,
								FPNode.COMMENT_NODE + 1,
								attName));
					} else {
						if (name.equals(attName)) {
							ns.addNode(
								new FPNode(
									n,
									FPNode.COMMENT_NODE + 1,
									attName));
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
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			FPNode ntmp = n;
			for (int i = 0; i < n.childCount(); i++) {
				ntmp = n.childAt(i);
				if (all && matchNode(ntmp, type, namespaceURI)) {
					ns.addNode(ntmp);
				} else if (matchNode(ntmp, name, type, namespaceURI)) {
					ns.addNode(ntmp);
				}
			}
		}
	}

	// descendant

	static class DescendantResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			setAllDescendant(false, all, name, type, namespaceURI, n, ns);
		}
	}

	// descendant-or-self

	static class DescendantOrSelfResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			setAllDescendant(true, all, name, type, namespaceURI, n, ns);
		}
	}

	// following

	static class FollowingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			FPNode p = n.getFPParent();
			if (p != null) {
				int i = p.childNodeIndex(n);
				for (int j = i + 1; j < p.childCount(); j++) {
					setAllDescendant(
						true,
						all,
						name,
						type,
						namespaceURI,
						p.childAt(j),
						ns);
				}
			}
		}
	}

	// following-sibling

	static class FollowingSiblingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			FPNode p = n.getFPParent();
			if (p != null) {
				int i = p.childNodeIndex(n);
				for (int j = i + 1; j < p.childCount(); j++) {
					FPNode ntmp = p.childAt(j);
					if (all && matchNode(ntmp, type, namespaceURI)) {
						ns.addNode(ntmp);
					} else if (matchNode(ntmp, name, type, namespaceURI)) {
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
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			if (n.getFPParent() != null) {
				ns.add(n.getFPParent());
			}
		}
	}

	// preceding

	static class PrecedingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			FPNode p = n.getFPParent();
			if (p != null) {
				int i = p.childNodeIndex(n);
				for (int j = 0; j < i; j++) {
					setAllDescendant(
						true,
						all,
						name,
						type,
						namespaceURI,
						p.childAt(j),
						ns);
				}
			}
		}
	}

	// preceding-sibling

	static class PrecedingSiblingResolver implements AxisResolver {
		public void resolve(
			NodeSet ns,
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			FPNode p = n.getFPParent();
			if (p != null) {
				int i = p.childNodeIndex(n);
				for (int j = 0; j < i; j++) {
					FPNode ntmp = p.childAt(j);
					if (all && matchNode(ntmp, type, namespaceURI)) {
						ns.addNode(ntmp);
					} else if (matchNode(ntmp, name, type, namespaceURI)) {
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
			FPNode n,
			String name,
			int type,
			boolean all,
			String namespaceURI) {
			if (all) {
				if (matchNode(n, type, namespaceURI)) {
					ns.addNode(n);
				}
			} else if (matchNode(n, name, type, namespaceURI)) {
				ns.addNode(n);
			}
		}
	}

	//////////////////////////////

	public Object getRoot(Object refNode) {
		FPNode n = (FPNode) refNode;
		if (n.isRoot()) {
			return n;
		}
		return n.getDocument().getRoot();
	}

	public Object getDocumentRoot(Object refNode) {
		FPNode root = (FPNode) getRoot(refNode);
		FPNode doc = new FPNode(FPNode.TAG_NODE, "/");
		doc.appendChild(root);
		doc.setFPParent(null);
		root.setFPParent(null);
		return doc;
	}

	public NodeSet getNodes(
		Object refNode,
		String axis,
		String nodeType,
		String name,
		String namespaceURI,
		boolean attributeMode ) {

		FPNode n = (FPNode) refNode;
		NodeSet ns = new NodeSet();
		int type = NODE;

		if ("text".equals(nodeType)) {
			type = TEXT;
		} else if ("comment".equals(nodeType)) {
			type = COMMENT;
		} else if ("processing-instruction".equals(nodeType)) {
			type = PROCESSING_INSTRUCTION;

		}
		boolean all = "*".equals(name);

		AxisResolver resolver = (AxisResolver) htResolver.get(axis);
		if (resolver == null) {
			throw new RuntimeException("Unknown axis " + axis);
		}

		resolver.resolve(ns, n, name, type, all, namespaceURI);
		return ns;
	}

}
// FastParserNavigator ends here
