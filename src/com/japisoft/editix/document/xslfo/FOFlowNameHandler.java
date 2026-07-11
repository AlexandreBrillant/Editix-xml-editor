// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.document.xslfo;

import java.util.Enumeration;
import java.util.Iterator;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class FOFlowNameHandler extends AbstractHelperHandler {

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
		// Search for page master-name
		TreeWalker tw = new TreeWalker( root );
		FPNode node = tw.getFirstTagNodeByName( "layout-master-set", false );
		if ( node != null ) {
			tw = new TreeWalker( node );
			Iterator e = tw.getNodeByCriteria( 
					new AttributeCriteria( "region-name" ), true );
			while ( e.hasNext() ) {
				FPNode n = ( FPNode )e.next();
				String mr = n.getAttribute( "region-name" );
				if ( mr != null ) {
					addDescriptor(
							new BasicDescriptor( 
									addedString + 
									mr + 
									addedString ) );
				}
			}

			String[] s = new String[] {
					"xsl-region-body", 
					"xsl-region-before",
					"xsl-region-after", 
					"xsl-region-start", 
					"xsl-region-end", 
					"xsl-before-float-separator", 
					"xsl-footnote-separator"			
			};

			for ( int i = 0; i < s.length; i++ ) {
				// Add default cases
				addDescriptor( new BasicDescriptor( 
										addedString + 
										s[ i ] + 
										addedString ) );
			}
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
			return match(
				document, 
				offset, 
				"", 
				"flow-name=\"" );
		} else {
			if  ( "\"".equals( activatorString ) ) {
				return match(
						document, 
						offset, 
						"", 
						"flow-name=" );
			} else
				return false;
		}
	}

	public String getTitle() {
		return "Flow Name";
	}

	public int getPriority() {
		return 1;
	}

}