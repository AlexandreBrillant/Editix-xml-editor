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

/**
 * View the content of the node
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public interface ViewableNode {
    /** @return nameSpace prefix support */
    public Iterator<String> getNameSpaceDeclaration();
    /** @return a nameSpace declaration URI for this prefix */
    public String getNameSpaceDeclarationURI( String prefix );
    /** @return the current nameSpace prefix */
    public String getNameSpacePrefix();
    /** @return the current nameSpace URI */
    public String getNameSpaceURI();
    /** @return the Default namespace */
    public String getDefaultNamespace();
    /** @return the attribute value for the name or null */
    public String getViewAttributeAt( int index );
    public String getViewAttribute(String name);
    public int getViewAttributeCount();
    /** @return the content of the node */
    public String getViewContent();
    /** @return true is the node is a leaf */
    public boolean isViewLeaf();
    /** @return the child count */
    public int getViewChildCount();
    /** @return a child starting from 0 */
    public ViewableNode getViewChildAt( int i );
    /** @return true for text node */
    public boolean isViewText();
    /** @return true for comment */
    public boolean isViewComment();
    /** @return the stopping line for this node */
    public int getStoppingLine();
    /** @return the starting line for this node */
    public int getStartingLine();
    /** @return the current stopping offset */
    public int getStoppingOffset();
    /** @return the current starting offset */
    public int getStartingOffset();
}


