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

package com.japisoft.framework.xml.parser.document;

import java.util.List;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.document.DocumentBuilderException;
import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * Interface for building an XML document
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public interface DocumentBuilder {

	/** By default to false, if true, a list will available
	 * node is built. This is a flat view of the tree. It can be
	 * useful for searching node on location
	 * @param listMode */
	public void setFlatView( boolean mode );
	
	/** @return a flat view of the current tree */
	public List<FPNode> getFlatView();

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


