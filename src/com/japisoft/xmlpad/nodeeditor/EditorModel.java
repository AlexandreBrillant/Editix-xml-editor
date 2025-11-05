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

package com.japisoft.xmlpad.nodeeditor;

import java.util.ArrayList;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * This EditorModel stores editor for custom node edition.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public final class EditorModel {
	static ArrayList model;

	public static Editor DEFAULT_EDITOR = null;

	static {
		addEditor( DEFAULT_EDITOR = new DefaultEditor() );
	}

	/** Add an editor. The last added has always a priority to the older
	 * added when finding a good editor */	
	public static void addEditor( Editor editor ) {
		if ( model == null )
			model = new ArrayList();
		model.add( editor );
	}

	/** Remove an editor */
	public static void removeEditor( Editor editor ) {
		if ( model == null )
			return;
		model.remove( editor );		
	}

	/** @return an editor for this node. <code>null</code> is returned if
	 * no editor is available.
	 */
	public static Editor getEditorForNode( FPNode node ) {
		if ( model == null )
			return null;
		for ( int i = model.size() - 1; i >= 0; i-- ) {
			if ( ( ( Editor )model.get( i ) ).accept( node ) ) {
				return ( Editor )model.get( i );
			}
		}
		return null;
	}

	/** @return <code>true</code> if the node can be edited */
	public static boolean accept( FPNode node ) {
		if ( model == null )
			return false;
		for ( int i = model.size() - 1; i >= 0; i-- ) {
			Editor e = ( Editor )model.get( i );
			if ( e.accept( node ) )
				return true;
		}
		return false;
	}
}

