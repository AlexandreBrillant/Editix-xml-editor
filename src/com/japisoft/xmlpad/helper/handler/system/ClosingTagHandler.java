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

package com.japisoft.xmlpad.helper.handler.system;

import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

/** Assistant when closing a tag, it will open the previous opening tag */
public class ClosingTagHandler extends AbstractSystemHandler {

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {
		try {
			String closingTag = document.getPreviousOpeningTag( offset - 2 );
			if ( closingTag != null )
				addDescriptor(
					new BasicDescriptor( "/" + closingTag + ">�" ) );
		} catch ( BadLocationException e ) {
		}
	}

	protected String getActivatorSequence() {
		return "</";
	}	
	
	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document,
			boolean insertBefore, 
			int offset, 
			String activatorString ) {
		if ( activatorString == null )
			return false;
		if ( "/".equals( activatorString ) ) {
			try {
				// Avoid case with <toto> at the end
				String tagName = document.getNextClosingTag( offset );
				String closingTag = document.getPreviousOpeningTag( offset - 2 );
				if ( tagName == null || tagName.equals( closingTag ) )
					return false;
			} catch( BadLocationException e ) {	
			}
			return match(
					document, 
					offset,
					activatorString, 
					"</" );
		} else
			return false;
	}

}
