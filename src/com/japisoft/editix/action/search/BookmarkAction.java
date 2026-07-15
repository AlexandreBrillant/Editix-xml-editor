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

package com.japisoft.editix.action.search;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.actions.SynchronizableAction;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class BookmarkAction extends AbstractAction implements SynchronizableAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		FPNode node = container.getCurrentNode();
		String stored = null;
		if ( node == null ) {
			// Cursor location
			stored = "Cursor at " + container.getEditor().getCaretPosition();
		} else
			stored = node.getXPathLocation();

		addBookmarkToMenu( stored );
		storeBookmarksInContainer( container );
	}

	public void synchronizeState(Object source) {
		if ( source instanceof XMLContainer ) {
			XMLContainer container = ( XMLContainer )source;
			String bookmarks = ( String )container.getProperty( "bookmarks", "" );
			EditixFrame.THIS.getBuilder().cleanMenuItems("listOfBookmarks" );
			StringTokenizer st = new StringTokenizer( bookmarks, "!!" );
			while ( st.hasMoreTokens() ) {
				addBookmarkToMenu( st.nextToken() );
			}
		}
	}

	public static void storeBookmarksInContainer( XMLContainer container ) {
		JMenu menu = EditixFrame.THIS.getBuilder().getMenu( "listOfBookmarks" );
		if ( menu == null ) {	//??
			System.err.println( "Can't find sub menu listOfBookmarks ??" );
		} else {
			StringBuffer mustStored = null;
			for ( int i = 0; i< menu.getItemCount(); i++ ) {
				JMenuItem item = menu.getItem( i );
				String lbl = item.getText();
				if ( mustStored == null )
					mustStored = new StringBuffer();
				else
					mustStored.append( "!!" );
				mustStored.append( lbl );
			}
			if ( mustStored != null )
				container.setProperty( "bookmarks", mustStored.toString() );
		}
	}

	public static void addBookmarkToMenu( String bookmark ) {
		BookmarkGoToAction bgt = new BookmarkGoToAction();
		bgt.putValue( Action.NAME, bookmark );
		bgt.putValue( "param", bookmark );
		bgt.putValue( "iconPath", "images/bookmark.png" );
		EditixFrame.THIS.getBuilder().insertMenuItemAtFirst( "listOfBookmarks", bgt, 20 );		
	}

}
