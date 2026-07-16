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

package com.japisoft.framework.ui.stylededitor.ui;

import java.awt.Graphics;

import com.japisoft.framework.ui.stylededitor.EditorByCSS;
import com.japisoft.framework.ui.stylededitor.model.NodeElement;
import com.japisoft.framework.ui.stylededitor.ui.node.ElementView;
import com.japisoft.framework.ui.stylededitor.ui.node.ElementViewFactory;

public class ViewPartDocument implements ViewPart {

	public void paint(EditorByCSS editor, Graphics gc) {
		NodeElement root = ( NodeElement )editor.getDocument().getDefaultRootElement();
		ElementView view = ElementViewFactory.getInstance().getView( editor, root );
		view.paint( editor, root, 0, 0, gc );		
	}

}
