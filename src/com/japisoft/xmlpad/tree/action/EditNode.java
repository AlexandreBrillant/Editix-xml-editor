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

package com.japisoft.xmlpad.tree.action;

import javax.swing.tree.TreePath;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.nodeeditor.EditorModel;

/**
 * Edit the current node using the <code>EditorModel</code>
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1
 */
public class EditNode extends AbstractTreeAction {

	public static final String ID = EditNode.class.getName();

	public EditNode() {
		super();
	}

	protected void notifyXMLContainer() {
		if ( container.getTree() != null ) {
			TreePath tp = container.getTree().getSelectionPath();
			if ( tp != null )
				setEnabled( EditorModel.accept( ( FPNode )tp.getLastPathComponent() ) );
		}
	}

	protected void actionForSelection( TreePath tp ) {
		container.editNode( ( FPNode )tp.getLastPathComponent() );
	}

	public String getDefaultLabel() {
		return "Edit node";
	}

}