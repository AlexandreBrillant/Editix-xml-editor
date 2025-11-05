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



package com.japisoft.xpath.node;

import com.japisoft.xpath.*;
import com.japisoft.xpath.function.Lib;
import com.japisoft.xpath.FastVector;

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

@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
@author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)

*/
public class Function extends Expr {
    public Function() {
	super();
    }

    public String functionName;

    public void setName( String functionName ) {
	this.functionName = functionName;
    }

    public String getName() {
	return functionName;
    }

    public Object eval( XPathContext context ) {
	// Eval every parameter
	FastVector arg = new FastVector();
	// Save the current NodeSet and location
	int l = context.getContextPosition();
	NodeSet ns = context.getContextNodeSet();

	for ( int i = 0; i < getNodeCount(); i++ ) {
	    AbstractNode n = ( AbstractNode )getNodeAt( i );

	    if ( i > 0 ) {
		context.setContextNodeSet( ns );
		context.setContextPosition( l );
	    }

	    arg.addElement( n.eval( context ) );
	}

	// Reset the context
	if ( getNodeCount() > 0 ) {
	    context.setContextNodeSet( ns );
	    context.setContextPosition( l );
	}

	Lib lib = context.getLibrary();
	return lib.eval( getName(), context, arg );
    }

}


