package com.japisoft.editix.editor.xsd.view2.nodeview;

import com.japisoft.editix.editor.xsd.view2.node.XSDNode;

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
