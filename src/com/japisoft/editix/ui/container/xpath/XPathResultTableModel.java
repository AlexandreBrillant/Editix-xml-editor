package com.japisoft.editix.ui.container.xpath;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
public class XPathResultTableModel implements TableModel {

	private XPathEditorModel query;
	private Document doc;
	
	public XPathResultTableModel( 
		Document doc, 
		XPathEditorModel query ) {
		this.doc = doc;
		this.query = query;
	}

	public XPathEditorModel getXPathEditorModel() {
		return query;
	}
	
	public Document getSource() {
		return doc;
	}
	
	public void dispose() {
		this.query = null;
		this.doc = null;
		this.results = null;
		fireTableUpdate();
	}

	public void setErrorFound( String errorMessage ) {}

	private List<XPathRunnerResultObject[]> results = null;
	
	public void addResult( XPathRunnerResultObject[] objects ) {
		if ( results == null ) {
			results = new ArrayList<XPathRunnerResultObject[]>();
		}
		results.add( objects );
	}

	public void run() {}
	public void stop() {}

	private TableModelListener l;
	
	private TableModelEvent uniqueEvent = new TableModelEvent( this );
	
	void fireTableUpdate() {
		l.tableChanged( uniqueEvent );
	}
	
	public void addTableModelListener(TableModelListener l) {
		this.l = l;
	}

	public void removeTableModelListener(TableModelListener l) {
		this.l = null;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return query.getColumnCount();
	}

	public String getColumnName(int columnIndex) {
		return query.getColumn( columnIndex ).getName();
	}

	public int getRowCount() {
		if ( results == null )
			return 0;
		return results.size();
	}
	
	private boolean modified = false;

	public void setValueAt(
		Object aValue, 
		int rowIndex, 
		int columnIndex ) {
		XPathRunnerResultObject[] obj = results.get( rowIndex );
		obj[ columnIndex ].setValue( ( String )aValue );
		this.modified = true;
	}

	public boolean isModified() {
		return modified;
	}
	
	public void setModified( boolean modified ) {
		this.modified = modified;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		XPathRunnerResultObject[] obj = results.get( rowIndex );
		return obj[ columnIndex ].getValue();
	}
	
	public Node getNodeAt( int rowIndex ) {
		XPathRunnerResultObject[] obj = results.get( rowIndex );
		return obj[ 0 ].getSourceNode();
	}

	public void removeNodeAt( int rowIndex ) {
		Node n = getNodeAt( rowIndex );
		n.getParentNode().removeChild( n );
		results.remove( rowIndex );
		this.modified = true;
		fireTableUpdate();
	}
	
	public void addNodeAt( int rowIndex, Node copy ) throws Exception {
		Node currentNode = getNodeAt( rowIndex );
		Node newNode = copy.cloneNode( true );
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathRunnerResultObject[] objs = XPathRunner.getResultObject( getXPathEditorModel(), xpath, newNode );
		results.add( rowIndex, objs );
		currentNode.getParentNode().insertBefore( newNode,currentNode );
		fireTableUpdate();
		this.modified = true;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

}
