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

package com.japisoft.editix.db.berkley;

import java.util.ArrayList;

import com.japisoft.editix.db.NodeDb;
import com.japisoft.editix.db.RootNodeDb;
import com.japisoft.editix.ui.EditixFactory;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;

public class BerkleyRootDbImpl extends BerkleyContainerDbImpl implements RootNodeDb {

	private XmlManager m;
	
	public BerkleyRootDbImpl( 
			String driverName, 
			String location ) throws Exception {	
		super( null );
		this.driverName = driverName;
		this.url = location;
	}

	public NodeDb[] request( String query ) throws Exception {
		
		NodeDb[] ns = null;
		
		XmlQueryContext context = m.createQueryContext();
		XmlQueryExpression qe = m.prepare( query, context );
		XmlResults res = qe.execute(context); 

		if ( res != null ) {
			ArrayList l = new ArrayList();

			BerkleyRootDbImpl newRoot = new BerkleyRootDbImpl( driverName, url );
			newRoot.m = m;
			newRoot.container = container;
			
			while ( res.hasNext() ) {
				XmlValue val = res.next();

				if ( val.isNode() ) {
										
					XmlDocument doc = val.asDocument();
					// Avoid a clone
					
					doc = container.getDocument( doc.getName() );
					
					newRoot.addChild( new BerkleyFileNodeImpl( doc ) );
					
					// Can't edit it
					l.add( newRoot );
				} else
					EditixFactory.buildAndShowInformationDialog( val.asString() );
			}

			ns = new NodeDb[ l.size() ];
			for ( int i = 0; i < l.size(); i++ )
				ns[ i ] = ( NodeDb )l.get( i );
	
			// res.delete();
		}
		qe.delete();
		context.delete();
		
		return ns;
	}

	public boolean canConnect() {
		return ( container != null );
	}

	public void open() throws Exception {
		// Only once
		if ( m == null )
			m = new XmlManager();
		container = m.openContainer( url );
		
		int i = url.lastIndexOf( "/" );
		if ( i == -1 )
			i = url.lastIndexOf( "\\" );
		String alias = url.substring( i + 1 );
		container.addAlias( alias );
		
		int j = alias.lastIndexOf( "." );
		if ( j > -1 )
			container.addAlias( alias.substring( 0, j ) );

		super.open();
	}	

	public void close() {
		super.close();
		try {
			// Don't close otherwise crash
			// m.close();
		} catch (Throwable e) {
		}
	}

	protected void prepareName() {
		int i = url.lastIndexOf( "/" );
		if ( i == -1 )
			i = url.lastIndexOf( "\\" );
		name = "berkley-" + url.substring( i + 1 );
	}

	public String toXml() {
		return "<connection driver='" + driverName + "' url='" + url + "' embedded='true'/>";
	}

}

