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

package com.japisoft.editix.document.xslt;

import java.util.Enumeration;

import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AndCriteria;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class CallTemplateNameHandler extends AbstractHelperHandler {

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
		if ( addedString == null )
			addedString = "";		

		TreeWalker tw = new TreeWalker( root );
		Enumeration e = tw.getNodeByCriteria(
				new AndCriteria( 
						new NodeNameCriteria( "template" ),
						new AttributeCriteria( "name" ) ), false );

		while ( e.hasMoreElements() ) {
			FPNode n = ( FPNode )e.nextElement();
			addDescriptor( new BasicDescriptor(
					n.getAttribute( "name" ) ) );
		}
	}

	protected String getActivatorSequence() {
		return null;
	}

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {

		if ( activatorString == null ) {
			boolean b = match(
				document, 
				offset, 
				"", 
				"name=\"" );

			try {
				if ( b ) {
					String previousTag = document.getPreviousOpeningTagInsideATagPartWithoutPrefix( offset );
					return "call-template".equals( previousTag );
				}
			} catch ( BadLocationException e ) {
			}
			
			return b;
		} else {
			if  ( "\"".equals( activatorString ) ) {
				boolean b = match(
						document, 
						offset, 
						"", 
						"name=" );

				try {
					if ( b ) {
						String previousTag = document.getPreviousOpeningTagInsideATagPartWithoutPrefix( offset );
						return "call-template".equals( previousTag );
					}
				} catch (BadLocationException e) {
				}
				
				return b;
			} else
				return false;
		}
	}

	public String getTitle() {
		return "Templates";
	}

	public int getPriority() {
		return 1;
	}
}

