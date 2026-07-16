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

package com.japisoft.editix.editor.html.helper;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.text.BadLocationException;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.xml.dtdparser.DTDParser;
import com.japisoft.framework.xml.dtdparser.node.AttributeDTDNode;
import com.japisoft.framework.xml.dtdparser.node.ElementDTDNode;
import com.japisoft.framework.xml.dtdparser.node.RootDTDNode;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.handler.schema.dtd.DTDAttDescriptor;
import com.japisoft.xmlpad.helper.handler.schema.dtd.DTDTagDescriptor;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

public class HTMLHandler extends AbstractHelperHandler {

	private RootDTDNode node = null;

	String[] html5 = null;
	
	public HTMLHandler() {
		html5 = new String[] {
				"canvas", "Used to draw graphics, on the fly, via scripting (usually JavaScript)",
				"audio", "Defines sound content",
				"video", "Defines a video or movie",
				"source", "Defines multiple media resources for <video> and <audio>",
				"embed", "Defines a container for an external application or interactive content (a plug-in)",
				"track", "Defines text tracks for <video> and <audio>",
				"datalist", "Specifies a list of pre-defined options for input controls",
				"keygen", "Defines a key-pair generator field (for forms)",
				"output", "Defines the result of a calculation",
				"article", "Defines an article",
				"aside", "Defines content aside from the page content",
				"bdi", "Isolates a part of text that might be formatted in a different direction from other text outside it",
				"command", "Defines a command button that a user can invoke",
				"details", "Defines additional details that the user can view or hide",
				"dialog", "Defines a dialog box or window",
				"summary", "Defines a visible heading for a <details> element",
				"figure", "Specifies self-contained content, like illustrations, diagrams, photos, code listings, etc.",
				"figcaption", "Defines a caption for a <figure> element",
				"footer", "Defines a footer for a document or section",
				"header", "Defines a header for a document or section",
				"mark", "Defines marked/highlighted text",
				"meter", "Defines a scalar measurement within a known range (a gauge)",
				"nav", "Defines navigation links",
				"progress", "Represents the progress of a task",
				"ruby", "Defines a ruby annotation (for East Asian typography)",
				"rt", "Defines an explanation/pronunciation of characters (for East Asian typography)",
				"rp", "Defines what to show in browsers that do not support ruby annotations",
				"section", "Defines a section in a document",
				"time", "Defines a date/time",
				"wbr", "Defines a possible line-break"
		};
	}
	
	private boolean isHTML5( XMLPadDocument document ) {
		try {
			String tmp = document.getText( 0, document.getLength() ).toLowerCase();
			return tmp.contains( "<!doctype html>" ) || tmp.contains( "<!doctype  html>" );
		} catch( BadLocationException ble ) {
			return false;
		}
	}

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {
				
		if ( "#".equals( addedString ) ) {
			
			List<FPNode> fv = currentNode.getDocument().getFlatNodes();
			if ( fv != null ) {
				for ( int i = 0; i < fv.size(); i++ ) {
					FPNode node = ( FPNode )fv.get( i );
					if ( node.hasAttribute( "id" ) ) {
						String idValue = node.getAttribute( "id" );
						
						BasicDescriptor bd = new BasicDescriptor( "#" + idValue );
						addOrderedDescriptor( bd );
					}
				}
			}
			
			return;
		}
		
		
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
			return;
		}

		if ( node != null ) {
			
			if ( !attributesAssistant ) {
			
				for ( int i = 0; i < node.getDTDNodeCount(); i++ ) {
					Color pink2 = Color.PINK.darker();
					if ( node.getDTDNodeAt( i ).isElement() ) {
						DTDTagDescriptor dtd = ( DTDTagDescriptor )addOrderedDescriptor( 
								new DTDTagDescriptor( 
									( ElementDTDNode )node.getDTDNodeAt( i ) ) 
						);
						dtd.setColor( pink2 );
						dtd.setAddedPart( "<" );
					}
				}

				if ( isHTML5( document ) ) {
				
					Color tmp = Color.ORANGE.darker();
					
					for ( int i = 0; i < html5.length; i += 2 ) {
						String element = html5[ i ];
						String comment = html5[ i + 1 ];
	
						TagDescriptor td = new TagDescriptor( element, false );
						td.setColor( tmp );
						td.setComment( "HTML 5 Element : " + comment );
						addOrderedDescriptor( td );
						td.setAddedPart( "<" );

					}

				}
				
			} else {
				
				// Search for the matchingNode

				try {
				
					String matchingNode = document.getPreviousOpeningTag(offset);
					boolean found = false;
					
					for ( int i = 0; i < node.getDTDNodeCount(); i++ ) {
						if ( node.getDTDNodeAt( i ).isElement() ) {
							ElementDTDNode e = ( ElementDTDNode )node.getDTDNodeAt( i );
							if ( e.getName().equalsIgnoreCase( matchingNode ) ) {
								found = true;
								break;
							}
						}
					}
					
					if ( !found )	// For HTML 5 element
						matchingNode = "div";

					for ( int i = 0; i < node.getDTDNodeCount(); i++ ) {
						if ( node.getDTDNodeAt( i ).isElement() ) {
	
							ElementDTDNode e = ( ElementDTDNode )node.getDTDNodeAt( i );
							if ( e.getName().equalsIgnoreCase( matchingNode ) ) {
								
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
					
				} catch( BadLocationException ble ) {

				}
			}
		}
	}

	protected String getActivatorSequence() {
		return null;
	}

	private boolean attributesAssistant;
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
				
		attributesAssistant = false;

		if ( "#".equals( activatorString ) )
			return true;
		
		// Check for attributes
		if ( null == activatorString || 
						"<".equals( activatorString )
				) {

			// Ignore attribute value
			if ( document.isInsideAttributeValue( offset ) )

				return false;
					
			
			return true;
		}
		
		if ( " ".equals( activatorString ) ) {

			if  ( document.isInsideTag(
					offset,
					true,
					true ) ) {

				attributesAssistant = true;
				return true; 
				
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
