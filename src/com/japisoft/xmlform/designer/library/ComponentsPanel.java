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

