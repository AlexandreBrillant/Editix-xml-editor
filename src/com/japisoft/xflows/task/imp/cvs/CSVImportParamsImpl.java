package com.japisoft.xflows.task.imp.cvs;

import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.japisoft.framework.xml.imp.CSVImportParams;
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
public class CSVImportParamsImpl implements CSVImportParams {

	private TaskParams params;
	
	public CSVImportParamsImpl(
			TaskParams params ) {
		this.params = params;
	}

	public ListModel getColumnName() {
		DefaultListModel model = new DefaultListModel();
		String colNameVal = params.getParamValue( CSVParametersDialog.COLNAME_KEY );
		if ( colNameVal != null ) {
			StringTokenizer st = new StringTokenizer( colNameVal, "@@" );
			while ( st.hasMoreTokens() ) {
				model.addElement( st.nextToken() );
			}
		}		
		return model;
	}

	public String getOther() {
		return params.getParamValue( CSVParametersDialog.OTHERVAL_KEY );
	}

	public String getRowName() {
		return params.getParamValue( CSVParametersDialog.ROWNAME_KEY );
	}

	public int getStartingRow() {
		return params.getParamValueInteger( CSVParametersDialog.STARTINGROW_KEY );
	}

	public String getTextQualifier() {
		return params.getParamValue( CSVParametersDialog.TEXTQUALIF_KEY );
	}

	public boolean isCommaSelected() {
		return params.getParamValueBoolean( CSVParametersDialog.COMMA_KEY );
	}

	public boolean isOtherSelected() {
		return params.getParamValueBoolean( CSVParametersDialog.OTHER_KEY );
	}

	public boolean isSemiColonSelected() {
		return params.getParamValueBoolean( CSVParametersDialog.SEMICOL_KEY );
	}

	public boolean isSpaceSelected() {
		return params.getParamValueBoolean( CSVParametersDialog.SPACE_KEY );
	}

	public boolean isTabSelected() {
		return params.getParamValueBoolean( CSVParametersDialog.TAB_KEY );
	}

}
