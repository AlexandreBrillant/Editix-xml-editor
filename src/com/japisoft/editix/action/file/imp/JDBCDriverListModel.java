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

package com.japisoft.editix.action.file.imp;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class JDBCDriverListModel implements ListModel, ComboBoxModel, JDBCDriverModelListener {

	private JDBCDriverModel model = null;

	public JDBCDriverListModel( JDBCDriverModel model ) {
		this.model = model;
		this.model.setListener( this );
		if ( model.size() > 0 )
			item = model.getDriver( 0 );
	}
	
	public void modelUpdated() {
		if ( l != null ) {
			l.contentsChanged( 
				new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1 ) 
			);
		}
	}

	private ListDataListener l;
	
	public void addListDataListener(ListDataListener l) {
		this.l = l;
	}
	public void removeListDataListener(ListDataListener l) {
		this.l = null;
	}

	public Object getElementAt(int index) {
		return model.getDriver( index );
	}

	public int getSize() {
		return model.size();
	}
	
	public Object getSelectedItem() {
		return item;
	}

	private Object item;
	
	public void setSelectedItem(Object anItem) {
		this.item = anItem;
	}

}
