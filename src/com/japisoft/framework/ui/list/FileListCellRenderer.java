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

package com.japisoft.framework.ui.list;

import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A renderer for a list of files
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class FileListCellRenderer implements ListCellRenderer {

	private JLabel lbl = new JLabel();
	
	public FileListCellRenderer() {
		lbl.setIcon(
				new ImageIcon(
						ClassLoader.getSystemResource( "images/document_plain.png" ) ) );
		lbl.setOpaque( true );
	}

	public Component getListCellRendererComponent(
			JList list, 
			Object value,
			int index, 
			boolean isSelected, 
			boolean cellHasFocus ) {
		
		if ( isSelected ) {
			lbl.setForeground( list.getSelectionForeground() );
			lbl.setBackground( list.getSelectionBackground() );
		} else {
			lbl.setForeground( list.getForeground() );
			lbl.setBackground( list.getBackground() );
		}
		
		File f = ( File )value;
		lbl.setText( f.toString() + " (" + f.length() + " bytes)" );

		return lbl;
	}

}
