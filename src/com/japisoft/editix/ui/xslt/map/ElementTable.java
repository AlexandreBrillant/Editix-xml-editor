package com.japisoft.editix.ui.xslt.map;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.japisoft.framework.ui.table.ExportableTable;

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
public class ElementTable extends ExportableTable {
	
	public void setElement( Element e ) {
		setModel( new ElementModel( e ) );
	}

	private ElementTableListener etl;

	public void setElementTableListener( ElementTableListener etl ) {
		this.etl = etl;
	}

	void fireElementTableUpdated() {
		if ( this.etl != null )
			this.etl.tableUpdated();
	}
	
	class ElementModel implements TableModel {

		private Element e;
		
		ElementModel( Element e ) {
			this.e = e;
		}
		
		private TableModelListener l;
		
		public void addTableModelListener( TableModelListener l ) {
			this.l = l;
		}

		public void removeTableModelListener( TableModelListener l ) {
			this.l = null;
		}

		public Class<?> getColumnClass( int columnIndex ) {
			return String.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName( int columnIndex ) {
			if ( columnIndex == 0 )
				return "Attribute name";
			return "Attribute value";
		}

		public int getRowCount() {
			NamedNodeMap nnm = e.getAttributes();
			if ( nnm == null )
				return 1;
			return nnm.getLength() + 1;
		}

		public Object getValueAt( 
				int rowIndex, 
				int columnIndex ) {
			NamedNodeMap nnm = e.getAttributes();
			
			if ( rowIndex == nnm.getLength() ) {
				return "";
			}
			
			Attr a = ( Attr )nnm.item(rowIndex);
			if ( columnIndex == 0 )
				return a.getNodeName();
			return a.getNodeValue();
		}

		public boolean isCellEditable( 
				int rowIndex, 
				int columnIndex
		) {
			return true;
		}

		public void setValueAt(
				Object aValue, 
				int rowIndex, 
				int columnIndex ) {
			NamedNodeMap nnm = e.getAttributes();
			
			if ( rowIndex == nnm.getLength() ) {
				
				if ( columnIndex == 0 ) {
					e.setAttribute( ( String )aValue, "" );
					
					if ( l != null ) {
						l.tableChanged( new TableModelEvent( this ) );
					}
				}
				
			} else {
			
				Attr a = ( Attr )nnm.item(rowIndex);
				String name = a.getNodeName();
				
				if ( columnIndex == 1 ) {
					a.setNodeValue( ( String )aValue );
				} else {
					
					e.removeAttributeNode( a );
					if ( !"".equals( aValue ) ) {
						e.setAttribute( 
							name, 
							( String )aValue 
						);
					} else {
						
						if ( l != null ) {
							l.tableChanged( new TableModelEvent( this ) );
						}						

					}
					
				}
			
			}
			
			fireElementTableUpdated();
		}

	}

}
