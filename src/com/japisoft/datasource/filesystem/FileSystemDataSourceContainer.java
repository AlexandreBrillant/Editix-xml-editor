package com.japisoft.datasource.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.japisoft.datasource.DataSource;
import com.japisoft.datasource.DataSourceContainer;
import com.japisoft.datasource.DataSourceItem;

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
public class FileSystemDataSourceContainer extends FileSystemDataSource
		implements DataSourceContainer {

	public FileSystemDataSourceContainer( File f ) {
		super( f );
	}

	public FileSystemDataSourceContainer( String f ) {
		super( f );
	}
	
	public DataSourceItem createItem( String name ) throws Exception {
		return new FileSystemDataSourceItem( new File( f, name ) );
	}
	
	public DataSourceContainer createContainer(String name) throws Exception {
		File ff = new File( f, name );
		ff.mkdir();
		return new FileSystemDataSourceContainer( ff );
	}

	public List<DataSource> list() throws Exception {
		ArrayList<DataSource> r = new ArrayList<DataSource>();
		String[] content = f.list();
		if ( content != null ) {
			for ( String c : content ) {
				File ff = new File( f, c );
				if ( ff.isDirectory() ) {
					r.add( new FileSystemDataSourceContainer( ff ) );
				} else
				if ( ff.isFile() ) {
					r.add( new FileSystemDataSourceItem( ff ) );
				}
			}
		}
		return r;
	}

}
