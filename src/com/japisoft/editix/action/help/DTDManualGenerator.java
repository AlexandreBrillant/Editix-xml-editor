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

package com.japisoft.editix.action.help;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;

import DTDDoc.DTDCommenter;
import DTDDoc.ExtendedDTD;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.basic.HTMLDialog;
import com.japisoft.framework.toolkit.FileToolkit;

public class DTDManualGenerator extends AbstractAction {

	public void actionPerformed(ActionEvent e) {

		File userHome = ApplicationModel.getAppUserPath();
		File target = new File( userHome, "html-dtd" );
		target.mkdir();
		String dtdLocation = (String)getValue( "param" );
		String dtdName = (String)getValue( "param2" );
		if (dtdLocation == null || dtdName == null) {
			EditixFactory
					.buildAndShowErrorDialog("Cannot find the DTD location !!");
			return;
		}

		File dtdLoc = new File(target, dtdName + ".dtd");
		File htmlLoc = new File(target, dtdName + ".html");

		if ( !htmlLoc.exists() ) {
			// Generate the documentation once
			if (!dtdLoc.exists()) {
				try {
					FileToolkit.copyFileFromURL(ClassLoader
							.getSystemResource(dtdLocation), dtdLoc);

					ExtendedDTD dtd = new ExtendedDTD(dtdLoc);

					DTDCommenter c = new DTDCommenter();
					c.createDocumentation( dtd, new PrintWriter(new FileWriter(
							htmlLoc)), false);

				} catch ( IOException e1 ) {
					EditixFactory
							.buildAndShowErrorDialog("Can't display the documentation : "
									+ e1.getMessage());
				}
			}
		}

		if ( htmlLoc.exists() ) {
			try {
				HTMLDialog d = new HTMLDialog( ApplicationModel.MAIN_FRAME,
						"Schema Documentation", htmlLoc.toURI().toURL()
								.toString(), false );
				d.setVisible( true );
			} catch ( MalformedURLException e1 ) {
			}

		} else {
			EditixFactory
					.buildAndShowErrorDialog( "Can't display the documentation for "
							+ dtdName );
		}

	}

}

