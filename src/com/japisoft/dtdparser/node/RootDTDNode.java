package com.japisoft.dtdparser.node;

import com.japisoft.dtdparser.CannotFindElementException;
import com.japisoft.dtdparser.XMLGenerator;

import java.io.*;
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
public class RootDTDNode extends DTDNode implements XMLGenerator {

	public RootDTDNode() {
		super();
		setNodeType(ROOT);
	}

	private Hashtable htElement;

	private Hashtable htEntity;

	/** @return the first element from the DTD definition */
	public String getFirstElementName() {
		for (Enumeration enume = getDTDNodeForType(ELEMENT); enume
				.hasMoreElements();) {
			ElementDTDNode n = (ElementDTDNode) enume.nextElement();
			return n.getName();
		}
		return null;
	}

	// --------------- Declaration ---------------------

	/** Check if the nodeName is declared ? */
	public boolean isNodeDeclared(String nodeName) {
		if (htElement == null)
			return false;
		return htElement.containsKey(nodeName);
	}

	/**
	 * @return an entity declaration for this name or null if the entity is not
	 *         declared
	 */
	public EntityDTDNode getEntityDeclaration(String entityName) {
		if (htEntity == null)
			return null;
		return (EntityDTDNode) htEntity.get(entityName);
	}

	/**
	 * @return an element declaration for this name or null if the node is not
	 *         declared
	 */
	public ElementDTDNode getElementDeclaration(String nodeName) {
		if (htElement == null)
			return null;
		if (nodeName == null)
			return null;
		return (ElementDTDNode) htElement.get(nodeName);
	}

	// -------------- Validation ------------------------

	/**
	 * Check if the nodeParent can have the nodeName as child after the
	 * 'previousNodeName', occurence is for the node count
	 */
	public boolean isNodeValid(String nodeParentName, String previousNodeName,
			String nodeName, int occurence) {
		if (nodeParentName == null) {
			return isNodeDeclared(nodeName);
		}

		ElementDTDNode ref = getElementDeclaration(nodeParentName);
		if (ref == null)
			return false;
		return ref.isNodeChildSupported(nodeName, previousNodeName, occurence);
	}

	/** Check if the entity name is declared ? */
	public boolean isEntityValid(String entityName) {
		if (htElement == null)
			return false;
		return htElement.containsKey(entityName);
	}

	/** Check if the nodeName supports this attribute */
	public boolean isAttributeValid(String nodeName, String attributeName) {
		if (htElement == null)
			return false;
		ElementDTDNode e = (ElementDTDNode) htElement.get(nodeName);
		if (e == null)
			return false;
		return (e.getAttributeDeclaration(attributeName) != null);
	}

	/** Check if the nodeName supports this attribute name and value */
	public boolean isAttributeValid(String nodeName, String attributeName,
			String attributeValue) {
		if (htElement == null)
			return false;
		ElementDTDNode e = (ElementDTDNode) htElement.get(nodeName);
		if (e == null)
			return false;
		AttributeDTDNode a = e.getAttributeDeclaration(attributeName);
		if (a == null)
			return false;
		return a.isValueValid(attributeValue);
	}

	// ------------------------------------------------

	/** Override for fast element access */
	public void addDTDNode(DTDNode node) {
		if (node.isComment() && !isPreservedComment())
			return;

		super.addDTDNode(node);
		if (node.isElement()) {
			ElementDTDNode element = (ElementDTDNode) node;
			if (htElement == null)
				htElement = new Hashtable();

			htElement.put(element.getName(), element);
		} else if (node.isEntity()) {
			if (htEntity == null)
				htEntity = new Hashtable();
			EntityDTDNode entity = (EntityDTDNode) node;
			if (entity.getName() != null)
				htEntity.put(entity.getName(), entity);
		}
	}

	/**
	 * @return a node definition for a name : <code>null</code> is returned
	 *         for unknown element
	 */
	public ElementDTDNode getElementDefinitionByName(String name)
			throws CannotFindElementException {
		if (htElement == null) // || name == null )
			return null;
		if (name == null)
			throw new CannotFindElementException();
		return (ElementDTDNode) htElement.get(name);
	}

	/**
	 * @return entity value : <code>null</code> is returned for unknown
	 *         element
	 */
	public String getEntityValue(String name) {
		if (htEntity == null)
			return null;
		EntityDTDNode node = (EntityDTDNode) htEntity.get(name);
		if (node == null)
			return null;
		return node.getValue();
	}

	/** @return the entity definition for the name */
	public EntityDTDNode getEntityDefinitionByName(String name) {
		return (EntityDTDNode) htEntity.get(name);
	}

	private boolean preservedComment = true;

	/** Save the comment node : By default to true */
	public void preserveComment(boolean comment) {
		this.preservedComment = comment;
	}

	/** @return true if the comment node are preserved */
	public boolean isPreservedComment() {
		return preservedComment;
	}

	// -------------- GENERATOR -------------------

	/**
	 * Generate a minimal valid XML document. You may insert an encoding using
	 * the <code>ENCODING_...</code> constants
	 * 
	 * @param output
	 *            final document target
	 * @param encoding
	 *            use <code>ENCODING..<code> constants
	 @param rootNode the root node for the generation
	 @param dtdURI dtd location
	 */
	public void writeDocument(PrintWriter output, String encoding,
			String rootNode, String dtdURI) throws IOException {

		output.write("<?xml version=\"1.0\"");
		if (encoding != null)
			output.write(" encoding=\"" + encoding + "\"");
		output.write(">\n\n");

		if (htElement != null) {
			ElementDTDNode ref = (ElementDTDNode) htElement.get(rootNode);
			if (ref != null) {
				// Write the DTD reference
				output.write("<!DOCTYPE " + rootNode + " SYSTEM \"" + dtdURI
						+ "\">\n\n");

				ref.writeDocument(output);
			} else
				output.write("<" + rootNode + "/>");
		} else
			output.write("<" + rootNode + "/>");
	}

	/**
	 * Generate a minimal valid XML document.
	 * 
	 * @param output
	 *            final document target
	 * @param root
	 *            the root node for the generation
	 * @param dtdURI
	 *            dtd location
	 */
	public void writeDocument(PrintWriter output, String root, String dtdURI)
			throws IOException {
		writeDocument(output, null, root, dtdURI);
	}

	// -------------- GENERATOR -------------------

	/**
	 * Rewrite the DTD to the output stream : Note that comment node are always
	 * written in the DTD header only
	 */
	public void writeDTD(PrintWriter output) throws IOException {

		writeComment(output);

		// Write entities
		if (htEntity != null)
			for (Enumeration enume = htEntity.elements(); enume.hasMoreElements();) {
				output.println(enume.nextElement());
			}

		output.println();
		output.println();

		// Write elements
		if (htElement != null)
			for (Enumeration enume = htElement.elements(); enume
					.hasMoreElements();) {
				output.println("" + enume.nextElement());
			}
		else
			output.println("<!-- No element found -->");

		output.println();
		output.flush();
	}

}
