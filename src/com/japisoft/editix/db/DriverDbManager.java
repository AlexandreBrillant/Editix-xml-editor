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

package com.japisoft.editix.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.japisoft.editix.db.xmldb.XmlDbDriver;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;

public class DriverDbManager {

	private static ArrayList drivers = null;

	public static int getDriverCount() {
		install();
		if ( drivers == null )
			return 0;
		return drivers.size();

	}
	
	public static Driver getDriver( int index ) {
		if ( drivers == null )
			return null;
		return ( Driver )drivers.get( index );
	}
	
	public static void removeDriver( Driver d ) {
		if  ( drivers != null )
			drivers.remove( d );
	}
	
	public static void addDriver( Driver d ) {
		if ( drivers != null )
			drivers.add( d );
	}

	public static Driver getDriverByName( String name ) {
		install();
		for ( int i = 0; i < getDriverCount(); i++ ) {
			if ( getDriver(  i ).toString().equalsIgnoreCase( name ) )
				return getDriver( i );
		}
		return null;
	}
	
	private static boolean isInstalled = false;
	
	public static void save() {
		File currentOne = new File( 
				EditixApplicationModel.getAppUserPath(),  
				"dbdrivers.xml" );

		StringBuffer sb = new StringBuffer();
		sb.append( "<drivers>" );

		for ( int i = 0; i < DriverDbManager.getDriverCount(); i++ ) {
			sb.append( DriverDbManager.getDriver( i ).toXml() );
		}

		sb.append( "</drivers>" );
		try {
			FileWriter fw = new FileWriter( currentOne );
			try {
				fw.write( sb.toString() );
			} finally {
				fw.close();
			}
		} catch (IOException e) {
		}
	}

	public static void install() {
	
		if ( isInstalled )
			return;

		isInstalled = true;

		try {
			InputStream input = null;
			
			File currentOne = new File( EditixApplicationModel.getAppUserPath(),  "dbdrivers.xml" ); 
			if ( !currentOne.exists() ) {
				input = 
					ClassLoader.getSystemClassLoader().getResourceAsStream( 
					"dbdrivers/drivers.xml" );
				if ( input == null )
					input = 
						ClassLoader.getSystemClassLoader().getResourceAsStream( "drivers.xml" );
			}
			else
				input = new FileInputStream( currentOne );
			
			if ( input == null ) {
				System.out.println( "Can't find drivers.xml" );
				return;
			}

			FPParser p = new FPParser();
			Document doc = p.parse(input);
			FPNode root = ( FPNode )doc.getRoot();
			for ( int i = 0; i < root.childCount(); i++ ) {
				FPNode c = root.childAt( i );
				if ( c.matchContent( "driver" ) ) {
					Driver d = null;
					
					String driverClass = c.getAttribute( "class" );
					if ( driverClass != null ) {

						try {
							ApplicationModel.debug( "Loading " + driverClass );
							
							if ( "com.japisoft.editix.db.xmldb.XmlDbDriver".equals( driverClass ) ) {
								d = new XmlDbDriver( c );
							} else
								d = ( Driver )Class.forName( driverClass ).getConstructors()[ 0 ].newInstance( new Object[] { c } );

							if ( drivers == null )
								drivers = new ArrayList();
							drivers.add( d );
						} catch ( Exception e ) {
							ApplicationModel.debug( e );
						}
 					} else
 						System.out.println( "Can't find driver class ???" );
				}
			}

		} catch (Exception e) {
			ApplicationModel.debug( e );
		}

	}

}

