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

package com.japisoft.editix.db.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.japisoft.editix.db.Driver;
import com.japisoft.editix.db.DriverDbManager;
import com.japisoft.editix.db.NodeDb;
import com.japisoft.editix.db.RootNodeDb;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.leftpanels.AbstractLeftPanel;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;

public class DbPanel extends AbstractLeftPanel {
	
	protected JComponent buildView() {
		return new DbBrowser();
	}

	protected String getTitle() {
		return "Database browser";
	}

	public void stop() {
		// EditiX is stopped
		preHide();
	}
	
	protected void preShow() {
	}

	protected void preHide() {
		super.preHide();
		// Store the tree state

		TreeNode n = ( ( DbBrowser )getView() ).getRoot();
		StringBuffer sb = new StringBuffer( "<db>" );

		for ( int i = 0;i < n.getChildCount(); i++ ) {
			
			RootNodeDb nd = ( RootNodeDb )n.getChildAt( i );
			String connection = nd.toXml();
			if ( connection != null )
				sb.append( connection );
			
		}

		sb.append( "</db>" );
		File dbf = new File( EditixApplicationModel.getAppUserPath(), "db.xml" );
		try {
			FileWriter fw = new FileWriter( dbf );
			fw.write( sb.toString() );
			fw.close();
		} catch (IOException e) {
			EditixApplicationModel.debug( e );
		}

		( ( DbBrowser )getView() ).close();
	}	

	protected void postShow() {
		super.postShow();
		
		File dbf = new File( EditixApplicationModel.getAppUserPath(), "db.xml" );
		if ( dbf.exists() ) {
			// Restore the connection
			
			 DefaultMutableTreeNode root = ( ( DbBrowser )getView() ).getRoot();
			// <connection driver='" + driverName + "' url='" + url + "' user='" + user + "' password='" + password + "'/>
			try {
				FPParser p = new FPParser();
				Document d  = p.parse(new FileInputStream( 
						dbf ));
				FPNode node = ( FPNode )d.getRoot();
				StringBuffer errors = null;
				for ( int i = 0; i < node.childCount(); i++ ) {

					FPNode child = node.childAt( i );
					if ( child.matchContent( "connection" ) ) {
						String driverName = child.getAttribute( "driver" );
						String url = child.getAttribute( "url" );
						String user = child.getAttribute( "user" );
						String password = child.getAttribute( "password" );

						Driver dr = DriverDbManager.getDriverByName( driverName );
						if ( dr != null ) {
							try {
								NodeDb dbRoot = dr.getRoot( url, user, password );
								root.add( dbRoot );
							} catch (Exception e) {
								if ( errors == null )
									errors = new StringBuffer();
								if ( errors != null )
									errors.append( "\n" );
								errors.append( "Can't connect to " + url + " : " + e.getMessage() );
							}
						}
					}

				}

				( ( DbBrowser )getView() ).initTree();
				
				if ( errors != null ) {

					EditixFactory.buildAndShowErrorDialog( errors.toString() );
					
				}

			} catch (FileNotFoundException e) {
			} catch (ParseException e) {
			}

		}
	}
	
}

