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

package com.japisoft.editix.editor.xsd.view2.nodeview;

import com.japisoft.editix.editor.xsd.view2.node.XSDNode;

public class XSDNodeViewFactory {

	private static XSDNodeViewFactory instance = null;

	private XSDNodeViewFactory() {
		instance = this;
	}

	public static XSDNodeViewFactory getInstance() {
		if ( instance == null )
			new XSDNodeViewFactory();
		return instance;
	}

	public XSDNodeView getView( XSDNode node ) {
		if ( node.match( "attribute" ) ) {
			return new AttributeNodeView( node );
		} else
		if ( node.match( "sequence" ) ) {
			return new SequenceXSDNodeView( node );
		} else
		if ( node.match( "all" ) ) {
			return new SequenceXSDNodeView( node );
		} else
		if ( node.match( "choice" ) ) {
			return new ChoiceXSDNodeView3( node );
		} else
		if ( node.match( "key" ) ) {
			return new KeyXSDNodeView( node );
		} else
		if ( node.match( "keyref" ) ) {
			return new KeyRefXSDNodeView2( node );
		}
		return new AbstractXSDNodeView( node );
	}
	
}

