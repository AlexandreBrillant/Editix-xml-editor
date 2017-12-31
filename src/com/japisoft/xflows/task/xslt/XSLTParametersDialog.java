package com.japisoft.xflows.task.xslt;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.BasicOKCancelDialogComponent;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xflows.task.TaskParams;

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
public class XSLTParametersDialog extends BasicOKCancelDialogComponent {

	private TaskParams params;
	private JTable tb;

	public XSLTParametersDialog( TaskParams params ) {
		super(
			ApplicationModel.MAIN_FRAME,
			"XSLT Option",
			"XSLT Parameters",
			"Choose a set of name and value for using it inside your stylesheet with the <param> elements", 
			null );

		this.params = params;
		tb = new JTable();
		DefaultTableModel model = new DefaultTableModel(
				new String[] { "Name", "Value" },
				0 );
		tb.setModel( model );
		JScrollPane sp = null;
		setUI( sp = new JScrollPane( tb ) );
		sp.setPreferredSize( new Dimension( 400, 500 ) );
	}

	protected void beforeClosing() {
		super.beforeClosing();
		TableModel model = tb.getModel();
		for ( int i = 0; i < model.getRowCount(); i++ ) {
			String name = ( String )model.getValueAt( i, 0 );
			String value = ( String )model.getValueAt( i, 1 );
			if ( "".equals( value ) )
				value = null;
			params.setParam( "param_" + name, value, TaskParams.XSLTPARAMS );
		}
	}
	
	protected void beforeShowing() {
		super.beforeShowing();
		for ( Iterator it = params.getParams(); it.hasNext(); ) {
			String name = ( String )it.next();
			if ( params.getParamType( name ) == TaskParams.XSLTPARAMS ) {
				if ( params.hasParamValue( name ) ) {
					( ( DefaultTableModel )tb.getModel() ).addRow(
							new Object[] { 
									name.substring( 6 ), 
									params.getParamValue( name ) } );
				}
			}
		}
		for ( int i = tb.getModel().getRowCount(); 
					i < Preferences.getPreference( "xslt", "parameter", 20 ); 
						i++ ) {
			((DefaultTableModel)tb.getModel()).addRow(
					new Object[] { "", "" } );
		}
	}

}
