package com.japisoft.editix.document.xslt;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
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
public class VariableParamNameHandler extends AbstractHelperHandler {

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {	
		XMLContainer container = document.getContainer();
		if ( container == null )
			return;

		if ( addedString == null )
			addedString = "";
		
		currentNode = container.getCurrentElementNode();
		if ( currentNode != null )
			orderedSearch( 
					addedString, 
					currentNode.getFPParent(), 
					currentNode );
	}

	private void orderedSearch( String addedString, FPNode parentNode, FPNode endChild ) {
		if ( parentNode != null ) {
			for ( int i = 0; i < parentNode.childCount(); i++ ) {
				FPNode child = parentNode.childAt( i );
				
				if ( child == endChild ) {
					orderedSearch( addedString, parentNode.getFPParent(), parentNode );
					break;
				}
				
				if ( child.matchContent( "variable" ) ||
						child.matchContent( "param" ) ) {
					
					if ( child.hasAttribute( "name" ) )
						addDescriptor( 
								new BasicDescriptor(
										addedString +
										child.getAttribute( "name" ) ) );
					
				}
			}
		}
	}

	protected String getActivatorSequence() {
		return "$";
	}

	public boolean haveDescriptors(FPNode currentNode,
			XMLPadDocument document, boolean insertBefore, int offset,
			String activatorString) {
		if ( "$".equals( activatorString ) ) {
			return document.isInsideAttributeValue( offset );
		} 
		return false;
	}

	public String getTitle() {
		return "variables or params";
	}

}
