package com.japisoft.framework.xml.parser.node;

import java.util.Iterator;

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

// ViewableNode ends here
