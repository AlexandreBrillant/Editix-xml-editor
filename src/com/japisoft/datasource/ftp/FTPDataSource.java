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

package com.japisoft.datasource.ftp;

import org.apache.commons.vfs.FileObject;

import com.japisoft.datasource.DataSource;

public class FTPDataSource implements DataSource {

	protected String host,user,password,path;
	protected FileObject fo = null;
	
	public FTPDataSource( 
			String host, 
			String user, 
			String password, 
			String path ) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.path = path;
	}
	
	public FTPDataSource(
		String uri ) {
		String tmp = uri;
		tmp = tmp.toLowerCase();
		if ( tmp.startsWith( "ftp://" ) ) {
			tmp = tmp.substring( 6 );
		}
		
		int i = tmp.lastIndexOf( "@" );
		if ( i > -1 ) {
			String userPasswordRaw = tmp.substring( 0, i );
			String[] userPassword = userPasswordRaw.split( ":" );
			if ( userPassword.length == 1 ) {
				user = userPassword[ 0 ];
			} else
			if ( userPassword.length == 2 ) {
				user = userPassword[ 0 ];
				password = userPassword[ 1 ];
			}
			tmp = tmp.substring( i + 1 );			
		}

		String[] t = tmp.split( "@" );
		if ( t.length == 2 ) {
		}
		i = tmp.indexOf( "/" );
		if ( i > -1 ) {
			try {
				path = tmp.substring( i + 1 );				
			} catch( IndexOutOfBoundsException exc ) {
				path = "";
			}
			tmp = tmp.substring( 0, i );
		}
		host = tmp;
	}

	public FTPDataSource( FileObject fo ) {
		this.fo = fo;
	}
	
	public String getPath() {
		if ( fo != null )
			return fo.getName().getPath();
		return path;
	}
	
	public String getName() {
		String tmp = getPath();
		if ( tmp.endsWith( "/" ) )
			tmp = tmp.substring( 0, tmp.length() - 1 );
		if ( tmp.endsWith( "\\" ) )
			tmp = tmp.substring( 0, tmp.length() - 1 );
		int i = tmp.lastIndexOf( "/" );
		if ( i == -1 )
			i = tmp.lastIndexOf( "\\" );
		if ( i > -1 )
			tmp = tmp.substring( i + 1 );
		return tmp;
	}	

}
