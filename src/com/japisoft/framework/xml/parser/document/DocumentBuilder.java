package com.japisoft.framework.xml.parser.document;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.document.DocumentBuilderException;

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
public interface DocumentBuilder {

	/** By default to false, if true, a list will available
	 * node is built. This is a flat view of the tree. It can be
	 * useful for searching node on location
	 * @param listMode */
	public void setFlatView( boolean mode );
	
	/** @return a flat view of the current tree */
	public FastVector getFlatView();

    /** Check the closing node : <code>false</code> should be used for always well formed document */
    public void setCheckForCloseTag( boolean check );

    /** Reset the current node */
    public Object openNode( FPParser parser, String prefix, String prefixURI, String tag ) throws DocumentBuilderException;

    /** Close the current node, an exception is thrown for invalid tag name */
    public Object closeNode( FPParser parser, String prefix, String tag ) throws DocumentBuilderException;

    /** Close the last open node */
    public void closeNode( FPParser parser ) throws DocumentBuilderException;

    /** Reset attribute for the current node, an exception is thrown for no current node */
    public void setAttribute( String prefix, String prefixURI, String att, String value ) throws DocumentBuilderException;

    /** Set the namespace prefix and its URI */
    public void setNameSpace( String prefix, String prefixURI );

    /** Add a new node for the current node */
    public void addCommentNode( FPParser parser, String comment );

    /** Add a new text node for the current node */
    public Object addTextNode( FPParser parser, String text ) throws DocumentBuilderException;

    /** @return current document */
    public Document getDocument();

    /** Remove start and end white space for text */
    public void trimTextNode( boolean trim );
    
    /** @return <code>false</code> if the root element has not been closed */
    public boolean isTerminated();
    
    /** Called at the end of the parsing */
    public void dispose();
}

// DocumentBuilder ends here
