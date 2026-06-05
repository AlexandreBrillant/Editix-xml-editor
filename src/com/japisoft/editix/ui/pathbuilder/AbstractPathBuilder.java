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

package com.japisoft.editix.ui.pathbuilder;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.japisoft.editix.action.file.BrowseZIPAction;
import com.japisoft.editix.ui.panels.project2.ProjectManager;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.ui.text.PathBuilder;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.TreeWalker;

abstract class AbstractPathBuilder implements PathBuilder { 
	
	protected void completeForType( String type, List l ) {
		ProjectManager.getInstance().searchFilesForType( l, type );
	}
	
	private ArrayList l = null;
	
	private void addChoice( String path ) {
		if ( path != null && path.indexOf( "!" ) == -1 && !BrowseZIPAction.isFileArchive( path ) ) {
			if ( l == null )
				l = new ArrayList();
			if ( !l.contains( path ) ) {
				l.add( path );
			}
		}
	}

	public String[] buildPathsChoice() {
		String[] types = getTypes();
		addFromActiveOnes( types );
		if ( l == null )
			return null;
		else {
			String[] tmp = new String[ l.size() ];
			for ( int i = 0; i < l.size(); i++ )
				tmp[ i ] = ( String )l.get( i );
			return tmp;
		}
	}

	private void addFromActiveOnes( String[] types ) {
		// Search for opened
		for ( int i = 0; i < EditixFrame.THIS.getXMLContainerCount(); i++ ) {
			if ( EditixFrame.THIS.getXMLContainer( i ) == null )
				continue;
			for ( int j = 0; j < types.length; j++ ) {
				if ( types[ j ].equals(
						EditixFrame.THIS.getXMLContainer( i ).getDocumentInfo().getType() ) ) {
					addChoice( EditixFrame.THIS.getXMLContainer( i ).getCurrentDocumentLocation() );
				}
			}
		}
		// Search from project
		for ( int j = 0; j < types.length; j++ ) {
			ArrayList tempo = new ArrayList();
			completeForType( types[ j ], tempo );
			for ( int k = 0; k < tempo.size(); k++ )
				addChoice( ( String )tempo.get( k ) );
		}
		// Search from history
		InterfaceBuilder ib = EditixFrame.THIS.getBuilder();
		FPNode node = ib.getMenuNode( "openr" );
		if ( node != null ) {
			TreeWalker tw = new TreeWalker( node );
			Iterator enume = tw.getTagNodeByName( "ui", true );
			if ( enume != null ) {
				enume.next();	// Skip the menu ui
				while ( enume.hasNext() ) {
					FPNode uiNode = ( FPNode )enume.next();
					if ( uiNode.hasAttribute( "param2" ) ) {
						String type = uiNode.getAttribute( "param2" );
						for ( int j = 0; j < types.length; j++ ) {
							if ( type.equals( types[ j ] ) ) {
								if ( uiNode.hasAttribute( "param" ) ) {
									addChoice( uiNode.getAttribute( "param" ) );
								}
							}
						}
					}
				}
			}
		}
	}

	protected abstract String[] getTypes();
	
}

