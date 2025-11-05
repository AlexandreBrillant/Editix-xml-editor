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

package com.japisoft.editix.editor.js.helper;

import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class ParametersHandler extends AbstractHelperHandler {
	
	public ParametersHandler() {
	}
	
	private List<String> parameters = null;
	
	@Override
	protected void installDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			int offset, 
			String activatorString ) {
		if ( parameters != null ) {
			Icon ii = new ImageIcon( getClass().getResource( "parameter" + ".png" ) );
			for ( String p : parameters ) {
				( ( BasicDescriptor )addDescriptor( new BasicDescriptor( p, ii ) ) ).setComment( "parameter" );
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
			int line = document.getDefaultRootElement().getElementIndex( offset );
			int cpt = 0;
			for ( int i = line; i >= 0; i-- ) {
				Element e = document.getDefaultRootElement().getElement( i );
				int start = e.getStartOffset();
				int end = e.getEndOffset();
				if ( i == line ) {
					start = offset;
				}
				try {		
					parameters = null;
					for ( int j = start; j < end; j++ ) {
						if ( "{".equals( document.getText( j, 1 ) ) ) {
							cpt++;
						} else
							if ( "}".equals( document.getText( j, 1 ) ) ) {
								cpt--;
							}
					}
					String l = document.getText( start, ( end - start ) );
					if ( l.contains( "function" ) && cpt != 0 ) {
						int a = l.indexOf( "function" );
						int b = l.indexOf( "(", a + 1 );
						int c = l.indexOf( ")", a + 1 );
						if ( ( a > -1 ) && ( b > -1 ) && ( c > -1 ) ) {
							String parametersLine = l.substring( b + 1, c ).trim();
							if ( parametersLine.length() > 0 ) {
								String[] tmp = parametersLine.split( "," );
								if ( tmp.length > 0 ) {
									for ( int d = 0; d < tmp.length; d++ ) {
										tmp[ d ].trim();
									}
									parameters = Arrays.asList( tmp );
									return true;
								}
							}
						}
						return false;
					}
				} catch( BadLocationException ble ) {
				}
			}
			return false;
		} else
			return false;
	}

}

