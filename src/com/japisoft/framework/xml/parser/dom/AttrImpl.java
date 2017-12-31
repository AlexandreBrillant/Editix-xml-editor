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
public class AttrImpl extends NodeImpl implements Attr {
	private FPNode ref;
	private String name;

	/** @param name Attribute name
	@param ref Reference node */
	public AttrImpl(String name, FPNode ref) {
		super(-1, ref != null ? ref.getAttribute(name) : null );
		this.name = name;
		this.ref = ref;
	}

	public int getStartingLine() {
		return ref.getStartingLine();
	}

	public int getStartingOffset() {
		return ref.getStartingOffset();
	}
	
	/**
	 * Returns the name of this attribute. 
	 */
	public String getName() {
		return name;
	}

	public String getLocalName() {
		return name;
	}

	public String getNodeName() {
		return name;	
	}

	/**
	 * If this attribute was explicitly given a value in the original document, 
	 * this is <code>true</code>; otherwise, it is <code>false</code>. Note 
	 * that the implementation is in charge of this attribute, not the user. If 
	 * the user changes the value of the attribute (even if it ends up having 
	 * the same value as the default value) then the <code>specified</code> 
	 * flag is automatically flipped to <code>true</code>.  To re-specify the 
	 * attribute as the default value from the DTD, the user must delete the 
	 * attribute. The implementation will then make a new attribute available 
	 * with <code>specified</code> set to <code>false</code> and the default 
	 * value (if one exists).
	 * <br>In summary: If the attribute has an assigned value in the document 
	 * then  <code>specified</code> is <code>true</code>, and the value is the  
	 * assigned value. If the attribute has no assigned value in the document 
	 * and has  a default value in the DTD, then <code>specified</code> is 
	 * <code>false</code>,  and the value is the default value in the DTD. If 
	 * the attribute has no assigned value in the document and has  a value of 
	 * #IMPLIED in the DTD, then the  attribute does not appear  in the 
	 * structure model of the document.
	 */
	public boolean getSpecified() {
		return false;
	}

	/**
	 * On retrieval, the value of the attribute is returned as a string. 
	 * Character and general entity references are replaced with their values.
	 * <br>On setting, this creates a <code>Text</code> node with the unparsed 
	 * contents of the string.
	 */
	public String getValue() {
		if ( ref == null )
			return null;
		return ref.getAttribute(name);
	}

	public void setValue(String value) {
		ref.setAttribute(name, value);
	}

	public Element getOwnerElement() {
		return (Element) ref;
	}
	
	public void setNodeValue( String value ) {
		if ( ref != null )
			setValue( value );
	}

	public String getNodeValue() {
		if ( ref != null )
			return getValue();
		return null;
	}

	public boolean isId() {
		return false;
	}

	public TypeInfo getSchemaTypeInfo() {
		return null;
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return 0;
	}

	public String getBaseURI() {
		return null;
	}

	public Object getFeature(String feature, String version) {
		return null;
	}

	public String getTextContent() throws DOMException {
		return null;
	}

	public Object getUserData(String key) {
		return null;
	}

	public boolean isDefaultNamespace(String namespaceURI) {
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

// AttrImpl ends here
