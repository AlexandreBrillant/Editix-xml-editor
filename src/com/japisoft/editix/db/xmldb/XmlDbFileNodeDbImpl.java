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
