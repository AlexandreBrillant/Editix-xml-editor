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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;

import com.japisoft.datasource.DataSource;
import com.japisoft.datasource.DataSourceContainer;
import com.japisoft.datasource.DataSourceItem;

public class FTPDataSourceContainer extends FTPDataSource implements DataSourceContainer {

	public FTPDataSourceContainer( String host, String user, String password, String path ) {
		super( host, user, password, path );
	}

	public FTPDataSourceContainer( String uri ) {
		super( uri );
	}
	
	public FTPDataSourceContainer( FileObject fo ) {
		super( fo );
	}
	
	FileObject connect() throws Exception {
		if ( fo == null ) {		
			FileSystemOptions opts = null;
			if ( user != null ) {
				StaticUserAuthenticator auth = new StaticUserAuthenticator( null, user, password ); 
				opts = new FileSystemOptions(); 
				try {
					DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator( opts, auth );
				} catch (FileSystemException e) {
				}
			}
			FileSystemManager fsManager = VFS.getManager();
			fo = fsManager.resolveFile( "ftp://" + host + "/" + path, opts );
		}
		return fo;
	}

	public List<DataSource> list() throws Exception {
		connect();
		FileObject[] children = fo.getChildren();
		ArrayList<DataSource> res = new ArrayList<DataSource>();
		if ( children != null ) {
			for ( FileObject fo : children ) {
				if ( fo.getType() == FileType.FILE ) {
					res.add( new FTPDataSourceItem( fo ) );
				} else
				if ( fo.getType() == FileType.FOLDER ) {
					res.add( new FTPDataSourceContainer( fo ) );
				}
			}
		}
		return res;
	}

	public DataSourceItem createItem(String name) throws Exception {
		connect();
		FileObject fo2 = fo.resolveFile( name );
		fo2.createFile();
		return new FTPDataSourceItem( fo2 );
	}
	
	public DataSourceContainer createContainer(String name) throws Exception {
		connect();
		FileObject fo2 = fo.resolveFile( name );
		fo2.createFolder();
		return new FTPDataSourceContainer( fo2 );
	}

}
