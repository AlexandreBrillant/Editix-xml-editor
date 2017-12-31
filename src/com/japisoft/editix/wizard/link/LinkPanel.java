package com.japisoft.editix.wizard.link;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

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
public class LinkPanel extends JPanel implements TableModel {
	
	public LinkPanel() {
		this( false );
	}

	public LinkPanel( boolean innerMode ) {
		super();
		setLayout( new BorderLayout() );
		JTable t = null;
		add( new JScrollPane( t = new JTable( this ) ) );
		t.getColumnModel().getColumn( 0 ).setPreferredWidth( 20 );		
		if ( innerMode ) {
			XMLContainer container = EditixFrame.THIS.getSelectedContainer();
			FPNode root = container.getRootNode();
			if ( root != null ) {
				Document doc = root.getDocument();
				if ( doc != null ) {
					FastVector fv = doc.getFlatNodes();
					if ( fv != null ) {
						boolean foMode = "FO".equals( container.getDocumentInfo().getType() );;
						for ( int i = 0; i < fv.size(); i++ ) {
							FPNode n = ( FPNode )fv.get( i );
							
							boolean match = false;
							String name = null;
							
							if ( !foMode ) {
								match = n.matchContent( "a" );
								name = n.getAttribute( "name" );
							} else {
								if ( n.hasAttribute( "id" ) ) {
									match = true;
									name = n.getAttribute( "id" );
								}
							}

							if ( match ) {
								if ( name != null ) {
									String innerLink = name;
									String value = "link";
									// Use the next text
									for ( int j = i + 1 ; j < fv.size(); j++ ) {
										FPNode text = ( FPNode )fv.get( j );
										if ( text.isTag() && !text.isLeaf() ) {
											text = text.childAt( 0 );
										}
										if ( text.isText() ) {
											value = text.getContent();
											break;
										}
									}
									getLinks().add( new BasicLink(innerLink,value) );
								}
							}
						}
					}
				}
			}
		}
		getLinks().add( new BasicLink() );
	}

	private List<Link> links = null;

	public List<Link> getLinks() {
		if ( links == null )
			links = new ArrayList<Link>();
		return links;
	}

	private TableModelListener listener = null;
	
	public void addTableModelListener(TableModelListener l) {
		this.listener = l;
	}

	public Class<?> getColumnClass(int columnIndex) {
		if ( columnIndex == 0 )
			return Boolean.class;
		return String.class;
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int columnIndex) {
		switch( columnIndex ) {
			case 0 : return "Enabled";
			case 1 : return "URI";
			case 2 : return "Label";
		}
		return null;
	}

	public int getRowCount() {
		return getLinks().size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Link l = getLinks().get( rowIndex );

		switch( columnIndex ) {		
			case 0 : return l.isEnabled();
			case 1 : return l.getUri();
			case 2 : return l.getLabel();
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void removeTableModelListener(TableModelListener l) {
		this.listener = null;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		BasicLink bl = ( BasicLink )getLinks().get( rowIndex );
		if ( bl == null ) {
			bl = new BasicLink();
			getLinks().add( rowIndex, bl );
		}
		switch( columnIndex ) {
			case 0 : 
				bl.setEnabled( ( Boolean )aValue );
				break;
			case 1 : 
				bl.setUri( ( String )aValue );
				break;
			case 2 : 
				bl.setLabel( ( String )aValue );
				break;
		}
		
		// Force a new one		
		if ( rowIndex == getRowCount() - 1 ) {
			getLinks().add( new BasicLink() );
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							listener.tableChanged( new TableModelEvent(LinkPanel.this) );	
						}
					}
			);
		}		
	}

}
