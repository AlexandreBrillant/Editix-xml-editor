// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.xml.parser.dom;

import com.japisoft.framework.xml.parser.node.*;

import java.util.Enumeration;
import org.w3c.dom.*;

/**
 * NamedNodeMap
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class NamedNodeMapImpl implements NamedNodeMap {
	private FPNode n;

	public NamedNodeMapImpl(FPNode n) {
		super();
		this.n = n;
	}

	/**
	 * Retrieves a node specified by name.
	 * @param name Name of a node to retrieve.
	 * @return A <code>Node</code> (of any type) with the specified name, or 
	 *   <code>null</code> if the specified name did not identify any node in 
	 *   the map. 
	 */
	public Node getNamedItem(String name) {
		// Return an attribute node for the name
		if (n.getAttribute(name) == null)
			return null;
		return new AttrImpl(name, n);
	}

	/**
	 * Adds a node using its <code>nodeName</code> attribute. 
	 * <br>As the <code>nodeName</code> attribute is used to derive the name 
	 * which the node must be stored under, multiple nodes of certain types 
	 * (those that have a "special" string value) cannot be stored as the names 
	 * would clash. This is seen as preferable to allowing nodes to be aliased.
	 * @param arg A node to store in a named node map. The node will later be 
	 *   accessible using the value of the <code>nodeName</code> attribute of 
	 *   the node. If a node with that name is already present in the map, it 
	 *   is replaced by the new one.
	 * @return If the new <code>Node</code> replaces an existing node with the 
	 *   same name  the previously existing <code>Node</code> is returned, 
	 *   otherwise <code>null</code> is returned.
	 * @exception DOMException
	 *   WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
	 *   different document than the one that created the 
	 *   <code>NamedNodeMap</code>.
	 *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this 
	 *   <code>NamedNodeMap</code> is readonly.
	 *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
	 *   <code>Attr</code> that is already an attribute of another 
	 *   <code>Element</code> object. The DOM user must explicitly clone 
	 *   <code>Attr</code> nodes to re-use them in other elements.
	 */
	public Node setNamedItem(Node arg) throws DOMException {
		if (arg instanceof Attr) {
			Attr att = (Attr) arg;
			String name = att.getName();
			if (name == null)
				return null;
			if (n.getAttribute(name) != null) {
				// Reset value
				n.setAttribute(name, att.getValue());
			}
			return arg;
		} else
			throw new DOMExceptionImpl(
				DOMException.NO_MODIFICATION_ALLOWED_ERR,
				"Invalid argument : need an Attr node type ");
	}

	/**
	 * Removes a node specified by name. If the removed node is an 
	 * <code>Attr</code> with a default value it is immediately replaced.
	 * @param name The name of a node to remove.
	 * @return The node removed from the map or <code>null</code> if no node 
	 *   with such a name exists.
	 * @exception DOMException
	 *   NOT_FOUND_ERR: Raised if there is no node named <code>name</code> in 
	 *   the map.
	 */
	public Node removeNamedItem(String name) throws DOMException {
		n.setAttribute(name, null);
		return null;
	}

	/**
	 * Returns the <code>index</code>th item in the map. If <code>index</code> 
	 * is greater than or equal to the number of nodes in the map, this returns 
	 * <code>null</code>.
	 * @param index Index into the map.
	 * @return The node at the <code>index</code>th position in the 
	 *   <code>NamedNodeMap</code>, or <code>null</code> if that is not a valid 
	 *   index.
	 */
	public Node item(int index) {
		return new AttrImpl(n.getViewAttributeAt(index), n);
	}

	/**
	 * The number of nodes in the map. The range of valid child node indices is 
	 * 0 to <code>length-1</code> inclusive. 
	 */
	public int getLength() {
		return n.getViewAttributeCount();
	}

	public Node getNamedItemNS(String namespaceURI, String localName) {
		throw new RuntimeException("Not supported");
	}

	public Node setNamedItemNS(Node arg) throws DOMException {
		throw new RuntimeException("Not supported");
	}

	public Node removeNamedItemNS(String namespaceURI, String localName)
		throws DOMException {
		throw new RuntimeException("Not supported");
	}

}


