package com.japisoft.xmlpad.nodeeditor;

import java.util.ArrayList;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

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
