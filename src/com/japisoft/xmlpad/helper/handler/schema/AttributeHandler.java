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

package com.japisoft.xmlpad.helper.handler.schema;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.SchemaHelperManager;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

/** Common handler for attributes handler */
public class AttributeHandler extends AbstractHelperHandler {
	private AbstractTagHandler tagHandler;
	
	public AttributeHandler( AbstractTagHandler handler ) {
		this.tagHandler = handler;
	}

	public boolean haveDescriptors(
			FPNode currentNode, 
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset, 
			String activatorString ) {
		
		if ( " ".equals( activatorString ) && 
				document.isInsideTag( 
						offset,
						true,
						true ) ||
			( activatorString == null &&
					document.isInsideTag( offset, true, true ) )
		) {
			return true;
		}
		return false;
	}

	public void dispose() {
		super.dispose();
		this.tagHandler = null;
	}	
	
	protected String getActivatorSequence() {
		return " ";
	}

	public String getTitle() {
		return tagHandler.getTitle();
	}

	private String getForwardeAttributeName( 
			XMLPadDocument document, 
			int offset ) {
		try {
			StringBuffer sbRes = null;
			for ( int c = offset; c > 0; c-- ) {
				char ch = 
					document.getText( c, 1 ).charAt( 0 );
				if ( ch == '=' ) {
					sbRes = 
						new StringBuffer();
				} else
					if ( ch == '<' )
						return null;
					else
						if ( sbRes != null ) {
							if ( ch == ' ' || 
									ch == '\t' || 
										ch == '\n' )
								break;
							sbRes.insert( 0, ch );
						}
			}
			if ( sbRes != null )
				return sbRes.toString();
		} catch (BadLocationException e) {}
		return null;
	}

	protected void installDescriptors(
			FPNode currentNode,
			final XMLPadDocument document, 
			final int offset,
			final String activatorString ) {
/*		SimpleNode currentNode =
			document.getContainer().getCurrentElementNode(); */
		boolean mustReturn = false;
		if ( currentNode == null ) // Wrong state?
			mustReturn = true;
		TagDescriptor td = null;
		if ( !mustReturn )
			td = tagHandler.getTag( currentNode );
		if ( td == null ) {
			mustReturn = true;
		}

		if ( mustReturn ) {
			// For not loosing the whitespaces
			if ( activatorString != null ) {
				SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								document.insertStringWithoutHelper( offset, activatorString, null, true );								
							}
						}
				);

			}
			return;
		}

		AttDescriptor[] atts = td.getAtts();
		if ( atts != null )
			for ( int i = 0; i < atts.length; i++ ) {
				if ( currentNode.hasAttribute( atts[ i ].getName() ) )
					continue;
				
				atts[ i ].setAddedPart( activatorString );
				
				/*
					atts[ i ].setEnabled(
							!currentNode.hasAttribute(
									atts[ i ].getName() ) );
				*/

				// Force an automatic next helper ?
				XMLContainer container = document.getContainer();
				String[] toForce = container.getDocumentInfo().getListOfAttributesWithAutoAssistant();
				if ( toForce != null ) {
					for ( int j = 0; j < toForce.length; j++ ) {
						if ( toForce[ j ].equals(
								atts[ i ].getName() ) ) {
							atts[ i ].setAutomaticNextHelper( true );
							break;
						}
					}
					
				}
				addOrderedDescriptor( atts[ i ] );
			}
	}

	public String getName() {
		return SchemaHelperManager.SCHEMA_ATTRIBUTES;
	}	

	public boolean mustBeJobSynchronized() {
		return true;
	}

}

