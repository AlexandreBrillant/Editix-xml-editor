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

package com.japisoft.xmlpad.action.edit;

import javax.swing.JOptionPane;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;

/**
 * Select the current tag
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class SelectTagAction extends XMLAction {

	public static final String ID = SelectTagAction.class.getName();
	
	protected boolean autoRequestFocus() { return false; }

	public boolean notifyAction() {
		if ( container.getTreeListeners() == null )
			return INVALID_ACTION;
		if ( container.getTree() == null )
			return INVALID_ACTION;
		if  ( container.getTree().getSelectionPath() == null )
			return INVALID_ACTION;
		FPNode n = ( FPNode )container.getTree().getSelectionPath().getLastPathComponent();
		if (n == null) {
			JOptionPane.showMessageDialog( container.getView(), "No Tag Found", "Error", JOptionPane.ERROR_MESSAGE );
			return INVALID_ACTION;
		}
		selectNode( container, n );
		return VALID_ACTION;		
	}
	
	public static void selectNode( 
			XMLContainer container, 
			FPNode n ) {
		if ( n == null )
			return;
		int start = n.getStartingOffset();
		int stop = n.getStoppingOffset();
		if ( n.isTag() )
			stop++;
		container.getEditor().requestFocus();
		container.getEditor().select( start, stop );		
	}

}
