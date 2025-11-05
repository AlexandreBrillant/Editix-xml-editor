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

package com.japisoft.editix.wizard.document;

import java.io.File;

import com.japisoft.editix.action.file.imp.spreadsheet.SpreadSheetImportAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.xml.XMLFileData;

public class XLSX2XMLDocument implements DocumentWizard {

	private File source;
	
	@Override
	public String start() {
		try {
			XMLFileData f = SpreadSheetImportAction.getXMLWizard( 
				new String[] { "xlsx" }, 
				new String[] { "Open XML - Spreadsheet (*.xlsx)" } 
			);
			source = new File( f.getURI() );
			return f.getContent();
		} catch( Exception exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't import this file : " + exc.getMessage() );
		}
		return null;
	}
	
	@Override
	public File getSource() {
		return source;
	}
	
}

