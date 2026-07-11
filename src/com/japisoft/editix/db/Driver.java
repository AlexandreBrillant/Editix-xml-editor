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

package com.japisoft.editix.db;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.xml.parser.node.FPNode;

public abstract class Driver {
	protected String name;
	public String url;
	public String jars;
	public String dbxmlClass;

	public boolean canAddContainer = false;
	public boolean canRemoveContainer = false;
	public boolean query = false;
	public boolean embedded = false;
	public boolean system = false;
	
	public Driver( String name ) {
		this.name = name;
	}
	
	public Driver( 
			FPNode driverNode ) {
		this.name = driverNode.getAttribute( "name" );
		this.url = driverNode.getAttribute( "url" );
		this.jars = driverNode.getAttribute( "jars" );
		this.system = "true".equals( driverNode.getAttribute( "system" ) );
		this.dbxmlClass = driverNode.getAttribute( "xmldbDriver" );
		
		String home = System.getProperty( "user.home" );
		home = home.replace( '\\', '/' );

		this.url = this.url.replaceFirst( "\\$H", home );
		
		this.embedded = "true".equals( 
				driverNode.getAttribute( "embedded", "false" ) );

		this.canAddContainer = "true".equals( 
				driverNode.getAttribute( 
						"addc", 
							"false" ) );

		this.canRemoveContainer = "true".equals( 
				driverNode.getAttribute( 
						"removec", 
							"true" ) );
		
		this.query = "true".equals( 
				driverNode.getAttribute( 
						"query", 
							"false" ) );
		
	}

	private boolean install = false;

	public void install() {
		if ( !install ) {
			install = true;

			if ( jars != null ) {
			
				String[] drivers = jars.split( ";" );
				//URL root = ClassLoader.getSystemResource( "/dbdrivers" );
				URL[] res = new URL[ drivers.length ];

				for ( int i = 0;i < drivers.length; i++ ) {

					// Convert absolute path to URL
					File f = new File( drivers[ i ] );
					try {

						if ( f.exists() ) {
							res[ i ] = f.toURI().toURL();
						} else
							res[ i ] = new URL( drivers[ i ] );

					} catch ( MalformedURLException e ) {
						
						EditixApplicationModel.debug( e );

					}

				}

				URLClassLoader loader = new URLClassLoader( res );
				
				install( loader );
			
			} else
				
				install( ClassLoader.getSystemClassLoader() );
		}
	}

	protected void install( ClassLoader loader ) {
	}

	public boolean test( String url, String user, String password ) throws Exception {
		install();
		return false;
	}

	public String toString() {
		return name;
	}

	public abstract RootNodeDb getRoot( String url, String user, String password ) throws Exception;

	public String toXml() {
		String res = "<driver system='" + 
			system + 
			"' name='" + 
			name + 
			"' class='" + 
			getClass().getName() +
			"' url='" +
			url +
			"' xmldbDriver='" + 
			dbxmlClass + 
			"'";
		if ( jars != null ) {
			res += " jars='" + jars + "'"; 
		} 
		res +=
			" addc='" +
			canAddContainer +
			"' removec='" + 
			canRemoveContainer +
			"' query='" + 
			query + "'/>";
		return res;
	}

}
