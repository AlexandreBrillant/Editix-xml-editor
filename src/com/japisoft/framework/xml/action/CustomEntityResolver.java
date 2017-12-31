package com.japisoft.framework.xml.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import com.japisoft.framework.ApplicationModel;

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
public class CustomEntityResolver extends CatalogResolver {

	CatalogManager manager;

	public CustomEntityResolver( CatalogManager manager) {
		super( manager );
		this.manager = manager;
		loadCatalogs();
	}

	static CustomEntityResolver RESOLVER = null;

	public static CustomEntityResolver getInstance() {
		if ( RESOLVER == null ) {
			RESOLVER = new CustomEntityResolver( CatalogManager.getStaticManager() );
			CatalogManager.getStaticManager().setRelativeCatalogs( false );
		}
		return RESOLVER;
	}

	public void loadCatalogs() {
		File catalog = XMLCatalogAction.getCatalogLstPath();
		if ( catalog.exists() ) {
			try {
				BufferedReader reader = new BufferedReader( 
						new FileReader( catalog ) );
				try {
					String line = null;
					StringBuffer sb = new StringBuffer();
					while ( ( line = reader.readLine() ) != null ) {
						if ( sb.length() > 0 )
							sb.append( ";" );
						sb.append( line );
					}
					ApplicationModel.debug( "RESET CATALOG FILES = " + sb.toString() );
					manager.setCatalogFiles( sb.toString() );
				} finally {
					reader.close();
				}

			} catch( IOException exc ) {}
		}		
		try {
			manager.getCatalog().loadSystemCatalogs();
		} catch( IOException exc ) {
			exc.printStackTrace();
		}
	}
	
}
