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

package com.japisoft.editix.document.xslt;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;

import com.japisoft.dtdparser.DTDParser;
import com.japisoft.dtdparser.node.AttributeDTDNode;
import com.japisoft.dtdparser.node.ElementDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.handler.schema.dtd.DTDAttDescriptor;
import com.japisoft.xmlpad.helper.handler.schema.dtd.DTDTagDescriptor;

public class HTMLHandler extends AbstractHelperHandler {

	private RootDTDNode node = null;
	private boolean rejectIt = false;

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {
		
		if ( node == null && !rejectIt ) {
		
			// Parse the HTML DTD
			XMLDocumentInfo info = 
				DocumentModel.getDocumentForType( "XHTML" );
			String HTMLDTD = info.getDefaultDTDLocation();
			if ( HTMLDTD.startsWith( "file:" ) )
				HTMLDTD = HTMLDTD.substring( 5 );
			if ( HTMLDTD.indexOf( ":" ) > -1 ) {
				if ( HTMLDTD.startsWith( "/" ) )
					HTMLDTD = HTMLDTD.substring( 1 );
			}

			DTDParser parser = new DTDParser();
			try {
				parser.parse( new FileInputStream( HTMLDTD ) );
				node = parser.getDTDElement();
			} catch (IOException e) {
				rejectIt = true;
			}

		}
		
		if ( node != null ) {
			
			if ( !attributesAssistant ) {
			
				for ( int i = 0; i < node.getDTDNodeCount(); i++ ) {
					Color pink2 = Color.PINK.darker();
					if ( node.getDTDNodeAt( i ).isElement() ) {
						DTDTagDescriptor dtd = ( DTDTagDescriptor )addDescriptor( 
								new DTDTagDescriptor( 
										( ElementDTDNode )node.getDTDNodeAt( i ) ) );
						dtd.setColor( pink2 );
						dtd.setAddedPart( "<" );
					}
				}
				
			} else {
				
				// Search for the matchingNode
				
				for ( int i = 0; i < node.getDTDNodeCount(); i++ ) {
					if ( node.getDTDNodeAt( i ).isElement() ) {

						ElementDTDNode e = ( ElementDTDNode )node.getDTDNodeAt( i );
						if ( e.getName().equals( matchingNode ) ) {
							
							// We got it
							for ( int j = 0; j < e.getChildCount(); j++ ) {
								
								if ( e.getDTDNodeAt( j ).isAttribute() ) {
									
									DTDAttDescriptor dad = ( DTDAttDescriptor )addOrderedDescriptor( new DTDAttDescriptor(
											( AttributeDTDNode )e.getDTDNodeAt( j ) ) );
									dad.setAddedPart( " " );

								}

							}
							
							break;
							
						}
						
					}

				}
			}
		}
	}

	protected String getActivatorSequence() {
		return null;
	}

	private String matchingNode = null;

	private boolean isForHtml( FPNode currentNode ) {
		FPNode tmpNode = currentNode;
		boolean foundTemplate = false;

		while ( tmpNode != null ) {
			if ( tmpNode.matchContent( "template" ) )
				foundTemplate = true;
			tmpNode = tmpNode.getFPParent();
		}
		
		if ( foundTemplate ) {
			// Check for the HTML output
			FPNode rootNode = ( FPNode )currentNode.getDocument().getRoot();
			for ( int i = 0; i < rootNode.childCount(); i++ ) {
				FPNode c = rootNode.childAt( i );
				if ( c.matchContent( "output" ) ) {
					return "html".equalsIgnoreCase( c.getAttribute( "method" ) ) ||
						"xhtml".equalsIgnoreCase( c.getAttribute( "method" ) );
				}
			}
		}
		return false;
	}

	private boolean attributesAssistant;
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {

		if (currentNode == null )
			return false;
		
		matchingNode = currentNode.getContent();		
		attributesAssistant = false;

		// Check for attributes
		if ( null == activatorString || 
						"<".equals( activatorString )
				) {

			// Ignore attribute value
			if ( document.isInsideAttributeValue( offset ) )

				return false;
			

			if ( isForHtml( currentNode ) ) {

				return true;

			}
			
		}
		
		if ( " ".equals( activatorString ) ) {

			if  ( document.isInsideTag(
					offset,
					true,
					true ) ) {

				if ( !"http://www.w3.org/1999/XSL/Transform"
						.equals( currentNode.getNameSpaceURI() ) ) {
					attributesAssistant = true;
					return true; 
				}
				
			}

		}
		

		return false;
	}

	public String getTitle() {
		return "HTML";
	}

	public int getPriority() {
		return -1;
	}

}
