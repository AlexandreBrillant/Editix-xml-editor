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
public class NodeImpl extends FPNode implements Node {

	protected NodeImpl() {
		super(0, null);
	}

	public NodeImpl(int type, String content) {
		super(type, content);
	}

	public NodeImpl(int type, int id ) {
		super(type, id);
	}
		
	public String getNodeName() {
		return getNodeContent();
	}

	/**
	 * The value of this node, depending on its type; see the table above.
	 * @exception DOMException
	 *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
	 * @exception DOMException
	 *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than 
	 *   fit in a <code>DOMString</code> variable on the implementation 
	 *   platform.
	 */
	public String getNodeValue() throws DOMException {
		return getNodeContent();
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		setNodeContent(nodeValue);
	}

	/**
	 * A code representing the type of the underlying object, as defined above.
	 */
	public short getNodeType() {
		if (isText()) {
			return org.w3c.dom.Node.TEXT_NODE;
		} else if (isTag()) {
			return org.w3c.dom.Node.ELEMENT_NODE;
		} else if (isComment()) {
			return org.w3c.dom.Node.COMMENT_NODE;
		}
		// Attribute
		return org.w3c.dom.Node.ATTRIBUTE_NODE;
	}

	/**
	 * The parent of this node. All nodes, except <code>Document</code>, 
	 * <code>DocumentFragment</code>, and <code>Attr</code> may have a parent. 
	 * However, if a node has just been created and not yet added to the tree, 
	 * or if it has been removed from the tree, this is <code>null</code>.
	 */
	public Node getParentNode() {
		return (Node) getFPParent();
	}

	/**
	 * A <code>NodeList</code> that contains all children of this node. If there 
	 * are no children, this is a <code>NodeList</code> containing no nodes. 
	 * The content of the returned <code>NodeList</code> is "live" in the sense 
	 * that, for instance, changes to the children of the node object that 
	 * it	was created from are immediately reflected in the nodes returned by 
	 * the <code>NodeList</code> accessors; it is not a static snapshot of the 
	 * content of the node. This is true for every <code>NodeList</code>, 
	 * including the ones returned by the <code>getElementsByTagName</code> 
	 * method.
	 */
	public NodeList getChildNodes() {
		return new NodeListImpl(this);
	}

	/**
	 * The last child of this node. If there is no such node, this returns 
	 * <code>null</code>.
	 */
	public Node getLastChild() {
		if (childCount() == 0)
			return null;
		return (Node) childAt(childCount() - 1);
	}

	/**
	 * The node immediately preceding this node. If there is no such node, this 
	 * returns <code>null</code>.
	 */
	public Node getPreviousSibling() {
		FPNode p = getFPParent();
		if (p == null)
			return null;
		int index = p.childNodeIndex(this);
		if (index > 0)
			return (Node) p.childAt(index - 1);
		return null;
	}

	/**
	 * The first child of this node. If there is no such node, this returns 
	 * <code>null</code>. */
	public Node getFirstChild() {
		if (childCount() == 0)
			return null;
		return (Node) childAt(0);
	}

	/**
	 * The node immediately following this node. If there is no such node, this 
	 * returns <code>null</code>. */
	public Node getNextSibling() {
		FPNode p = getFPParent();
		if (p == null)
			return null;
		int index = p.childNodeIndex(this);
		if (index + 1 <= p.childCount() - 1)
			return (Node) p.childAt(index + 1);
		return null;
	}

	/**
	 * A <code>NamedNodeMap</code> containing the attributes of this node (if it 
	 * is an <code>Element</code>) or <code>null</code> otherwise. 
	 */
	public NamedNodeMap getAttributes() {
		if (isTag()) {
			return new NamedNodeMapImpl(this);
		}
		return null;
	}

	/**
	 * The <code>Document</code> object associated with this node. This is also 
	 * the <code>Document</code> object used to create new nodes. When this 
	 * node is a <code>Document</code> this is <code>null</code>.
	 */
	public Document getOwnerDocument() {
		if (ownerDoc != null)
			return ownerDoc;
		else
			ownerDoc = new DocumentImpl(getDocument());
		return ownerDoc;
	}

	private Document ownerDoc = null;

	public void setOwnerDocument(Document doc) {
		ownerDoc = doc;
	}

