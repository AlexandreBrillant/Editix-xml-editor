package com.japisoft.datasource.filesystem;

import java.io.File;

import com.japisoft.datasource.DataSource;

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
