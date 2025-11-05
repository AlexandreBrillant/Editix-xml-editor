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

package com.japisoft.framework.dockable.action.common;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.japisoft.framework.dockable.ComponentFactory;
import com.japisoft.framework.dockable.BasicInnerWindow;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.action.BasicAction;

/**
 * Action for maximizing or restoring an inner window
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class MaxMinAction extends BasicAction implements CommonAction {

	public static final String NAME = "MaxMin";

	public MaxMinAction() {
		putValue( 
			Action.SMALL_ICON, 
			new ImageIcon( MaxMinAction.class.getResource( "window.png" ) ) );
	}
	
	public void actionPerformed( ActionEvent e ) {
		getJDock().maximizedRestoredInnerWindow( innerWindow.getId() );
	}

}

