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

package com.japisoft.xmlform.designer.library;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ComponentsPanel extends JPanel {

	public ComponentsPanel() {
		initUI();
	}

	private JList list = null;
	
	private void initUI() {
		setLayout( 
			new BorderLayout() );
		add( 
			new JScrollPane( 
					list = new JList() ) );
		
		list.setCellRenderer( new ComponentsRenderer() );
		
		DefaultListModel model = new DefaultListModel();
		for ( int i = 0; i < ComponentDescriptorModel.getComponentDescriptorCount(); i++ ) {
			model.addElement( 
					ComponentDescriptorModel.getComponentDescriptor( i ) );
		}

		list.setModel( model );
		new ListDragDrop( list );
	}

}
