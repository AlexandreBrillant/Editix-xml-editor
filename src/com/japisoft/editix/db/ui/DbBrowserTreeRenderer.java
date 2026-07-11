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

package com.japisoft.editix.db.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.editix.db.ContainerNodeDb;
import com.japisoft.editix.db.FileNodeDb;
import com.japisoft.editix.db.RootNodeDb;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.framework.app.toolkit.Toolkit;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.xmlpad.XMLDocumentInfo;

public class DbBrowserTreeRenderer implements TreeCellRenderer {

	Icon connection = null;
	Icon collection = null;

	FastLabel label = new FastLabel();
	
	public DbBrowserTreeRenderer() {
		connection = Toolkit.getImageIcon( "images/data.png" );
		collection = Toolkit.getImageIcon( "images/data_table.png" );
		label.setOpaque( true );
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		if ( value instanceof DefaultMutableTreeNode ) {
			label.setText( value.toString() );
			label.setIcon( connection );
		} else
		if ( value instanceof RootNodeDb ) {
			label.setText( value.toString() );
			label.setIcon( connection );
		} else
		if ( value instanceof ContainerNodeDb ) {
			label.setText( value.toString() );
			label.setIcon( collection );
		} else
		if ( value instanceof FileNodeDb ) {
			String file = value.toString();
			label.setText( file );
			XMLDocumentInfo xdi = 
				DocumentModel.getDocumentByFileName( file );
			if ( xdi == null )
				label.setIcon( null );
			else
				label.setIcon( xdi.getDocumentIcon() );
		}
		
		if ( selected ) {
			
			label.setForeground(UIManager
					.getColor("List.selectionForeground"));
			label.setBackground(UIManager
					.getColor("List.selectionBackground"));			
			
		} else {
			
			label.setForeground(  
				tree.getForeground()	
			);
			label.setBackground(
					tree.getBackground() );
			
		}
		
		//label.setUnderlineMode( selected );
		
		return label;
	}

}
