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

import javax.swing.Icon;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.model.AbstractDescriptor;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

/** Assistant for comment */
public class CommentSystemHandler extends AbstractSystemHandler {
	
	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {

		// Complete content from the current text sequence
		
		( ( AbstractDescriptor )addDescriptor( 
				new TagDescriptor( 
						addedString + "-- -->", 
						null, 
						true, 
						true ) ) ).setComment( "XML comment" );

	}
	
	protected String getActivatorSequence() {
		return "<!";
	}	

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset, 
			String activatorString ) {

		if ( activatorString != null && 
				!"!".equals( activatorString ) )
			return false;

		if ( document.isInsideTag( offset ) )
			return false;
		
		return match( 
				document, 
				offset, 
				activatorString, 
				"<!" );
	}

	protected Icon getDefaultIcon() {
		return SharedProperties.getDefaultSystemHelperIcon();
	}

}

