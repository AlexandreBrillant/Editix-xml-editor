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

package com.japisoft.editix.action.options;

import java.awt.event.ActionEvent;
import java.io.InputStream;

import javax.swing.AbstractAction;

import com.japisoft.editix.action.xsl.result.DocumentTypeModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.xmlpad.IXMLPanel;

public class TransformationDocumentModelAction extends AbstractAction {
	
	@Override
	public void actionPerformed( ActionEvent ae ) {
		InputStream input = DocumentTypeModel.instance().getModel();
		try {
			String content = FileToolkit.getContentFromInputStream(input, "UTF-8" );
			XMLFileData xdata = new XMLFileData( "UTF-8", content );
			IXMLPanel panel = EditixFactory.buildNewContainer(
				DocumentTypeModel.instance().getUserPath().toString(),
				xdata
			);
			panel.getMainContainer().setCurrentDocumentLocation( DocumentTypeModel.instance().getUserPath().toString() );
			EditixFrame.THIS.addContainer( panel );
		} catch( Throwable th ) {
			EditixFactory.buildAndShowErrorDialog( "Can't read : " + th.getMessage() );
		}		
	}
	
}

