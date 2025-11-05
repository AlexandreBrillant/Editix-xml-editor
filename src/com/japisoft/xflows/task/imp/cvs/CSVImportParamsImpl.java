// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xflows.task.imp.cvs;

import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.japisoft.framework.xml.imp.CSVImportParams;
import com.japisoft.xflows.task.TaskParams;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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
