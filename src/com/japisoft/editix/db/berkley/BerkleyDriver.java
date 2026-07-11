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

package com.japisoft.editix.db.berkley;

import java.io.File;

import com.japisoft.editix.db.Driver;
import com.japisoft.editix.db.RootNodeDb;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlManager;

public class BerkleyDriver extends Driver {

	public BerkleyDriver( FPNode node ) {
		super( node );
	}	
	
	public RootNodeDb getRoot(String url, String user, String password)
			throws Exception {

		File f = new File( url );
		if ( !f.exists() ) {
			throw new Exception( "Cannot find a container/you may try the 'test' button" );
		}

		BerkleyRootDbImpl root = new BerkleyRootDbImpl( toString(), url );
		
		return root;
	}

	public boolean test(String url, String user, String password) {
		
		File f = new File( url );
		if ( !f.exists() ) {

			if ( EditixFactory.buildAndShowConfirmDialog( "This container doesn't exist, do you want to create it ?" ) ) {
				
				try {
					XmlManager m = new XmlManager();
					try {
						XmlContainer container = m.createContainer( url );
						if ( container != null ) {
							EditixFactory.buildAndShowInformationDialog( "Container created" );
						}
					} finally {
						m.close();
					}
					
				} catch ( Exception e ) {

					EditixFactory.buildAndShowErrorDialog( "Can't build : " + e.getMessage() );
					return false;
					
				}

			}
			
		}
			
		return true;

	}
	
}
