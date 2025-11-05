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

import com.japisoft.editix.db.AbstractNodeDb;
import com.japisoft.editix.db.ContainerNodeDb;
import com.japisoft.editix.db.NodeDb;
import com.japisoft.editix.ui.EditixFactory;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;

public class BerkleyContainerDbImpl extends AbstractNodeDb implements ContainerNodeDb {

	protected XmlContainer container;
	
	public BerkleyContainerDbImpl( XmlContainer container ) {
		this.container = container;
	}

	public boolean canCreateSubContainer() {
		return false;
	}

	public ContainerNodeDb createSubContainer(String id) throws Exception {
		return null;
	}

	public String getContent(String id) throws Exception {		
		return container.getDocument( id ).getContentAsString();
	}

	public boolean delete( String docId ) throws Exception {
		if ( EditixFactory.buildAndShowConfirmDialog( "This operation may crash EditiX due a problem with the berkley API, are you sure ?" ) ) {
			container.deleteDocument( docId );
			return true;
		} else
			return false;
	}

	public NodeDb[] request(String query ) throws Exception {
		return ( ( BerkleyRootDbImpl )getParent() ).request( query );
	}

	public void setContent(String id, String content) throws Exception {
		container.putDocument( id, content );
	}
	
	public void setContent( XmlDocument doc ) throws Exception {
		container.updateDocument( doc );
		container.sync();
	}

	public void open() throws Exception {
		XmlResults res = container.getAllDocuments( null );
		if ( res != null ) {
			while ( res.hasNext() ) {
				XmlValue val = res.next();			
				XmlDocument doc = val.asDocument();
				addChild( 
						new BerkleyFileNodeImpl( 
								doc ) );
			}
			// res.delete();
		}
	}	

	public void close() {
		try {
			container.close();
		} catch (XmlException e) {
		}
		super.close();
	}

	public void refresh() throws Exception {
		
	}

}

