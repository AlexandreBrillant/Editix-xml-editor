// (c) ALEXANDRE BRILLANT : http://www.japisoft.com
// All this work is confidential, you have rights to
// change and evolve it for your products but you
// have no rights to sell it, propose concurrent works.
// Morever any changes to bugs or evolutions should
// be send to JAPISOFT that needs to maintain a
// valid version and has all rights on the product.

package com.japisoft.xpath.kit;

import com.japisoft.xpath.function.basic.FunctionLib;
import com.japisoft.xpath.function.Lib;
import com.japisoft.xpath.navigator.DOMNavigator;
import com.japisoft.xpath.AbstractKit;
import com.japisoft.xpath.NodeSet;
import com.japisoft.xpath.Navigator;

import org.w3c.dom.*;

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
public class DOMKit
    extends AbstractKit {

  /** Feature for ignoring lower/upper case, by default to false */
  public final static String IGNORE_CASE_FEATURE = "http://www.japisoft.com/jxp/dom/ignorecase";

  public DOMKit() {
    super();
    addFeature( IGNORE_CASE_FEATURE, false);
  }

	/** Replace a DOM document by its root */
  public Object getBetterReferenceNode( Object ref ) {
		if ( ref instanceof Document ) {
			return ( (Document)ref ).getDocumentElement();
		}  	
		return ref;
  }

  /** @return the library resolver. If <code>null</code> is returned then the standard library is used */
  public Lib getLibrary() {
    return new FunctionLib();
  }

  private Navigator cache;

  /** @return the tree navigator toolkit */
  public Navigator getNavigator() {
    if (cache != null) {
      return cache;
    }
    cache = new DOMNavigator();
    if ( isFeatureSupported( IGNORE_CASE_FEATURE ) )
      ( (DOMNavigator)cache ).setIgnoreCaseMode( true );
    return cache;
  }

  // Particular method for the string-value on node
  private void findSubTextNode(Node n, StringBuffer res) {
    NodeList nl = n.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node child = nl.item(i);
      if (child.getNodeType() == Node.TEXT_NODE) {
        res.append(child.getNodeValue());
      }
    }
    for (int i = 0; i < nl.getLength(); i++) {
      Node child = nl.item(i);
      if (child.hasChildNodes() && child.getNodeType() == Node.ELEMENT_NODE) {
        findSubTextNode(child, res);
      }
    }
  }

  /** Compute the string-value for this node */
  public String getStringValue(Object node) {
    if (node instanceof Node) {
      Node n = (Node) node;
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        // Particular cas for node : get all sub text node
        StringBuffer sb = new StringBuffer();
        findSubTextNode(n, sb);
        return sb.toString();
      }
      else {
        return n.getNodeValue();
      }
    }
    else
    if (node instanceof NodeSet) {
      NodeSet ns = (NodeSet) node;
      if (ns.size() > 0) {
        Node n = (Node) ns.elementAt(0);
        return getStringValue(n);
      }
    }
    return node.toString();
  }

  /** Compute the local name of the node */
  public String getLocalName(Object node) {
    Node n = (Node) node;
    String s = n.getLocalName();
    if (s == null) {
      s = n.getNodeName();
    }
    if (s == null) {
      return "";
    }
    return s;
  }

  /** Compute the namespace URI for this node */
  public String getNamespaceURI(Object node) {
    Node n = (Node) node;
    return n.getNamespaceURI();
  }

  /** Compute the qualified name for this node */
  public String getName(Object node) {
    Node n = (Node) node;
    return n.getNodeName();
  }

  /** Compute the language for this node */
  public String getLang(Object node) {
    if (node instanceof Element) {
      Element e = (Element) node;
      String l = e.getAttribute("xml:lang");
      if ("".equals(l)) {
        if (e.getParentNode() != null) {
          return getLang(e.getParentNode());
        }
        else {
          return null;
        }
      }
      else {
        return l;
      }
    }
    else {
      return null;
    }
  }

  /**
   * @param refNode a reference document element
   * @param id ID value to match
   * @return the node with the unique ID. The ID scope is theorically
   * limited to attribute defined as ID in the DTD
   */
  public Object getNodeForId(Object refNode, String id) {
    Document doc = ( (Node) refNode).getOwnerDocument();
    if (doc == null) {
      throw new RuntimeException("No owner document for node " + refNode + " ?");
    }
    return doc.getElementById(id);
  }

}
