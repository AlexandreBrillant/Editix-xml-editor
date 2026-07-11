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

package com.japisoft.xmlpad.helper.model;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * This helper manager all expression starting by <!
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
public class SystemHelper extends AbstractTagHelper {

	public SystemHelper() {
		this( false );
	}
	
	public SystemHelper( boolean empty ) {
		if ( !empty ) {
			addTagDescriptor( new TagDescriptor( "-- -->", null, true, true ) );
			addTagDescriptor( new TagDescriptor( "[CDATA[ ]]>", null, true, true ) );
			addTagDescriptor( new TagDescriptor( "DOCTYPE tag SYSTEM \"\">", null, true, true ) );			
		}
	}

	public TagDescriptor getTag(FPNode node) {
		return null;
	}	

	protected boolean addSystemTag() {
		return false;
	}
	
	public String getTitle() {
		return "XML";
	}
	
	protected String getLostCharacter() {
		return "!";
	}

}
