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

package com.japisoft.xmlpad.tree.action;

import com.japisoft.xmlpad.action.ActionModel;

/**
 * Cut the current node
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class CutNode extends AbstractTreeAction {

	public static final String ID = CutNode.class.getName();
	
	public CutNode() {
		super(ActionModel.CUT_ACTION);
	}

	public boolean notifyAction() {
		boolean ok = ActionModel.activeActionByName(SelectNode.class.getName());
		if (ok) {
			container.getEditor().cut();
		}
		return ok;
	}

	protected String getDefaultLabel() {
		return "Cut text";	
	}
}

