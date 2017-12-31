package com.japisoft.dtdparser.document;

import java.io.IOException;
import java.io.PrintWriter;

import com.japisoft.dtdparser.node.*;

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
public abstract class AbstractDTDDocumentBuilder implements DTDDocumentBuilder {
	protected DTDNodeFactory factory;

	protected RootDTDNode root;

	public AbstractDTDDocumentBuilder() {
		super();
		factory = new DTDNodeFactory();
	}

	public RootDTDNode getRoot() {
		return root;
	}

	public void setNodeFactory(DTDNodeFactory factory) {
		this.factory = factory;
	}

	public void dump() {
		System.out.println( "**********************");		
		System.out.println( "******** DUMP ********" );
		System.out.println( "**********************");
		try {
			root.writeDTD( new PrintWriter(
					System.out ) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

// AbstractDTDDocumentBuilder ends here
