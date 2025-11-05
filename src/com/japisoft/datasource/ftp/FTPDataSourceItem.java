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

package com.japisoft.datasource.ftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import com.japisoft.datasource.DataSourceItem;

public class FTPDataSourceItem extends FTPDataSource implements DataSourceItem {

	public FTPDataSourceItem( FileObject fo ) {
		super( fo );
	}

	public void delete() {
		try {
			fo.delete();
		} catch( FileSystemException fse ) {
			
		}
	}

	public byte[] getContent() throws Exception {
		FileContent fc = fo.getContent();
		InputStream in = fc.getInputStream();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[ 1024 ];
		int c;
		while ( ( c = in.read( buffer ) ) > -1 ) {
			bout.write( buffer, 0, c );
		}
		return bout.toByteArray();
	}

	public void setContent(byte[] content) throws Exception {
		FileContent fc = fo.getContent();
		OutputStream out = fc.getOutputStream();
		out.write( content );
	}
	

	
}

