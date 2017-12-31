package com.japisoft.xflows.task.imp.cvs;

import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.BasicOKCancelDialogComponent;
import com.japisoft.framework.xml.imp.CSVImportParams;
import com.japisoft.framework.xml.imp.CSVPanel;
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
public class CSVParametersDialog extends BasicOKCancelDialogComponent {

	private TaskParams params;
	private CSVPanel csvParams; 

	public CSVParametersDialog( TaskParams params ) {
		super(
			ApplicationModel.MAIN_FRAME,
			"CSV Option",
			"CSV Parameters",
			"Fix here your CSV format", 
			null );

		this.params = params;
		setUI( csvParams = new CSVPanel() );
		setSize( 400, 550, true );
	}

	protected void beforeClosing() {
		super.beforeClosing();
		if ( isOk() ) {

			CSVImportParams p = ( CSVImportParams )csvParams;
			
			// Store the result

			params.setParam( TAB_KEY, "" + p.isTabSelected() );
			params.setParam( SEMICOL_KEY, "" + p.isSemiColonSelected() );
			params.setParam( COMMA_KEY, "" + p.isCommaSelected() );
			params.setParam( SPACE_KEY, "" + p.isSpaceSelected() );
			params.setParam( OTHER_KEY, "" + p.isOtherSelected() ); 
			params.setParam( TEXTQUALIF_KEY, p.getTextQualifier() );
			params.setParam( STARTINGROW_KEY, "" + p.getStartingRow() );
			
			ListModel model = p.getColumnName();
			StringBuffer sb = new StringBuffer();
			for ( int i = 0; i < model.getSize(); i++ ) {
				if ( i > 0 )
					sb.append( "@@" );
				sb.append( model.getElementAt( i ) );
			}
			
			params.setParam( COLNAME_KEY, sb.toString() ); 
			params.setParam( ROWNAME_KEY, p.getRowName() );
			params.setParam( OTHERVAL_KEY, p.getOther() );
		}
	}

	protected void beforeShowing() {
		super.beforeShowing();
		
		csvParams.setTabSelected(
				params.getParamValueBoolean( TAB_KEY ) );
		
		csvParams.setSemiColonSelected(
				params.getParamValueBoolean( SEMICOL_KEY ) );
		
		csvParams.setCommaSelected(
				params.getParamValueBoolean( COMMA_KEY ) );
		
		csvParams.setSpaceSelected(
				params.getParamValueBoolean( SPACE_KEY ) );
		
		csvParams.setOtherSelected(
				params.getParamValueBoolean( OTHER_KEY ) );

		csvParams.setTextQualifier(
				params.getParamValue( TEXTQUALIF_KEY ) );

		csvParams.setStartingRow(
				params.getParamValueInteger( STARTINGROW_KEY ) );

		String colNameVal = params.getParamValue( COLNAME_KEY );
		if ( colNameVal != null ) {
			DefaultListModel model = new DefaultListModel();
			StringTokenizer st = new StringTokenizer( colNameVal, "@@" );
			while ( st.hasMoreTokens() ) {
				model.addElement( st.nextToken() );
			}
			csvParams.setColumnName( model );
		}

		csvParams.setRowName(
				params.getParamValue( ROWNAME_KEY, "row" ) );

		csvParams.setOther(
				params.getParamValue( OTHERVAL_KEY ) );
	}

	static final String TAB_KEY = "tab";
	static final String SEMICOL_KEY = "semicol";
	static final String COMMA_KEY = "comma";
	static final String SPACE_KEY = "space";
	static final String OTHER_KEY = "other";
	static final String TEXTQUALIF_KEY = "textq";
	static final String STARTINGROW_KEY = "startingrow";
	static final String COLNAME_KEY = "colname";
	static final String ROWNAME_KEY = "rowname";
	static final String OTHERVAL_KEY = "otherval";

}
