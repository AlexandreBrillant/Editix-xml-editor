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

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

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

public class XSLFOHandler extends AbstractHelperHandler {

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
				DocumentModel.getDocumentForType( "FO" );
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
			
				String p = ( xslFoPrefix != null ? xslFoPrefix + ":" : "" );
				for ( int i = 0; i < node.getDTDNodeCount(); i++ ) {
					Color pink2 = Color.PINK.darker();
					if ( node.getDTDNodeAt( i ).isElement() ) {
						DTDTagDescriptor dtd = ( DTDTagDescriptor )addDescriptor( 
								new DTDTagDescriptor(
										( ElementDTDNode )node.getDTDNodeAt( i ) ) );
						dtd.setColor( pink2 );
	
						dtd.setNameHelper( p + dtd.getName() );
	
						dtd.setAddedPart( "<" + p );
						dtd.setEndAddedPart( p );
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
									
									DTDAttDescriptor dad = ( DTDAttDescriptor )addDescriptor( new DTDAttDescriptor(
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

	private String xslFoPrefix = null;
	private String matchingNode = null;

	private boolean isForXSLFO( FPNode currentNode ) {
		FPNode tmpNode = currentNode;

		boolean foundTemplate = false;
		String ns = "http://www.w3.org/1999/XSL/Format";
		boolean foundXSLFoNs = false;

		while ( tmpNode != null ) {
			if ( tmpNode.matchContent( "template" ) ) {
				foundTemplate = true;
			}
			Iterator<String> enume = tmpNode.getNameSpaceDeclaration();
			if ( enume != null )
			while ( enume.hasNext() ) {
				String prefix = ( String )enume.next();
				String value = tmpNode.getNameSpaceDeclarationURI( prefix );
				if ( ns.equals( value ) ) {
					xslFoPrefix = prefix;
					foundXSLFoNs = true;
					break;
				}
			}
			if ( ns.equals( tmpNode.getNameSpaceURI() ) ) {
				foundXSLFoNs = true;
				xslFoPrefix = tmpNode.getNameSpacePrefix();
			}
			if ( foundTemplate && foundXSLFoNs )
				break;
			tmpNode = tmpNode.getFPParent();
		}

		return ( foundTemplate && foundXSLFoNs );		
	}

	private boolean attributesAssistant;
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {

		if ( currentNode == null )
			return false;
		
		matchingNode = currentNode.getContent();		
		attributesAssistant = false;

		if ( null == activatorString || 
				"<".equals( activatorString ) ) {

			// Ignore attribute value
			if ( document.isInsideAttributeValue( offset ) )

				return false;
			
			if ( isForXSLFO( currentNode ) ) {

					return true;
			}
		} else
		if ( " ".equals( activatorString ) ) {
		
			// Check if the offset is inside the tag starting part
			if  ( document.isInsideTag(
				offset,
				true,
				true ) ) {
				
				if ( "http://www.w3.org/1999/XSL/Format"
						.equals( currentNode.getNameSpaceURI() ) ) {
					attributesAssistant = true;
					return true; 
				}				

			}			
		}

		return false;
	}

	public String getTitle() {
		return "XSL-FO";
	}

	public int getPriority() {
		return -1;
	}

}

