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

package com.japisoft.editix.editor.xsd.view2.node;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view2.nodeview.XSDNodeView;

public interface XSDNode {

	public int getChildCount();
	
	public XSDNode getChildAt( int index );
	
	public XSDNodeView getView();
	
	public boolean isOpened();
	
	public void setOpened( boolean state );
	
	public boolean isSelected();
	
	public boolean isMarked();
	
	public void setSelected( boolean selected );
	
	public void setData( String name, Object value );
	
	public Object getData( String name );
	
	public Element getDOM();
	
	public void setParent( XSDNode parent );
	
	public void repaint();
	
	public void invalidate();
	
	public void remove();
	
	public XSDNode getParent();
	
	public XSDNode add( String nodeName );
	
	public XSDNode insert( String nodeName );
	
	public boolean append( Element newChildren );
	
	public boolean isRoot();
	
	public boolean match( String name );
		
	public boolean isEnabled();
	
	public boolean containsNode( Element node );
	
	public boolean isReference();
	
	public String getReferenceName();
	
	public void moveUp();
	
	public void moveDown();
	
	public void dump();
	
}

