package com.japisoft.editix.document.schema;

import java.util.Enumeration;
import java.util.Iterator;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
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
public class SchemaSubstitionGroupHandler extends AbstractHelperHandler {

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {	

		XMLContainer container = document.getContainer();
		if ( container == null )
			return;
		FPNode root = container.getRootNode();
		if ( root == null )
			return;
		if ( container.getCurrentElementNode() == null )
			return;

		String currentElement = "element";
		// Search for this element with all the name
		TreeWalker tw = new TreeWalker( root );
		Enumeration enume = tw.getNodeByCriteria(
				new NodeNameCriteria( currentElement ), false );
		if ( enume != null ) {

			String prefix = "";

			// Check for targetNameSpace
			if ( root.hasAttribute( "targetNamespace" ) ) {
				String namespace = root.getAttribute( "targetNamespace" );
				Iterator<String> enum2 = root.getNameSpaceDeclaration();
				// Search a prefix
				if ( enum2 != null ) {
					while ( enum2.hasNext() ) {
						String p = ( String )enum2.next();
						if ( namespace.equals( root.getNameSpaceDeclarationURI( p ) ) ) {
							prefix = p + ":";
							break;
						}
					}
				}
			}

			while ( enume.hasMoreElements() ) {
				FPNode n = ( FPNode )enume.nextElement();
				if ( n.hasAttribute( "name" ) )
					addDescriptor( new BasicDescriptor( prefix + n.getAttribute( "name" ) ) );
			}
		}
	}

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		if ( activatorString == null ) {
			return match(
				document, 
				offset, 
				"", 
				"substitutionGroup=\"" );
		} else {
			if  ( "\"".equals( activatorString ) ) {
				return match(
						document, 
						offset, 
						"", 
						"substitutionGroup=" );
			} else
				return false;
		}
	}

	public String getTitle() {
		return "Subtitution group helper";
	}

	protected String getActivatorSequence() {
		return null;
	}	
	
	public int getPriority() {
		return 1;
	}		
	
}
