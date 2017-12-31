package com.japisoft.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
public class DataSourceContainerProxy implements DataSourceContainer {

	private DataSourceContainer container;
	private Pattern p = null;
	
	public DataSourceContainerProxy( DataSourceContainer container, String filterRegexp ) {
		this.container = container;
		p = Pattern.compile( filterRegexp );
	}

	public DataSourceContainer createContainer(String name) throws Exception {
		return container.createContainer( name );
	}
	
	public DataSourceItem createItem(String name) throws Exception {
		return container.createItem( name );
	}
	
	public String getPath() {
		return container.getPath();
	}

	public List<DataSource> list() throws Exception {
		ArrayList<DataSource> res = new ArrayList<DataSource>();
		List<DataSource> proxyList = container.list();
		for ( DataSource ds : proxyList ) {
			if ( p.matcher( ds.getName() ).matches() ) {
				res.add( ds );
			}
		}
		return res;
	}
	
	public String getName() {
		return container.getName();
	}

}
