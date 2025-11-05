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

package com.japisoft.editix.db.xmldb;

import com.japisoft.editix.db.AbstractNodeDb;
import com.japisoft.editix.db.ContainerNodeDb;
import com.japisoft.editix.db.FileNodeDb;

public class XmlDbFileNodeDbImpl extends AbstractNodeDb 
	implements 
		FileNodeDb {

	private String id;
	
	public XmlDbFileNodeDbImpl( ContainerNodeDb parent, String id ) {
		this.parent = parent;
		this.id = id;
	}

	public boolean delete() throws Exception {
		( ( XmlDbContainerDbImpl )getParent() ).delete( id );
		return true;
	}

	public boolean canBeDeleted() {
		return true;
	}
	
	public String getContent() throws Exception {
		return ( ( ContainerNodeDb )getParent() ).getContent( id );
	}

	public void setContent(String content) throws Exception {
		( ( ContainerNodeDb )getParent() ).setContent( id, content );		
	}

	public String toString() {
		return id;
	}

	public void refresh() throws Exception {
	}
}

