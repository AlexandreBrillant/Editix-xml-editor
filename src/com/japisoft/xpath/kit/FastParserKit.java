// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath.kit;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xpath.function.basic.FunctionLib;
import com.japisoft.xpath.function.Lib;
import com.japisoft.xpath.navigator.FastParserNavigator;
import com.japisoft.xpath.AbstractKit;
import com.japisoft.xpath.NodeSet;
import com.japisoft.xpath.Navigator;


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
public class FastParserKit
    extends AbstractKit {

  public FastParserKit() {
    super();
  }

  /** @return the library resolver. If <code>null</code> is returned then the standard library is used */
  public Lib getLibrary() {
    return new FunctionLib();
  }

  /** @return the tree navigator toolkit */
  public Navigator getNavigator() {
    return new FastParserNavigator();
  }

  // Particular method for the string-value on node
  private void findSubTextNode(FPNode n, StringBuffer res) {
    for (int i = 0; i < n.childCount(); i++) {
      FPNode child = n.childAt(i);
      if (child.isText()) {
        res.append(child.getContent());
      }
    }
    for (int i = 0; i < n.childCount(); i++) {
      FPNode child = n.childAt(i);
      if (child.isTag() && !child.isLeaf()) {
        findSubTextNode(child, res);
      }
    }
  }

  /** Compute the string-value for this node */
  public String getStringValue(Object node) {
    if (node instanceof FPNode) {
      FPNode n = (FPNode) node;
      // Cas of the attribute
      if (n.getType() == (FPNode.COMMENT_NODE + 1)) {
        FPNode p = n.getFPParent();
        return p.getAttribute(n.getContent());
      }
      else
      if ( (n.getType() == FPNode.TEXT_NODE) ||
          (n.getType() == FPNode.COMMENT_NODE)) {
        return n.getContent();
      }
      // Particular cas for node : get all sub text node
      StringBuffer sb = new StringBuffer();
      findSubTextNode(n, sb);
      return sb.toString();
    }
    else
    if (node instanceof NodeSet) {
      if ( ( (NodeSet) node).size() > 0) {
        return getStringValue( ( (NodeSet) node).elementAt(0));
      }
    }
    return node.toString();
  }

  private void buildNodeSet(FPNode node, NodeSet ns) {
    ns.addNode(node);
    for (int i = 0; i < node.childCount(); i++) {
      FPNode n1 = node.childAt(i);
      if ( (n1.childCount() > 0) &&
          (n1.isTag())) {
        buildNodeSet(n1, ns);
      }
    }
  }

  /** Compute the local name of the node */
  public String getLocalName(Object node) {
    FPNode n = (FPNode) node;
    return n.getContent();
  }

  /** Compute the namespace URI for this node */
  public String getNamespaceURI(Object node) {
    FPNode n = (FPNode) node;
    return n.getNameSpaceURI();
  }

  /** Compute the qualified name for this node */
  public String getName(Object node) {
    FPNode n = (FPNode) node;
    if (n.getNameSpacePrefix() != null) {
      return n.getNameSpacePrefix() + ":" + n.getContent();
    }
    return getLocalName(node);
  }

  /** Compute the language for this node */
  public String getLang(Object node) {
    FPNode n = (FPNode) node;
    if ( n == null )
    	return null;
    String s = n.getAttribute( "xml:lang" );
    if ( "".equals( s ) ) {
      if ( n.getFPParent() != null )
        return getLang( n.getFPParent() );
      else
        return null;
    } else
      return s;
  }

  /**
   * This method will always return <code>null</code> as the
   * FastParser SimpleNode for v1.6 doesn't includes support for ID
   * @param refNode a reference document element
   * @param id ID value to match
   * @return the node with the unique ID. The ID scope is theorically
   * limited to attribute defined as ID in the DTD
   */
  public Object getNodeForId(Object refNode, String id) {
    return null;
  }

}
