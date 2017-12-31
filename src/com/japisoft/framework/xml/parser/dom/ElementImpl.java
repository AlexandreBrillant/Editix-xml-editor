package com.japisoft.framework.xml.parser.dom;

import com.japisoft.framework.xml.parser.node.*;

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
public class ElementImpl extends NodeImpl implements Element {


	public ElementImpl(String tagName) {
		super(FPNode.TAG_NODE, tagName);
	}

	public ElementImpl(int idTag) {
		super(FPNode.TAG_NODE, idTag);
	}

	/**
	 * The name of the element. For example, in: &lt;elementExample 
	 * id="demo"&gt;  ... &lt;/elementExample&gt; , <code>tagName</code> has 
	 * the value <code>"elementExample"</code>. Note that this is 
	 * case-preserving in XML, as are all of the operations of the DOM. The 
	 * HTML DOM returns the <code>tagName</code> of an HTML element in the 
	 * canonical uppercase form, regardless of the case in the  source HTML 
	 * document. 
	 */
	public String getTagName() {
		return getContent();
	}

	/** This method will return <code>null</code> */
	public String getNodeValue() throws DOMException {
		return null;
	}

	/** This method has no effect */
	public void setNodeValue(String nodeValue) throws DOMException {
	}

	public Node appendChild(Node newChild) throws DOMException {
		if ( newChild == null )
			throw new DOMExceptionImpl( DOMException.NOT_FOUND_ERR, "Null child" );
		if ( newChild.getNodeType() == Node.ATTRIBUTE_NODE ) {
			setAttribute( newChild.getNodeName(), newChild.getNodeValue() );
			return new AttrImpl( newChild.getNodeName(), this );
		} else
		return super.appendChild(newChild);
	}

	/**
	 * Retrieves an attribute value by name.
	 * @param name The name of the attribute to retrieve.
	 * @return The <code>Attr</code> value as a string, or the empty  string if 
	 *   that attribute does not have a specified or default value.
	 */
	public String getAttribute(String name) {
		return super.getAttribute(name);
	}

	/**
	 * Adds a new attribute. If an attribute with that name is already present 
	 * in the element, its value is changed to be that of the value parameter. 
	 * This value is a simple string, it is not parsed as it is being set. So 
	 * any markup (such as syntax to be recognized as an entity reference) is 
	 * treated as literal text, and needs to be appropriately escaped by the 
	 * implementation when it is written out. In order to assign an attribute 
	 * value that contains entity references, the user must create an 
	 * <code>Attr</code> node plus any <code>Text</code> and 
	 * <code>EntityReference</code> nodes, build the appropriate subtree, and 
	 * use <code>setAttributeNode</code> to assign it as the value of an 
	 * attribute.
	 * @param name The name of the attribute to create or alter.
	 * @param value Value to set in string form.
	 * @exception DOMException
	 *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
	 *   invalid character.
	 *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 */
	public void setAttribute(String name, String value) throws DOMException {
		super.setAttribute(name, value);
	}

	/**
	 * Removes an attribute by name. If the removed attribute has a default 
	 * value it is immediately replaced.
	 * @param name The name of the attribute to remove.
	 * @exception DOMException
	 *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 */
	public void removeAttribute(String name) throws DOMException {
		setAttribute(name, null);
	}

	/**
	 * Retrieves an <code>Attr</code> node by name.
	 * @param name The name of the attribute to retrieve.
	 * @return The <code>Attr</code> node with the specified attribute name or 
	 *   <code>null</code> if there is no such attribute.
	 */
	public Attr getAttributeNode(String name) {
		return new AttrImpl(name, this);
	}

	/**
	 * Adds a new attribute. If an attribute with that name is already present 
	 * in the element, it is replaced by the new one.
	 * @param newAttr The <code>Attr</code> node to add to the attribute list.
	 * @return If the <code>newAttr</code> attribute replaces an existing 
	 *   attribute with the same name, the  previously existing 
	 *   <code>Attr</code> node is returned, otherwise <code>null</code> is 
	 *   returned.
	 * @exception DOMException
	 *   WRONG_DOCUMENT_ERR: Raised if <code>newAttr</code> was created from a 
	 *   different document than the one that created the element.
	 *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>newAttr</code> is already an 
	 *   attribute of another <code>Element</code> object. The DOM user must 
	 *   explicitly clone <code>Attr</code> nodes to re-use them in other 
	 *   elements.
	 */
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String v = getAttribute( newAttr.getName() );
		setAttribute(newAttr.getName(), newAttr.getValue());
		if ( v == null )
			return null;
		return new AttrImpl( newAttr.getName(), this );
	}

	/**
	 * Removes the specified attribute.
	 * @param oldAttr The <code>Attr</code> node to remove from the attribute 
	 *   list. If the removed <code>Attr</code> has a default value it is 
	 *   immediately replaced.
	 * @return The <code>Attr</code> node that was removed.
	 * @exception DOMException
	 *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 *   <br>NOT_FOUND_ERR: Raised if <code>oldAttr</code> is not an attribute 
	 *   of the element.
	 */
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		setAttribute(oldAttr.getName(), null);
		return oldAttr;
	}

	/**
	 * Returns a <code>NodeList</code> of all descendant elements with a given 
	 * tag name, in the order in which they would be encountered in a preorder 
	 * traversal of the <code>Element</code> tree.
	 * @param name The name of the tag to match on. The special value "*" 
	 *   matches all tags.
	 * @return A list of matching <code>Element</code> nodes.
	 */
	public NodeList getElementsByTagName(String name) {
		return new NodeListImpl(this, name);
	}

	/**
	 * Puts all <code>Text</code> nodes in the full depth of the sub-tree 
	 * underneath this <code>Element</code> into a "normal" form where only 
	 * markup (e.g., tags, comments, processing instructions, CDATA sections, 
	 * and entity references) separates <code>Text</code> nodes, i.e., there 
	 * are no adjacent <code>Text</code> nodes.  This can be used to ensure 
	 * that the DOM view of a document is the same as if it were saved and 
	 * re-loaded, and is useful when operations (such as XPointer lookups) that 
	 * depend on a particular document tree structure are to be used.
	 */
	public void normalize() {
		throw new RuntimeException("Not supported");
	}

	public String getAttributeNS(String namespaceURI, String localName) {
		return getAttribute(localName);
	}

	public void setAttributeNS(
		String namespaceURI,
		String qualifiedName,
		String value)
		throws DOMException {
		setAttribute(qualifiedName, value);
	}

	public void removeAttributeNS(String namespaceURI, String localName)
		throws DOMException {
		setAttribute(localName, null);
	}

	public Attr getAttributeNodeNS(String namespaceURI, String localName) {
		AttrImpl att = new AttrImpl(localName, this);
		att.setNamespaceURI(namespaceURI);
		return att;
	}

	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return setAttributeNode(newAttr);
	}

	public NodeList getElementsByTagNameNS(
		String namespaceURI,
		String localName) {
		throw new RuntimeException("Not supported");
	}

	public boolean hasAttribute(String name) {
		return (getAttribute(name) != null);
	}

	public boolean hasAttributeNS(String namespaceURI, String localName) {
		return hasAttribute(localName);
	}

	public TypeInfo getSchemaTypeInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setIdAttribute(String name, boolean isId) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getBaseURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getFeature(String feature, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTextContent() throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getUserData(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEqualNode(Node arg) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSameNode(Node other) {
		// TODO Auto-generated method stub
		return false;
	}

	public String lookupNamespaceURI(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public String lookupPrefix(String namespaceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTextContent(String textContent) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

}

// ElementImpl ends here
