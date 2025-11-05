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

package com.japisoft.xmlform.designer.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.xmlform.designer.DesignerFrame;
import com.japisoft.xmlform.designer.XmlFormModel;

public abstract class CommonAction extends AbstractAction {

	protected DesignerFrame frame = null;

	public void actionPerformed(ActionEvent e) {
		frame = XmlFormModel.getDesignerFrame();
		try {
			actionPerformed2( e );
		} finally {
			frame = null;
		}
	}

	public abstract void actionPerformed2( ActionEvent e );
	
}

