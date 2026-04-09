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

package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

import net.sf.xframe.xsddoc.Processor;

import org.w3c.dom.*;

public class GenerateXSDDoc extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent arg0) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container.getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "Please save your Schema before" );
			return;
		}
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		// Generate the new schema
		panel.prepareToSave();
		
		File f = FileManager.getSelectedDirectory();

		if ( f != null ) {

			try {
		    	Processor p = new Processor();
		    	p.setDoctitle( "Documentation Generated at " + new Date() );
		    	p.setSchemaLocation( container.getCurrentDocumentLocation() );
		    	p.setOut( f.toString() );
		    	p.execute();
		    	
		    	BrowserCaller.displayURL( new File( f, "index.html" ).toString() );

			} catch( Exception exc ) {
				exc.printStackTrace();
				EditixFactory.buildAndShowErrorDialog( "Can't generate : " + exc.getMessage() );
			}
			
		}
		
	}

}

