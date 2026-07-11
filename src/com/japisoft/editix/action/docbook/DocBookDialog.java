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

package com.japisoft.editix.action.docbook;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.xmlpad.XMLContainer;

/*
 "HTML",
 "HTML Help",
 "XHTML",
 "Java Help",
 "FO",
 "PDF",
 "XML",
 "PRINT",
 "PCL",
 "PS",
 "TXT",
 "SVG"
 */

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 2.0 */
public class DocBookDialog extends EditixDialog {

	private DocBookDialogPanel panel = null;
	
	public DocBookDialog() {
		super(
			"DocBook",
			"DocBook usage",
			"Transform your DocBook document to multiple outputs.Note that this is only for \"book\" document. Choose an output file. Store these parameters using a project for the next time." );
		panel = new DocBookDialogPanel();
		add( panel );
	}

	
	protected Dimension getDefaultSize() {
		return new Dimension( 350, 250 );
	}
	
	public void init( XMLContainer container ) {
		panel.init( container );
	}
	
	public void store( XMLContainer container ) {	
		panel.store( container );
	}

}	
