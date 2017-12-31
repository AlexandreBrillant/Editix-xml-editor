package com.japisoft.editix.editor.xsd.view2.node;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view2.nodeview.XSDNodeView;

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
	
	public void moveUp();
	
	public void moveDown();
	
}
