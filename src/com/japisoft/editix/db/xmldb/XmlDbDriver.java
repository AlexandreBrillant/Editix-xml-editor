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

package com.japisoft.editix.db.xmldb;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;

import com.japisoft.editix.db.Driver;
import com.japisoft.editix.db.RootNodeDb;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.xml.parser.node.FPNode;

public class XmlDbDriver extends Driver {

	// Specific to XML DB driver
	public String dbDriver;

	public XmlDbDriver( String name ) {
		super( name );
	}

	public XmlDbDriver( 
			FPNode driverNode ) {
		super( driverNode );
		this.dbDriver= driverNode.getAttribute( "xmldbDriver" );
	}

	public boolean test(String url, String user, String password) throws Exception {
		install();
		RootNodeDb root = getRoot( url, user, password );
		boolean ok = root.canConnect();
		if ( root != null ) {
			try {
				root.close();
			} catch( Exception e ) {
			}
		}
		return ok;
	}

	protected void install(ClassLoader loader) {
		try {
		
			if ( dbDriver != null )

				installXmlDbDriver( dbDriver, loader );

		} catch (Exception e) {

			EditixApplicationModel.debug( e );

		}
	}


	public static void installXmlDbDriver( String driver, ClassLoader loader ) throws Exception {
		
		Class cl = loader.loadClass( driver );  
        Database database = (Database) cl.newInstance();  
        DatabaseManager.registerDatabase(database);

	}	
	
	public RootNodeDb getRoot(String url, String user, String password) throws Exception {
		install();
		return new XmlDbRootDbImpl( toString(), url, user, password );
	}	

}

