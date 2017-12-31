package com.japisoft.editix.action.file.imp;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

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
