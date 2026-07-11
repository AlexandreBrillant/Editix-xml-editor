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

package com.japisoft.editix.action.template;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.xmlpad.action.toolkit.InsertAction;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class InsertParamAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		
		JPanel panel = new JPanel(); 
		JComboBox cb = new JComboBox(
				ParamModel.DEFAULT );
		cb.setEditable( false );
		panel.add( cb );

		if ( DialogManager.showDialog(
				EditixFrame.THIS,
				"Template Param",
				"Choose a template param",
				"Choose a template param. It will be used when creating a new document. Go to the preferences for setting the default values",
				null,
				panel,
				new Dimension( 300, 200 )
				 ) == DialogManager.OK_ID ) {
			InsertAction a = new InsertAction( "${" + ( String )cb.getSelectedItem() + "}" );
			a.setXMLContainer( EditixFrame.THIS.getSelectedContainer() );
			try {
				a.notifyAction();
			} finally {
				a.setXMLContainer( null );
			}
		}
	}	

}
