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
public class DocumentImpl extends NodeImpl implements org.w3c.dom.Document {
	com.japisoft.framework.xml.parser.document.Document d;

	private Element root;

	public DocumentImpl(com.japisoft.framework.xml.parser.document.Document d) {
		super();
		this.d = d;
		if ( d.getRoot() instanceof Element ) {
			root = ( Element )d.getRoot();
		}
	}

	public DocumentImpl( Element root ) {
		this.root = root;
	}

	com.japisoft.framework.xml.parser.document.Document getInnerDocument() {
		if ( d == null ) {
			d = new com.japisoft.framework.xml.parser.document.Document();
			d.setNodeFactory( new DomNodeFactory() );
		}
		return d;
	}

	/**
	 * The Document Type Declaration (see <code>DocumentType</code>) associated 
	 * with  this document. For HTML documents as well as XML documents without 
	 * a document type declaration this returns <code>null</code>. The DOM Level
	 *  1 does not support editing the Document Type Declaration, therefore 
	 * <code>docType</code> cannot be altered in any way.
	 */
	public DocumentType getDoctype() {
		return new DocumentTypeImpl();
	}

	public short getNodeType() {
		return Node.DOCUMENT_NODE;
	}

	/**
	 * The <code>DOMImplementation</code> object that handles this document. A 
	 * DOM application may use objects from multiple  implementations.
	 */
	public DOMImplementation getImplementation() {
		return new DOMImplementationImpl();
	}

	/**
	 * This is a convenience attribute that allows direct access to the child 
	 * node that is the root element of  the document. For HTML documents, this 
	 * is the element with the tagName "HTML".
	 */
	public Element getDocumentElement() {
		if ( root != null )
			return (Element)root;
		if ( d != null )
			return (Element) d.getRoot();
		return null;
	}

	public Node getFirstChild() {
		return getDocumentElement();
	}

	public Node getNextSibling() {
		return null;
	}

	/**
	 * Creates an element of the type specified. Note that the instance returned 
	 * implements the Element interface, so attributes can be specified 
	 * directly  on the returned object.
	 * @param tagName The name of the element type to instantiate. For XML, this 
	 *   is case-sensitive. For HTML, the  <code>tagName</code> parameter may 
	 *   be provided in any case,  but it must be mapped to the canonical 
	 *   uppercase form by  the DOM implementation. 
	 * @return A new <code>Element</code> object.
	 * @exception DOMException
	 *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
	 *   invalid character.
	 */
	public Element createElement(String tagName) throws DOMException {
		return (Element) (getInnerDocument().getNodeFactory().getTagNode(tagName));
	}

	/**
	 * Creates an empty <code>DocumentFragment</code> object. 
	 * @return A new <code>DocumentFragment</code>.
	 */
	public DocumentFragment createDocumentFragment() {
		return new DocumentFragmentImpl();
	}

	/**
	 * Creates a <code>Text</code> node given the specified string.
	 * @param data The data for the node.
	 * @return The new <code>Text</code> object.
	 */
	public Text createTextNode(String data) {
		return (Text) getInnerDocument().getNodeFactory().getTextNode(data);
	}

	/**
	 * Creates a <code>Comment</code> node given the specified string.
	 * @param data The data for the node.
	 * @return The new <code>Comment</code> object.
	 */
	public Comment createComment(String data) {
		return (Comment) getInnerDocument().getNodeFactory().getCommentNode(data);
	}

	/**
	 * Creates a <code>CDATASection</code> node whose value  is the specified 
	 * string.
	 * @param data The data for the <code>CDATASection</code> contents.
	 * @return The new <code>CDATASection</code> object.
	 * @exception DOMException
	 *   NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
	 */
	public CDATASection createCDATASection(String data) throws DOMException {
		throw new DOMExceptionImpl(
			DOMException.NOT_SUPPORTED_ERR,
			"Not supported");
	}

	/**
	 * Creates a <code>ProcessingInstruction</code> node given the specified 
	 * name and data strings.
	 * @param target The target part of the processing instruction.
	 * @param data The data for the node.
	 * @return The new <code>ProcessingInstruction</code> object.
	 * @exception DOMException
	 *   INVALID_CHARACTER_ERR: Raised if an invalid character is specified.
	 *   <br>NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
	 */
	public ProcessingInstruction createProcessingInstruction(
		String target,
		String data)
		throws DOMException {
		throw new DOMExceptionImpl(
			DOMException.NOT_SUPPORTED_ERR,
			"Not supported");
	}

	/**
	 * Creates an <code>Attr</code> of the given name. Note that the 
	 * <code>Attr</code> instance can then be set on an <code>Element</code> 
	 * using the <code>setAttribute</code> method. 
	 * @param name The name of the attribute.
	 * @return A new <code>Attr</code> object.
	 * @exception DOMException
	 *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
	 *   invalid character.
	 */
	public Attr createAttribute(String name) throws DOMException {
		return new AttrImpl(name, null);
	}

	/**
	 * Creates an EntityReference object.
	 * @param name The name of the entity to reference. 
	 * @return The new <code>EntityReference</code> object.
	 * @exception DOMException
	 *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
	 *   invalid character.
	 *   <br>NOT_SUPPORTED_ERR: Raised if this document is an HTML document. 
	 */
	public EntityReference createEntityReference(String name)
		throws DOMException {
		throw new DOMExceptionImpl(
			DOMException.NOT_SUPPORTED_ERR,
			"Not supported");
	}

	/**
	 * Returns a <code>NodeList</code> of all the <code>Element</code>s with a 
	 * given tag name in the order in which they would be encountered in a 
	 * preorder traversal of the <code>Document</code> tree. 
	 * @param tagname The name of the tag to match on. The special value "*" 
	 *   matches all tags.
	 * @return A new <code>NodeList</code> object containing all the matched 
	 *   <code>Element</code>s.
	 */
	public NodeList getElementsByTagName(String tagname) {
		if ( d == null || root == null )
			return null;
		return root.getElementsByTagName( tagname );
	}

	public Node importNode(Node importedNode, boolean deep)
		throws DOMException {
		NodeImpl n = (NodeImpl) importedNode.cloneNode(deep);
		n.setOwnerDocument(this);
		return n;
	}

	public Element createElementNS(String namespaceURI, String qualifiedName)
		throws DOMException {
		NodeImpl n = (NodeImpl) createElement(qualifiedName);
		n.setNamespaceURI(namespaceURI);
		return (Element) n;
	}

	public Attr createAttributeNS(String namespaceURI, String qualifiedName)
		throws DOMException {
		NodeImpl n = (NodeImpl) createAttribute(qualifiedName);
		n.setNamespaceURI(namespaceURI);
		return (Attr) n;
	}

	public NodeList getElementsByTagNameNS(
		String namespaceURI,
		String localName) {
		throw new RuntimeException("Not supported");
	}

	public Element getElementById(String elementId) {
		return null;
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

	public Node adoptNode(Node source) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDocumentURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public DOMConfiguration getDomConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInputEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getStrictErrorChecking() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getXmlEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getXmlStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getXmlVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public void normalizeDocument() {
		// TODO Auto-generated method stub
		
	}

	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDocumentURI(String documentURI) {
		// TODO Auto-generated method stub
		
	}

	public void setStrictErrorChecking(boolean strictErrorChecking) {
		// TODO Auto-generated method stub
		
	}

	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public void setXmlVersion(String xmlVersion) throws DOMException {
		// TODO Auto-generated method stub
		
	}

}

// DocumentImpl ends here
