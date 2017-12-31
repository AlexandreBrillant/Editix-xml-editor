package com.japisoft.editix.action.xml;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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
public class CharRefAllTableModel implements TableModel {

	public int getRowCount() {
		return Character.MAX_VALUE / getColumnCount();
	}

	public int getColumnCount() {
		return 10;
	}

	public String getColumnName(int columnIndex) {
		return null;
	}

	public Class getColumnClass(int columnIndex) {
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return new Character( (char)( ( rowIndex * getColumnCount() ) + columnIndex ) );
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	public void addTableModelListener(TableModelListener l) {
	}

	public void removeTableModelListener(TableModelListener l) {
	}

}
