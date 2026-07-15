// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.xsl;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.toolkit.Toolkit;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class AssignCSSAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.toString().toLowerCase().endsWith( ".css" );
			}
			public String getDescription() {
				return "CSS (*.css)";
			}
		});

		if (chooser.showOpenDialog(EditixFrame.THIS)
			== JFileChooser.APPROVE_OPTION) {
			XMLPadDocument doc = container.getXMLDocument();
			int next = Math.max( 0, doc.nextTag( 0 ) - 1 );
			try {

				String href = null;
				if ( container.getCurrentDocumentLocation() != null ) {
					href = com.japisoft.framework.toolkit.Toolkit.getRelativePath(
							chooser.getSelectedFile(),
							new File( container.getCurrentDocumentLocation() ) );
				} else 
					href = Toolkit.toURL( chooser.getSelectedFile() ); 
								
				doc.insertString( next, "<?xml-stylesheet type=\"text/css\" href=\"" + href + "\"?>", null );
			} catch( BadLocationException exc ) {
			} catch( MalformedURLException exc ) {
			}
		}
	}

}
