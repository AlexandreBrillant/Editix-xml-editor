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

package com.japisoft.datasource.filesystem;

import java.io.File;

import com.japisoft.datasource.DataSource;

public class FileSystemDataSource implements DataSource {

	protected File f = null;
	
	public FileSystemDataSource( File f ) {
		this.f = f;
	}
	
	public FileSystemDataSource( String f ) {
		this( new File( f ) );
	}

	public String getPath() {
		return f.toString();
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

