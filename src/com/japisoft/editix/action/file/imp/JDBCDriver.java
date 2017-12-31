package com.japisoft.editix.action.file.imp;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
public class JDBCDriver {

	private String name;
	private String driverClass;
	private String defaultUrl;
	private String[] urls;
	
	public static JDBCDriver ODBC_DRIVER = null;
	
	static {
		ODBC_DRIVER = new JDBCDriver( "ODBC" );
		// <db name="ODBC" driver="sun.jdbc.odbc.JdbcOdbcDriver" url="jdbc:odbc:<data-source-name>"/>
		ODBC_DRIVER.setDriverClass( "sun.jdbc.odbc.JdbcOdbcDriver" );
		ODBC_DRIVER.setDefaultUrl( "jdbc:odbc:<data-source-name>" );
	}

	public JDBCDriver( String name ) {
		if ( name == null ) {
			this.name = "new driver";
		} else
			this.name = name;
	}
	
	public JDBCDriver( Element e ) {
		fromXML( e );
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDriverClass() {
		return driverClass;
	}
	
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	
	public String getDefaultUrl() {
		return defaultUrl;
	}
	
	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}
	
	public String[] getUrls() {
		return urls;
	}
	
	public void setUrls(String[] urls) {
		this.urls = urls;
	}
	
	public String getFlatUrls() {
		if ( urls != null ) {
			String tmp = "";
			for ( String u : urls ) {
				if ( !"".equals( tmp ) )
					tmp += ";";
				tmp += u;
			
			}
			return tmp;
		} else
			return null;
	}

	public void setFlatUrls( String urls ) {
		setUrls( 
			urls.split( ";" ) 
		);		
	}

	public void fromXML( Element e ) {
		setName( e.getAttribute( "name" ) );
		setDriverClass( e.getAttribute( "driverClass" ) );
		setDefaultUrl( e.getAttribute( "defaultUrl" ) );
		String urls = e.getAttribute( "libraries" );
		setFlatUrls( urls );
	}

	public Element toXML( Document doc ) {
		Element e = doc.createElement( "driver" );
		e.setAttribute( "name", name );
		e.setAttribute( "driverClass", driverClass );
		e.setAttribute( "defaultUrl", defaultUrl );
		if ( urls != null ) {
			e.setAttribute( "libraries", getFlatUrls() );
		}
		return e;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public Connection getConnection( String url, String user, String password ) throws Exception {
		String drivers[] = getUrls();
		if ( drivers != null && drivers.length >= 0 ) {
			URL[] urls = new URL[ drivers.length ];
			for ( int i = 0; i < drivers.length; i++ )
				urls[ i ] = new File( drivers[ i ] ).toURL();

			URLClassLoader loader = new URLClassLoader( urls );

			Driver d = ( Driver )Class.forName( getDriverClass(), true, loader ).newInstance();
			DriverManager.registerDriver( new DriverShim( d ) );			
		} else
			Class.forName( getDriverClass() );

		return DriverManager.getConnection( url, user, password );
	}

	static class DriverShim implements Driver {
		private Driver driver;
		DriverShim(Driver d) {
			this.driver = d;
		}
		public boolean acceptsURL(String u) throws SQLException {
			return this.driver.acceptsURL(u);
		}
		public Connection connect(String u, Properties p) throws SQLException {
			return this.driver.connect(u, p);
		}
		public int getMajorVersion() {
			return this.driver.getMajorVersion();
		}
		public int getMinorVersion() {
			return this.driver.getMinorVersion();
		}
		public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
			return this.driver.getPropertyInfo(u, p);
		}
		public boolean jdbcCompliant() {
			return this.driver.jdbcCompliant();
		}
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}
	}	
	
	
}
