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
import com.japisoft.editix.db.FileNodeDb;
import com.japisoft.editix.db.ui.Editable;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;

public class BerkleyFileNodeImpl extends AbstractNodeDb implements FileNodeDb, Editable {

	private XmlDocument doc;

	public BerkleyFileNodeImpl( XmlDocument doc ) {
		this.doc = doc;
	}

	public BerkleyFileNodeImpl( XmlDocument doc, boolean editable ) {
		this.doc = doc;
		setEdit( editable );
	}

	public String getContent() throws Exception {
		// return doc.getContentAsString();
		
		BerkleyContainerDbImpl parent = ( BerkleyContainerDbImpl )getParent();
		return parent.getContent( name );
	}

	public boolean delete() throws Exception {

		BerkleyContainerDbImpl parent = ( BerkleyContainerDbImpl )getParent();
		if ( parent.delete( doc.getName() ) ) {
			super.delete();
			return true;
		} else
			return false;
		
	}

	public void setContent(String content) throws Exception {
		
		BerkleyContainerDbImpl parent = ( BerkleyContainerDbImpl )getParent();
		doc.setContent( content );
		parent.setContent( doc );
		
	}

	protected void prepareName() {
		try {
			this.name = doc.getName();
		} catch (XmlException e) {
			this.name = "error";
		}
	}	
	
	private boolean editable = true;
	
	public void setEdit( boolean editable ) {
		this.editable = editable;
	}
	
	public boolean canEdit() {
		return editable;
	}		
	
	public void refresh() throws Exception {
	}

}

