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

package com.japisoft.editix.editor.css.helper;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class PropertiesHandler extends AbstractHelperHandler {

	protected String getActivatorSequence() {
		return null;
	}

	public String getTitle() {
		return "CSS properties";
	}

	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String activatorString ) {

		for ( int i = 0; i< Keywords.properties.length; i++ ) {
			BasicDescriptor rd = new BasicDescriptor( Keywords.properties[ i ] );
			if ( "{".equals( activatorString ) )
				rd.setAddedPart( "{\n" );
			else
				rd.setAddedPart( activatorString );
			addOrderedDescriptor( rd );
		}
		
		Color c = new Color( 100, 100, 0 );
		
		for ( int i = 0; i< Keywords.CSS3Properties.length; i++ ) {
			BasicDescriptor rd = new BasicDescriptor( Keywords.CSS3Properties[ i ] );
			rd.setComment( "CSS3 Property" );
			rd.setColor( c );
			if ( "{".equals( activatorString ) )
				rd.setAddedPart( "{\n" );
			else
				rd.setAddedPart( activatorString );
			addOrderedDescriptor( rd );
		}
				
	}

	public boolean haveDescriptors(FPNode currentNode,
			XMLPadDocument document, boolean insertBefore, int offset,
			String activatorString) {		
		if ( activatorString == null && 
				!checkForLeftPoint(document, offset - 1) && inSelector(document, offset))
			return true;
		return false;
	}

	static boolean inSelector( XMLPadDocument document, int offset ) {
		try {
			int index = document.getDefaultRootElement().getElementIndex( offset );
			int line = index - 1;	
			// Search for {
			before:for ( ; line >=0; line-- ) {
				Element e = document.getDefaultRootElement().getElement( line );
				for ( int i = e.getStartOffset(); i <= e.getEndOffset(); i++ ) {
					if ( "}".equals( document.getText( i, 1 ) ) )
						return false;
					if ( "{".equals( document.getText( i, 1 ) ) )
						break before;
				}
			}
			line = index;
			for ( ; line < document.getDefaultRootElement().getElementCount(); line++ ) {
				Element e = document.getDefaultRootElement().getElement( line );
				if ( e == null )
					return false;

				int start = e.getStartOffset();
				if ( line == index ) {
					start = offset;
				}
				for ( int i = start; i <= e.getEndOffset(); i++ ) {
					if ( "}".equals( document.getText( i, 1 ) ) )
						return true;
					if ( "{".equals( document.getText( i, 1 ) ) )
						return false;
				}
			}			
			return false;
		} catch( BadLocationException ble ) {
			return false;
		}
	}

	private boolean checkForLeftPoint( XMLPadDocument document, int offset ) {
		try {
			int index = document.getDefaultRootElement().getElementIndex( offset );
			Element e = document.getDefaultRootElement().getElement( index );
			
			for ( int i = offset; i >=e.getStartOffset(); i-- ) {
				char c = document.getText( i, 1 ).charAt( 0 );
				if ( Character.isWhitespace( c ) )
					continue;
				else
					if ( c == ':' )
						return true;
			}
		} catch (BadLocationException e) {
			
		}
		return false;
	}
	
}
