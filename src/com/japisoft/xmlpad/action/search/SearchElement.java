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

package com.japisoft.xmlpad.action.search;

import java.util.List;

import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.action.XMLAction;

/**
 * Search an element
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class SearchElement extends XMLAction {

	public static final String ID = SearchElement.class.getName();
	
	public static final int NEXT_TAG = 0;
	public static final int PREVIOUS_TAG = 1;
	public static final int TAG_START = 2;
	public static final int TAG_END = 3;
	
	private int type;

	public SearchElement( int type ) {
		this.type = type;
	}

	protected boolean autoRequestFocus() { return false; }

	public boolean notifyAction() {
		FPNode node = ( FPNode )container.getTree().getModel().getRoot();
		if ( node == null )
			return INVALID_ACTION;

		FPNode current = container.getCurrentNode(); 
		if ( current == null ) {
			switch( type ) {
				case PREVIOUS_TAG : return INVALID_ACTION;
				case NEXT_TAG : 
					highlightLine( node.getStartingLine() );
					return VALID_ACTION;
				case TAG_START :
				case TAG_END : 
					return INVALID_ACTION;
			}
		}

		Document doc = node.getDocument();
		List<FPNode> v = doc.getFlatNodes();
		int index = v.indexOf( current );

		switch( type ) {
			case PREVIOUS_TAG : 
				if ( index > 0 ) {
					FPNode n = ( FPNode )v.get( index - 1 );
					highlightLine( n.getStartingLine() );
					return VALID_ACTION;
				} else
					return INVALID_ACTION;
			case NEXT_TAG : 
				if ( index + 1 < v.size() ) {
					FPNode n = ( FPNode )v.get( index + 1 );
					highlightLine( n.getStartingLine() );
					return VALID_ACTION;
				} else
					return INVALID_ACTION;
			case TAG_START :
				highlightLine( current.getStartingLine() );
				return VALID_ACTION;
			case TAG_END :
				highlightLine( current.getStoppingLine() );
				return VALID_ACTION;
		}
		return VALID_ACTION;		
	}

	private void highlightLine( int line ) {
		container.getEditor().asynchronousHighlightLine( line );
	}

}
