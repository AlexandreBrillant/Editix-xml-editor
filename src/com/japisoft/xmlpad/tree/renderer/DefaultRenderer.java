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

package com.japisoft.xmlpad.tree.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer.TreeLabel;

public class DefaultRenderer implements NodeRenderer {
	private static final String TEXT = "text";
	protected ImageIcon i1,i2;
	protected TreeLabel tl;

	public DefaultRenderer(
			ImageIcon i1,
			ImageIcon i2,
			TreeLabel tl ) {
		this.i1 = i1;
		this.i2 = i2;
		this.tl = tl;
	}

	private Map attributes = null;

	public void setAttribute( 
			String nodeName, 
			String attributeName ) {
		if ( attributes == null ) {
			attributes = new HashMap();
		}
		if ( attributeName != null && 
				!"".equals( attributeName ) ) {
			attributes.put( 
				nodeName, 
				attributeName 
			);
		} else {
			attributes.remove( nodeName );
			if ( attributes.size() == 0 )
				attributes = null;
		}
	}

	public void prepare(
			FPNode n, 
			boolean selected ) {
		if ( n.isTag() ) {
			ImageIcon i = i1;
			String sub_content = null;
			if ( attributes != null ) {
				sub_content = 
					( String )attributes.get( 
						n.getNodeContent() );
				if ( sub_content != null )
					sub_content = n.getAttribute( sub_content );
			}
			if ( sub_content == null )
				sub_content = n.getFirstAttributeValue();
			if ( n.childCount() > 0 )
				tl.setContent( n + " (" + n.childCount() + ")",
						sub_content, selected, i);
			else
				tl.setContent( n + " ", sub_content, selected, i);
		} else
			tl.setContent( TEXT, n.getContent(), selected, i2);
	}

}

