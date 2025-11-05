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

package com.japisoft.framework.xml.parser.node;

import java.util.Iterator;

import com.japisoft.framework.xml.parser.document.Document;

/**
 * Minimal node interface. This interface is for building node from the parsing processing.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
public interface MutableNode {
	/** @return the current namespace URI */
	public String getNameSpaceURI();
    /** @return the current nameSpace prefix */
    public String getNameSpacePrefix();
    /** This node is a part of this nameSpace for this prefix and this URI */
    public void setNameSpace( String prefix, String uri );
    /** Add nameSpace support for this prefix and this URI */
    public void addNameSpaceDeclaration( String prefix, String uri );
	/** Check if this namespace prefix is declared in this node */
	public boolean isNamespaceDeclared( String prefix );
	/** @return a list of namespace prefix declaration */
	public Iterator<String> getNameSpaceDeclaration();
    /** Remove nameSpace for this prefix */
    public void removeNameSpaceDeclaration( String prefix );
    /** Reset the default namespace, using xmlns= value */
    public void setDefaultNamespace( String defaultNamespace );
    /** Return the default namespace declaration */
    public String getDefaultNamespace();
    /** Reset the node content, for tag node it means the tag name */
    public void setNodeContent( String content );
    /** @return the node content, for tag node it means the tag name */
    public String getNodeContent();
    /** Reset the node parent */
    public void setNodeParent( MutableNode node );
    /** Reset the node attribute */
    public void setNodeAttribute( String name, String value );
    /** Add a new child */
    public void addNode( MutableNode node );
    /** Reset the current starting tag line */
    public void setStartingLine( int line );
    /** Reset the current stopping line */
    public void setStoppingLine( int line );
    /** Reset the current staring offset */
    public void setStartingOffset( int offset );
    /** Reset the current stopping offset */
    public void setStoppingOffset( int offset );
    /** Particular way to know the tag declaration is in the &lt;A/&gt; form rather than &lt;A&gt;&lt;/A&gt; */
	public void setAutoClose( boolean closedLeaf );
	public void setDocument(Document doc);
	public boolean hasAttribute( String name );

}


