package com.japisoft.editix.action.view;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.netbeans.swing.tabcontrol.TabbedContainer;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.AbstractDialogAction;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.xmlpad.IXMLPanel;
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
public class SelectDialog extends EditixDialog {
	private JButton btnSelect;

	public SelectDialog() {
		super( "Select editor", "Switch to editor", "Select an editor", DialogManager.buildNewActionModel() );
		init();
		actionModel.addDialogAction( new SelectionAction() );
	}
	
	private JTable t;

	private void init() {
		t = new ExportableTable() {
			public boolean editCellAt(int row, int column)  {
				return false;
			}
		};

		DefaultTableModel model = new DefaultTableModel( new
			String[] { "Document name", "Encoding", "Document path" }, 0 ) {
				public boolean isCellEditable(int row, int col)
					{ return false; }
			};

		TabbedContainer pane = EditixFrame.THIS.getMainTabbedPane();
		for ( int i = 0; i < pane.getTabCount(); i++ ) {
			XMLContainer container = ( ( IXMLPanel )pane.getComponentAt( i ) ).getMainContainer();
			if ( container == null )
				continue;
			model.addRow( new Object[] {
				container.getDocumentInfo().getDocumentName(),
				container.getProperty( "encoding", "DEFAULT" ),
				container.getDocumentInfo().getCurrentDocumentLocation()
			} );
		}
		t.setModel( model );

		t.getSelectionModel().setSelectionInterval( 
			EditixFrame.THIS.getMainTabbedPane().getSelectedIndex(),
			EditixFrame.THIS.getMainTabbedPane().getSelectedIndex() );

		//t.setEnabled( false );
		getContentPane().add( new JScrollPane( t ) );
		setSize( 450, 250 );
	}

	////////////////////////////////////////////////////////////
	
	class SelectionAction extends AbstractDialogAction {
		public SelectionAction() {
			super( 101, false );
			putValue( Action.NAME, "Select" );
		}
		public void actionPerformed( ActionEvent e ) {
			int selection = t.getSelectedRow();
			if ( selection != -1 )
				EditixFrame.THIS.getMainTabbedPane().setSelectedIndex( selection );					
		}
	}

}
