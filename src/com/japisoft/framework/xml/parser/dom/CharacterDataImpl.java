package com.japisoft.framework.xml.parser.dom;

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
public class CharacterDataImpl extends NodeImpl implements CharacterData {
    public CharacterDataImpl() {
	super();   
    }

    public CharacterDataImpl( int type ) {
	super( type, null );
    }

    public CharacterDataImpl( int type, String content ) {
	super( type, content );
    }

    /**
     * The character data of the node that implements this interface. The DOM 
     * implementation may not put arbitrary limits on the amount of data that 
     * may be stored in a  <code>CharacterData</code> node. However, 
     * implementation limits may  mean that the entirety of a node's data may 
     * not fit into a single <code>DOMString</code>. In such cases, the user 
     * may call <code>substringData</code> to retrieve the data in 
     * appropriately sized pieces.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @exception DOMException
     *   DOMSTRING_SIZE_ERR: Raised when it would return more characters than 
     *   fit in a <code>DOMString</code> variable on the implementation 
     *   platform.
     */
    public String getData() throws DOMException {
	return getContent();
    }

    public void setData(String data) throws DOMException {
	setContent( data );
    }

    /**
     * The number of characters that are available through <code>data</code> and 
     * the <code>substringData</code> method below.  This may have the value 
     * zero,  i.e., <code>CharacterData</code> nodes may be empty.
     */
    public int getLength() {
	if ( getContent() == null )
	    return 0;
	return getContent().length();
    }

    /**
     * Extracts a range of data from the node.
     * @param offset Start offset of substring to extract.
     * @param count The number of characters to extract.
     * @return The specified substring. If the sum of <code>offset</code> and 
     *   <code>count</code> exceeds the <code>length</code>, then all 
     *   characters to the end of the data are returned.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified offset is negative or greater 
     *   than the number of characters in <code>data</code>, or if the 
     *   specified <code>count</code> is negative.
     *   <br>DOMSTRING_SIZE_ERR: Raised if the specified range of text does not 
     *   fit into a <code>DOMString</code>.
     */
    public String substringData(int offset, 
				int count)
	throws DOMException {
	if ( getContent() == null )
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, " no content" );
	if ( offset < 0 || ( offset + count ) >= getContent().length() ) {
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, " no content" );
	}
	return getContent().substring( offset, offset + count );
    }

    /**
     * Append the string to the end of the character data of the node. Upon 
     * success, <code>data</code> provides access to the concatenation of 
     * <code>data</code> and the <code>DOMString</code> specified.
     * @param arg The <code>DOMString</code> to append.
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void appendData(String arg)
	throws DOMException {
	if ( getContent() == null )
	    setContent( arg );
	else
	    setContent( getContent() + arg );
    }

    /**
     * Insert a string at the specified character offset.
     * @param offset The character offset at which to insert.
     * @param arg The <code>DOMString</code> to insert.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified offset is negative or greater 
     *   than the number of characters in <code>data</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void insertData(int offset, 
			   String arg)
	throws DOMException {
	if ( getContent() == null )
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "Invalid content" );
	if ( offset >= getContent().length() )
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "Invalid offset " + offset );
	String s = getContent();
	setContent( s.substring( 0, offset ) + arg + s.substring( offset ) );
    }

    /**
     * Remove a range of characters from the node. Upon success, 
     * <code>data</code> and <code>length</code> reflect the change.
     * @param offset The offset from which to remove characters.
     * @param count The number of characters to delete. If the sum of 
     *   <code>offset</code> and <code>count</code> exceeds <code>length</code> 
     *   then all characters from <code>offset</code> to the end of the data 
     *   are deleted.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified offset is negative or greater 
     *   than the number of characters in <code>data</code>, or if the 
     *   specified <code>count</code> is negative.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void deleteData(int offset, 
			   int count)
	throws DOMException {
	if ( getContent() == null )
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "No content" );
	if ( offset < 0 || ( offset + count ) >= getContent().length() )
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "Invalid offset " + offset );
	String s = getContent();
	setContent( s.substring( 0, offset ) + s.substring( offset + count ) );
    }

    /**
     * Replace the characters starting at the specified character offset with 
     * the specified string.
     * @param offset The offset from which to start replacing.
     * @param count The number of characters to replace. If the sum of 
     *   <code>offset</code> and <code>count</code> exceeds <code>length</code>
     *   , then all characters to the end of the data are replaced (i.e., the 
     *   effect is the same as a <code>remove</code> method call with the same 
     *   range, followed by an <code>append</code> method invocation).
     * @param arg The <code>DOMString</code> with which the range must be 
     *   replaced.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified offset is negative or greater 
     *   than the number of characters in <code>data</code>, or if the 
     *   specified <code>count</code> is negative.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public void replaceData(int offset, 
			    int count, 
			    String arg)
	throws DOMException {
	if ( getContent() == null )
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "No content" );
	if ( offset < 0 || ( offset + count ) > getContent().length() ) {
	    throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "Invalid offset " + offset );
	}
	String s = getContent();
	setContent( s.substring( 0, offset ) + arg + s.substring( offset + count ) );
    }

}

// CharacterDataImpl ends here

