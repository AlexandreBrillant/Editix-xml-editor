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

package com.japisoft.editix.ui;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.application.descriptor.InterfaceBuilderException;
import com.japisoft.framework.application.descriptor.helpers.MenuBuilderDelegate;
import com.japisoft.framework.application.descriptor.helpers.Savable;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.IXMLPanel;

public class CommonRecentFileMenuBuilder implements MenuBuilderDelegate, Savable {

	private String menuId = null;
	
	public CommonRecentFileMenuBuilder( String menuId ) {
		this.menuId = menuId;
	}

	private File getMenuFilePath() {
		return new File( EditixApplicationModel.getAppUserPath(), menuId + ".xml" );
	}
	
	private JMenu startingMenu = null;

	public void build( JMenu menu ) {
		this.startingMenu = menu;
		File f = getMenuFilePath();
		if ( f.exists() ) {
			InterfaceBuilder ib = new InterfaceBuilder();
			try {
				ib.buildUI( new FileInputStream( f ), null );
				ArrayList al = ib.getModel( menuId );
				if ( al != null ) {
					for ( int i = 0; i < al.size(); i++ ) {
						Action a = ( Action )al.get( i );
						try {
							String param2 = ( String )a.getValue( "param2" );
							if ( param2 == null )
								param2 = "XML";
							Icon icon =	DocumentModel.getDocumentForType( param2 ).getDocumentIcon();
							a.putValue( Action.SMALL_ICON, icon );
						} catch (Throwable e) {
							e.printStackTrace();
						}
						JMenuItem item = menu.add( a );
						String param = ( String )a.getValue( "param" );
						if ( !param.contains( "://" ) ) {
							File ff = new File( param );
							if ( !ff.exists() )
								item.setForeground( Color.RED );
						}
					}
				}
				menu.setEnabled( al != null && al.size() > 0 );
			} catch ( Throwable e ) {
				System.out.println( "The descriptor " + f + " has been corrupted ??" + e.getMessage() );
				f.delete();
			}
		}
	}

	public void save() throws Exception {

		if ( startingMenu == null ) {
			System.out.println( "Can't find the Menu " + menuId + " ?" );
			return;
		}

		int historySize = Preferences.getPreference( 
			"interface", 
			"fileHistoryStack", 
			15 
		);

		ArrayList al = new ArrayList();
		for  (int i = 0;i < Math.min( startingMenu.getItemCount(), historySize ); i++ ) {
			JMenuItem item = startingMenu.getItem( i );
			Action a = item.getAction();
			
			String filePath = ( String )a.getValue( Action.NAME );
			IXMLPanel panel = EditixFrame.THIS.getContainerByFilePath( filePath );
			if ( panel == null )
				continue;	// ??
			Iterator<String> it = panel.getProperties();
			StringBuffer sb = new StringBuffer();
			while ( it.hasNext() ) {
				String key = it.next();
				if  ( panel.getProperty( key ) instanceof String ) {
					if ( sb.length() > 0 ) {
						sb.append( ";" );
					}
					sb.append( key ).append( "=" ).append( panel.getProperty( key ) );
				}
			}
			a.putValue( "param4", sb.toString() );

			al.add( a );
		}

		InterfaceBuilder ib = new InterfaceBuilder();
		FPNode node = ib.createModel( menuId, al );

		FPNode root = new FPNode( FPNode.TAG_NODE, "root" );
		root.appendChild( node );
		Document d = new Document();
		d.setRoot( root );
		d.write( new FileOutputStream( getMenuFilePath() ) );

	}
	
}

