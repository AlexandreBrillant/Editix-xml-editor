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

package com.japisoft.framework.xml.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import com.japisoft.framework.ApplicationModel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
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