	/**
	 * Inserts the node <code>newChild</code> before the existing child node 
	 * <code>refChild</code>. If <code>refChild</code> is <code>null</code>, 
	 * insert <code>newChild</code> at the end of the list of children.
	 * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object, 
	 * all of its children are inserted, in the same order, before 
	 * <code>refChild</code>. If the <code>newChild</code> is already in the 
	 * tree, it is first removed.
	 * @param newChild The node to insert.
	 * @param refChild The reference node, i.e., the node before which the new 
	 *   node must be inserted.
	 * @return The node being inserted.
	 * @exception DOMException
	 *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not 
	 *   allow children of the type of the <code>newChild</code> node, or if 
	 *   the node to insert is one of this node's ancestors.
	 *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created 
	 *   from a different document than the one that created this node.
	 *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 *   <br>NOT_FOUND_ERR: Raised if <code>refChild</code> is not a child of 
	 *   this node.
	 */
	public Node insertBefore(Node newChild, Node refChild)
		throws DOMException {
		FPNode p = getFPParent();
		if (p == null)
			throw new DOMExceptionImpl(
				DOMException.HIERARCHY_REQUEST_ERR,
				"Invalid node");
		int i = p.childNodeIndex((FPNode) refChild);
		if (i < 0)
			throw new DOMExceptionImpl(
				DOMException.INDEX_SIZE_ERR,
				"Invalid reference node");
		p.insertChildNode(i, (FPNode) newChild);
		return newChild;
	}

	/**
	 * Replaces the child node <code>oldChild</code> with <code>newChild</code> 
	 * in the list of children, and returns the <code>oldChild</code> node. If 
	 * the <code>newChild</code> is already in the tree, it is first removed.
	 * @param newChild The new node to put in the child list.
	 * @param oldChildAtt The node being replaced in the list.
	 * @return The node replaced.
	 * @exception DOMException
	 *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not 
	 *   allow children of the type of the <code>newChild</code> node, or it 
	 *   the node to put in is one of this node's ancestors.
	 *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created 
	 *   from a different document than the one that created this node.
	 *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of 
	 *   this node.
	 */
	public Node replaceChild(Node newChild, Node oldChild)
		throws DOMException {
		insertBefore(newChild, oldChild);
		removeChild(oldChild);
		return newChild;
	}

	/**
	 * Removes the child node indicated by <code>oldChild</code> from the list 
	 * of children, and returns it.
	 * @param oldChild The node being removed.
	 * @return The node removed.
	 * @exception DOMException
	 *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 *   <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of 
	 *   this node.
	 */
	public Node removeChild(Node oldChild) throws DOMException {
		removeChildNode((FPNode) oldChild);
		return oldChild;
	}

	/**
	 * Adds the node <code>newChild</code> to the end of the list of children of 
	 * this node. If the <code>newChild</code> is already in the tree, it is 
	 * first removed.
	 * @param newChild The node to add.If it is a  <code>DocumentFragment</code> 
	 *   object, the entire contents of the document fragment are moved into 
	 *   the child list of this node
	 * @return The node added.
	 * @exception DOMException
	 *   HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not 
	 *   allow children of the type of the <code>newChild</code> node, or if 
	 *   the node to append is one of this node's ancestors.
	 *   <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created 
	 *   from a different document than the one that created this node.
	 *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
	 */
	public Node appendChild(Node newChild) throws DOMException {
		appendChild((FPNode) newChild);
		return newChild;
	}

	/**
	 *  This is a convenience method to allow easy determination of whether a 
	 * node has any children.
	 * @return  <code>true</code> if the node has any children, 
	 *   <code>false</code> if the node has no children.
	 */
	public boolean hasChildNodes() {
		return !(isLeaf());
	}

	/**
	 * Returns a duplicate of this node, i.e., serves as a generic copy 
	 * constructor for nodes. The duplicate node has no parent (
	 * <code>parentNode</code> returns <code>null</code>.).
	 * <br>Cloning an <code>Element</code> copies all attributes and their 
	 * values, including those generated by the  XML processor to represent 
	 * defaulted attributes, but this method does not copy any text it contains 
	 * unless it is a deep clone, since the text is contained in a child 
	 * <code>Text</code> node. Cloning any other type of node simply returns a 
	 * copy of this node. 
	 * @param deep If <code>true</code>, recursively clone the subtree under the 
	 *   specified node; if <code>false</code>, clone only the node itself (and 
	 *   its attributes, if it is an <code>Element</code>).  
	 * @return The duplicate node.
	 */
	public Node cloneNode(boolean deep) {
		return (Node) clone(deep);
	}

	public void normalize() {
		throw new RuntimeException("Not supported");
	}

	public boolean isSupported(java.lang.String a, java.lang.String b) {
		return false;
	}

	public String nameSpaceURI;

	public String getNamespaceURI() {
		return super.getNameSpaceURI();
	}

	public void setNamespaceURI(String namespace) {
	}

	public String getPrefix() {
		return getNameSpacePrefix();
	}

	public void setPrefix(String str) {
	}

	public String getLocalName() {
		return getContent();
	}

	public boolean hasAttributes() {
		return (getViewAttributeCount() > 0 );
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
