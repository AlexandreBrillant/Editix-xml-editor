package com.japisoft.editix.editor.xquery.helper;

import java.awt.Color;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

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
public class KeywordsHandler extends AbstractHelperHandler {

	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String activatorString) {

		for ( int i = 0; i < Keywords.main.length; i++ ) {

			String keyword = Keywords.main[ i ];
			BasicDescriptor rd = new BasicDescriptor( keyword );
			rd.setColor( Color.BLUE );
			addDescriptor( rd );

		}

		Color c = Color.MAGENTA.darker();
		
		for ( int i = 0; i < Keywords.axes.length; i++ ) {

			String keyword = Keywords.axes[ i ];
			BasicDescriptor rd = new BasicDescriptor( keyword );
			rd.setComment( "Axis" );
			rd.setColor( c );
			addDescriptor( rd );

		}

		c = Color.GREEN.darker();
		
		for ( int i = 0; i < Keywords.functions.length / 2; i += 2 ) {

			String keyword = Keywords.functions[ i ];
			BasicDescriptor rd = new BasicDescriptor( keyword );
			rd.setComment( "Function :\n" + Keywords.raw_functions[ i / 2 ] );
			rd.setColor( c );
			addDescriptor( rd );

		}

	}

	protected String getActivatorSequence() {
		return null;
	}

	public String getTitle() {
		return "Keywords and Functions";
	}

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		return activatorString == null;
	}

}
